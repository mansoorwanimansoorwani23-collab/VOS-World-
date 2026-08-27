package com.example.vos.engine

import com.example.vos.data.VosRepository
import com.example.vos.data.model.GuestStorageItemEntity
import com.example.vos.data.model.VirtualDeviceEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.UUID

class GuestShellEngine(
    private val repository: VosRepository,
    private val device: VirtualDeviceEntity,
    private val buildPropsJson: String
) {
    private var currentDirectory = "/sdcard"

    suspend fun executeCommand(input: String): String = withContext(Dispatchers.IO) {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return@withContext ""

        val tokens = trimmed.split("\\s+".toRegex())
        val cmd = tokens[0].lowercase()
        val args = tokens.drop(1)

        repository.logGuest(device.id, "shell", "DEBUG", "$ $trimmed")

        when (cmd) {
            "help" -> {
                """
                VOS World Guest Shell (Android User-Space Sandboxed Runtime)
                Available commands:
                  uname -a       : Print guest kernel & architecture
                  getprop [prop] : Query system properties
                  setprop [k] [v]: Set system property
                  whoami         : Show current user identity
                  ls [-la] [dir] : List virtual directory contents
                  cd [dir]       : Change current directory
                  pwd            : Print current working directory
                  cat [file]     : Read file contents
                  echo [text]    : Print text (or redirect > file)
                  df -h          : Show virtual disk space
                  free -m        : Show virtual RAM statistics
                  ps / top       : List running virtual processes
                  logcat         : View recent guest system logs
                  uptime         : Show guest OS uptime
                  reboot         : Soft restart guest OS environment
                  clear          : Clear terminal screen
                """.trimIndent()
            }

            "whoami" -> "root (uid=0 gid=0 groups=0 context=u:r:su:s0)"

            "pwd" -> currentDirectory

            "uname" -> {
                val opt = args.firstOrNull()
                if (opt == "-a" || opt == "-r") {
                    "Linux localhost ${device.guestKernelVersion} #1 SMP PREEMPT aarch64 Android"
                } else {
                    "Linux"
                }
            }

            "uptime" -> {
                val upSec = device.uptimeSeconds + 120
                val min = upSec / 60
                val sec = upSec % 60
                "up $min min, $sec sec, load average: 0.18, 0.22, 0.15"
            }

            "df" -> {
                val used = (device.storageMb * 0.32).toInt()
                val free = device.storageMb - used
                """
                Filesystem               Size     Used    Avail   Use%  Mounted on
                /dev/block/bootdevice    ${device.storageMb}M   ${used}M   ${free}M    32%  /
                tmpfs                    ${device.ramMb}M     42M   ${device.ramMb - 42}M     2%  /dev
                /dev/block/by-name/system 2800M   1950M     850M    69%  /system
                /dev/block/by-name/vendor  850M    420M     430M    49%  /vendor
                /dev/block/by-name/userdata ${device.storageMb - 3650}M   ${used}M   ${free}M    28%  /data
                /data/media              ${device.storageMb - 3650}M   ${used}M   ${free}M    28%  /sdcard
                """.trimIndent()
            }

            "free" -> {
                val usedRam = (device.ramMb * 0.45).toInt()
                val freeRam = device.ramMb - usedRam
                """
                               total        used        free      shared     buffers
                Mem:           ${device.ramMb}M       ${usedRam}M       ${freeRam}M         12M         48M
                -/+ buffers/cache:       ${usedRam - 48}M       ${freeRam + 48}M
                Swap:             0M          0M          0M
                """.trimIndent()
            }

            "ps" -> {
                """
                USER     PID   PPID  VSIZE   RSS   WCHAN            PC  NAME
                root       1      0   9800  2100  do_epoll_wait      0  init
                root      42      1  14200  3800  poll_schedule      0  ueventd
                system   110      1  34200  8400  binder_thread_read 0  servicemanager
                system   185      1  98400 24800  binder_thread_read 0  surfaceflinger
                root     220      1 184500 48200  poll_schedule      0  zygote64
                system   350    220 540200 96200  binder_thread_read 0  system_server
                u0_a12   490    220 320100 68400  binder_thread_read 0  com.android.systemui
                u0_a24   610    220 280500 54100  binder_thread_read 0  com.android.launcher3
                root     890      1   4800  1800  0                  0  sh (current)
                """.trimIndent()
            }

            "top" -> {
                """
                User 8%, System 4%, IOW 0%, IRQ 0%
                User 24 + Nice 0 + Sys 12 + Idle 264 + IOW 0 + IRQ 0 = 300

                  PID PR CPU% S  #THR     VSS     RSS PCY UID      Name
                  350 20   6% S    84 540200K  96200K  fg system   system_server
                  185 20   3% S    24  98400K  24800K  fg system   surfaceflinger
                  490 20   2% S    42 320100K  68400K  fg u0_a12   com.android.systemui
                  890 20   1% R     1   4800K   1800K  fg root     top
                """.trimIndent()
            }

            "getprop" -> {
                try {
                    val json = JSONObject(buildPropsJson)
                    val key = args.firstOrNull()
                    if (key != null) {
                        json.optString(key, "")
                    } else {
                        val sb = StringBuilder()
                        json.keys().forEach { k ->
                            sb.append("[$k]: [${json.getString(k)}]\n")
                        }
                        sb.toString().trimEnd()
                    }
                } catch (e: Exception) {
                    "[ro.build.version.release]: [${device.guestAndroidVersion}]\n[ro.product.model]: [${device.name}]"
                }
            }

            "setprop" -> {
                if (args.size >= 2) {
                    val k = args[0]
                    val v = args.drop(1).joinToString(" ")
                    repository.logGuest(device.id, "setprop", "INFO", "Set property $k = $v")
                    "Property [$k] set to [$v]"
                } else {
                    "Usage: setprop [key] [value]"
                }
            }

            "cd" -> {
                val target = args.firstOrNull() ?: "/sdcard"
                currentDirectory = if (target.startsWith("/")) target else "$currentDirectory/$target".replace("//", "/")
                ""
            }

            "ls" -> {
                val path = if (args.isNotEmpty() && !args.last().startsWith("-")) args.last() else currentDirectory
                val resolved = if (path.startsWith("/")) path else "$currentDirectory/$path".replace("//", "/")
                
                when (resolved) {
                    "/" -> "acct  bin  boot  cache  config  d  data  default.prop  dev  etc  init  mnt  odm  oem  proc  product  sdcard  storage  sys  system  vendor"
                    "/system" -> "app  bin  build.prop  etc  fonts  framework  lib  lib64  media  priv-app  usr  xbin"
                    "/data" -> "app  backup  cache  dalvik-cache  data  local  media  misc  system  user"
                    else -> {
                        "Documents  Download  DCIM  Pictures  Music  Movies  Welcome.txt  README.md"
                    }
                }
            }

            "cat" -> {
                val file = args.firstOrNull()
                if (file == null) {
                    "Usage: cat [filename]"
                } else {
                    val resolved = if (file.startsWith("/")) file else "$currentDirectory/$file".replace("//", "/")
                    if (resolved.endsWith("build.prop")) {
                        "ro.build.version.release=${device.guestAndroidVersion}\nro.product.model=${device.name}\nro.product.cpu.abi=arm64-v8a\nro.secure=1\nro.debuggable=1"
                    } else if (resolved.endsWith("Welcome.txt")) {
                        "Welcome to VOS World Virtual OS!\nThis is an isolated persistent user-space guest environment running on your local device."
                    } else if (resolved.endsWith("README.md")) {
                        "# Virtual Storage\nAll files created here are persistent and stored locally on your device."
                    } else {
                        "cat: $file: No such file or directory"
                    }
                }
            }

            "echo" -> {
                val text = args.joinToString(" ")
                if (text.contains(">")) {
                    val parts = text.split(">")
                    val content = parts[0].trim().removeSurrounding("\"").removeSurrounding("'")
                    val filename = parts[1].trim()
                    val path = if (filename.startsWith("/")) filename else "$currentDirectory/$filename"
                    repository.insertGuestStorageItem(
                        GuestStorageItemEntity(
                            id = UUID.randomUUID().toString(),
                            deviceId = device.id,
                            path = path,
                            name = filename.substringAfterLast("/"),
                            isDirectory = false,
                            sizeBytes = content.toByteArray().size.toLong(),
                            contentText = content,
                            mimeType = "text/plain"
                        )
                    )
                    "Written to $filename"
                } else {
                    text
                }
            }

            "logcat" -> {
                """
                --------- beginning of main
                08-27 10:14:02.112  110   110 I servicemanager: Starting Android Service Manager
                08-27 10:14:02.340  185   185 I SurfaceFlinger: SurfaceFlinger is starting (vos_framebuffer: 1080x2400)
                08-27 10:14:02.890  220   220 I Zygote  : Preloading classes and resources...
                08-27 10:14:03.200  350   350 I SystemServer: Entered the Android system server!
                08-27 10:14:03.620  350   350 I ActivityManager: Starting System UI components
                08-27 10:14:04.100  490   490 I SystemUI: VOS World dynamic statusbar & notification shade ready
                --------- beginning of system
                08-27 10:14:04.450  610   610 I Launcher: Guest OS workspace loaded with 8 system packages
                """.trimIndent()
            }

            "reboot" -> {
                "Rebooting guest environment (SIGTERM sent to init PID 1)..."
            }

            else -> "$cmd: command not found (type 'help' for available commands)"
        }
    }
}
