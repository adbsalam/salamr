package ios.record

import core.DirManager
import core.Logger
import java.io.BufferedReader
import java.io.File
import java.io.FileWriter
import java.io.InputStreamReader
import kotlin.concurrent.thread

class IOSRecord(
    private val dirManager: DirManager = DirManager()
) {
    private val logs = mutableListOf<String>()

    fun run() {
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

        // Continuously read the logs
        while (!stop) {
            val line = reader.readLine()
            if (line != null) {
                if (line.contains("salamr--")) {
                    logs.add(line)
                    if (line.contains("ended")) {
                        Logger.log("Input received..")
                    }
                }
            }
        }
        process.waitFor()
        Logger.log("⏳ processing current input recording")
        writeLogsToFile()
    }

    private fun writeLogsToFile() {
        if (!dirManager.iosLogsFile.exists()) {
            dirManager.iosLogsFile.createNewFile()
        }

        if (logs.isNotEmpty()) {
            Logger.log("saving current input recording..")
        } else {
            Logger.log("No events found..")
        }
        FileWriter(dirManager.iosLogsFile).use { writer ->
            logs.forEach { log ->
                writer.write("$log\n")
            }
            Logger.log("Recording saved.")
        }
    }
}