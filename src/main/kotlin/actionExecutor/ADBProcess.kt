package actionExecutor

import core.Delay
import core.DirManager
import core.Duration
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
     * @param x X coordinate to tap on
     * @param y Y coordinate to tap on
     * `adb shell input keyevent KEYCODE_BACK` to replicate a back press on emulator
     *
     * this process will tap on x and y coordinates of emulator screen
     */
    fun adbTapProcess(x: Int, y: Int, actionDelay: Duration?) {
        val process = ProcessBuilder("adb", "shell", "input", "tap", x.toString(), y.toString()).start()
        process.waitFor()
        actionDelay?.let { Delay.ofSeconds(actionDelay) }
    }

    fun adbSendTextProcess(text: String, actionDelay: Duration?) {
        val process = ProcessBuilder("adb", "shell", "input", "text", "'$text'").start()
        process.waitFor()
        actionDelay?.let { Delay.ofSeconds(actionDelay) }
    }

    fun adbSendKeyEventBack(keyEvent: KeyEvent, actionDelay: Duration?) {
        val process = ProcessBuilder("adb", "shell", "input", "keyevent", "${keyEvent.input}").start()
        process.waitFor()
        actionDelay?.let { Delay.ofSeconds(actionDelay) }
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
    fun sendSwipeEvent(startX: Int, startY: Int, endX: Int, endY: Int, duration: Int? = null, actionDelay: Duration?) {
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
        actionDelay?.let { Delay.ofSeconds(actionDelay) }
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
    fun adbEventListeningProcess(): List<String> {
        val eventList = mutableListOf<String>()
        val process = ProcessBuilder("adb", "shell", "getevent", "-lt").start()
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        var line: String?

        val userInputThread = thread {
            // Wait for user input to stop the process
            readLine()
            process.destroy()
        }

        while (reader.readLine().also { line = it } != null) {
            line?.let {
                eventList.add(it)
                if (it.contains("ffffffff")) {
                    // tap release/finger up
                    // This is to stop any fling behavior caused by the action
                    adbTapProcess(10, 100, null)
                }
            }
        }

        userInputThread.join() // Wait for the user input thread to finish
        return eventList
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
