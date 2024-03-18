package core

import core.data.ScreenDimensions
import core.data.ScreenResolutions
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.regex.Pattern
import kotlin.concurrent.thread
import kotlin.system.exitProcess

/**
 * @param dirManager directory manager to get directories data
 */
class ADBProcess(
    private val dirManager: DirManager = DirManager()
) {
    /**
     * `adb shell uiautomator dump` to get adb emulator screen dump
     * `adb pull /sdcard/window_dump.xml <DIR_TO_COPY_INTO>` to copy files to user home/.salamr
     *
     * this process will collect emulator screen dump and copy file into user home/.salamr directory
     */
    fun startScreenDumpProcess() {
        val dumpProcess = ProcessBuilder("adb", "shell", "uiautomator", "dump").start()
        dumpProcess.waitFor()

        val pullProcess =
            ProcessBuilder("adb", "pull", "/sdcard/window_dump.xml", "${dirManager.tempProjectDir}").start()
        pullProcess.waitFor()
    }

    /**
     * `adb shell input keyevent KEYCODE_BACK` to replicate a back press on emulator
     */
    fun adbBackProcess() {
        println("key event back")
        val process = ProcessBuilder("adb", "shell", "input", "keyevent", "KEYCODE_BACK").start()
        process.waitFor()
    }

    /**
     * @param x X coordinate to tap on
     * @param y Y coordinate to tap on
     * `adb shell input keyevent KEYCODE_BACK` to replicate a back press on emulator
     *
     * this process will tap on x and y coordinates of emulator screen
     */
    fun adbTapProcess(x: Int, y: Int) {
        println("Tapping at coordinates: ($x, $y)")
        val process = ProcessBuilder("adb", "shell", "input", "tap", x.toString(), y.toString()).start()
        process.waitFor()
    }

    /**
     * `adb shell vm size` to get current screen size
     *
     * @return screen dimension
     */
    fun adbGetScreenDimensions(): ScreenDimensions {
        val process = ProcessBuilder("adb", "shell", "wm", "size").start()
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        val dimensions = reader.readLine()
        val screenSize = dimensions.split(" ")[2]
        val (widthStr, heightStr) = screenSize.split("x")
        return ScreenDimensions(
            width = widthStr.toInt(),
            height = heightStr.toInt()
        )
    }

    /**
     * @param startX startX to start swipe from
     * @param startY startY to start swipe from
     * @param endX endX to end swipe at
     * @param endY endY to end swipe at
     *
     * `adb shell input swipe startX startY endX endY`
     *
     * This process will perform a swipe on emulator screen
     */
    fun sendSwipeEvent(startX: Int, startY: Int, endX: Int, endY: Int, duration: Int? = null) {
        val process: Process
        if (duration == null) {
            process = ProcessBuilder("adb", "shell", "input", "swipe", "$startX", "$startY", "$endX", "$endY").start()
        } else {
            process = ProcessBuilder(
                "adb",
                "shell",
                "input",
                "swipe",
                "$startX",
                "$startY",
                "$endX",
                "$endY",
                "$duration"
            ).start()
        }
        process.waitFor()
    }

    /**
     * @param eventList events collected from adb event logs
     *
     * `adb shell getevent -lt` to get current events
     * -l list all devices
     * t add time stamp of each event
     *
     * This process will set up start recording process
     * this process doesn't end here as it needs to end when user presses a key
     * this is handled where process is used
     */
    fun adbEventListeningProcess(eventList: MutableList<String>): Process {
        return ProcessBuilder("adb", "shell", "getevent", "-lt").start().apply {
            thread {
                val reader = BufferedReader(InputStreamReader(inputStream))
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    line?.let {
                        eventList.add(it)
                        if (it.contains("ffffffff")) {
                            // tap release/finger up
                            //this is to stop any fling behaviour caused by action
                            adbTapProcess(10, 100)
                        }
                    }
                }
            }
        }
    }

    /**
     * `adb shell vm size` to get current screen resolutions
     *
     * @return screen resolutions
     */
    fun adbGetScreenResolutions(): ScreenResolutions {
        val process = ProcessBuilder("adb", "shell", "wm", "size").start()
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        val resolutionOutput = reader.readLine()
        reader.close()
        process.waitFor()

        val widthHeightRegex = Pattern.compile("(\\d+)x(\\d+)")
        val matcher = widthHeightRegex.matcher(resolutionOutput)
        return if (matcher.find()) {
            ScreenResolutions(
                width = matcher.group(1).toInt(),
                height = matcher.group(2).toInt()
            )
        } else {
            println("Failed to retrieve screen resolution.")
            exitProcess(1)
        }
    }
}
