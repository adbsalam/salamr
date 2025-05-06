package ios.actionExecutor

import core.Logger
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

class IOSActionExecutor {
    fun executeCliClickCommands(
        coordinates: List<Pair<Int, Int>>,
        clickType: String = "c",
        delayBetweenClicks: Long = 1000
    ): List<String> {
        val results = mutableListOf<String>()
        var index = 1
        for (coord in coordinates) {
            index++
            val (x, y) = coord
            val command = "cliclick $clickType:$x,$y"

            try {
                Logger.log("playing recorded input ${index + 1}")
                val process = Runtime.getRuntime().exec(command)
                val reader = BufferedReader(InputStreamReader(process.inputStream))

                var line: String?
                val output = StringBuilder()

                while (reader.readLine().also { line = it } != null) {
                    output.append(line).append("\n")
                }

                process.waitFor()
                if (delayBetweenClicks > 0) {
                    TimeUnit.MILLISECONDS.sleep(delayBetweenClicks)
                }
            } catch (e: Exception) {
                Logger.log("Failed to play recording")
            }
        }

        return results
    }

}