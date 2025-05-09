package ios.actionExecutor

import core.Delay
import core.DirManager
import core.Duration
import core.Logger
import ios.core.IOSEventType
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.concurrent.thread
import kotlin.system.exitProcess

class IOSActionExecutorImpl(
    private val dirManager: DirManager = DirManager(),
) : IOSActionExecutor {
    override fun readLogs(): List<String> {
        val logs: MutableList<String> = mutableListOf()
        if (dirManager.iosLogsFile.exists()) {
            dirManager.iosLogsFile.deleteRecursively()
        }
        val command = arrayOf("bash", "-c", "log stream --predicate 'eventMessage contains \"salamr--\"' --info")
        val processBuilder = ProcessBuilder(*command)
        val process = processBuilder.start()
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        var stop = false
        thread {
            Logger.log("recording emulator inputs \uD83D\uDD34, press any key to stop recording...")
            System.`in`.read()
            stop = true
            process.destroy()
        }

        while (!stop) {
            val line = reader.readLine()
            if (line != null) {
                if (line.contains("salamr--") && line.contains("Phase: 3")) {
                    logs.add(line)
                    if (line.contains("ended")) {
                        Logger.log("Input received..")
                    }
                }
            }
        }
        process.waitFor()
        Logger.log("⏳ processing current input recording")
        return logs
    }

    override fun getSimulatorWindowInfo(): String {
        val appleScript =
            """
            tell application "System Events"
                tell process "Simulator"
                    try
                        -- Get the first window of the Simulator
                        set simWindow to first window
                        -- Get the position (x and y) of the window
                        set {xPos, yPos} to position of simWindow
                        -- Get the size (width and height) of the window
                        set {winWidth, winHeight} to size of simWindow
                        -- Return the position and size in a formatted string
                        return "Position: {" & xPos & ", " & yPos & "}, Size: {" & winWidth & ", " & winHeight & "}"
                    on error errMsg
                        return "Error: " & errMsg
                    end try
                end tell
            end tell
            """.trimIndent()

        try {
            val process = Runtime.getRuntime().exec(arrayOf("osascript", "-e", appleScript))
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val result = reader.readLine()

            if (result.startsWith("Error:")) {
                println("Error capturing Simulator window info")
                exitProcess(0)
            }
            return result
        } catch (e: Exception) {
            println("Error capturing Simulator window info")
            exitProcess(0)
        }
    }

    override fun executeSimulatorEvent(
        event: IOSEventType,
        delay: Double,
    ) {
        val command = arrayOf("cliclick", "c:${event.x},${event.y}")
        val processBuilder = ProcessBuilder(*command)
        val process = processBuilder.start()
        process.waitFor()
        Delay.ofSeconds(Duration(delay))
    }
}
