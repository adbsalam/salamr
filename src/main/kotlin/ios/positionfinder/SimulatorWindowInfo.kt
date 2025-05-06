package ios.positionfinder

import java.io.BufferedReader
import java.io.InputStreamReader

class SimulatorWindowInfo {
    /**
     * Fetches the position and size of the iOS Simulator window using AppleScript.
     *
     * @return A Pair containing:
     *         - First: A Pair of (x, y) coordinates representing the position of the Simulator window
     *         - Second: A Pair of (width, height) values representing the size of the Simulator window
     *         Or null if an error occurs
     */
    fun getSimulatorWindowInfo(): Pair<Pair<Int, Int>, Pair<Int, Int>>? {
        val appleScript = """
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
                println(result)
                return null
            }

            // Parse the result string to extract position and size values
            // Expected format: "Position: {1245, 101}, Size: {446, 949}"
            val regex = """Position: \{(\d+), (\d+)\}, Size: \{(\d+), (\d+)\}""".toRegex()
            val matchResult = regex.find(result)

            if (matchResult != null) {
                val (x, y, width, height) = matchResult.destructured
                return Pair(
                    Pair(x.toInt(), y.toInt()),
                    Pair(width.toInt(), height.toInt())
                )
            } else {
                println("Failed to parse result: $result")
                return null
            }
        } catch (e: Exception) {
            println("Error executing AppleScript: ${e.message}")
            return null
        }
    }
}