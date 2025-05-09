package ios.positionfinder

import java.io.BufferedReader
import java.io.InputStreamReader

class ScreenDimensionsInfo {
    fun getDisplay0Resolution(): Pair<Int, Int>? {
        try {
            val process =
                ProcessBuilder("screenresolution", "get")
                    .redirectErrorStream(true)
                    .start()

            val output = BufferedReader(InputStreamReader(process.inputStream)).use { it.readText() }
            process.waitFor()

            val regex = Regex("""Display 0:\s+(\d+)x(\d+)x\d+""")
            val match = regex.find(output)

            if (match != null) {
                val (width, height) = match.destructured
                return Pair(width.toInt(), height.toInt())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}
