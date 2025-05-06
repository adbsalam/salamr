package ios.positionfinder

import java.io.BufferedReader
import java.io.InputStreamReader

class ScreenDimensionsInfo {

    /**
     * Fetches the screen resolution of the main display on macOS.
     *
     * @return A Pair of (width, height) representing the screen resolution, or null if an error occurs
     */
    fun getScreenResolution(): Pair<Int, Int>? {
        try {
            val process = Runtime.getRuntime().exec(arrayOf("system_profiler", "SPDisplaysDataType"))
            val reader = BufferedReader(InputStreamReader(process.inputStream))

            var line: String?
            val resolutionRegex = """Resolution: (\d+) x (\d+)""".toRegex()

            while (reader.readLine().also { line = it } != null) {
                val matchResult = resolutionRegex.find(line ?: "")
                if (matchResult != null) {
                    val (width, height) = matchResult.destructured
                    return Pair(width.toInt(), height.toInt())
                }
            }

            println("No resolution information found")
            return null
        } catch (e: Exception) {
            println("Error getting screen resolution: ${e.message}")
            return null
        }
    }
}