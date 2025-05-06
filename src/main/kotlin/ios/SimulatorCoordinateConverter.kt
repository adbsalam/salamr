package ios


/**
 * A utility class for converting iOS simulator coordinates to Mac screen coordinates.
 */
class SimulatorCoordinateConverter(
    private val screenResolution: Pair<Int, Int>,
    private val simulatorPosition: Pair<Int, Int>,
    private val simulatorSize: Pair<Int, Int>,
    private val xOffset: Int = 25,
    private val yOffset: Int = 75
) {
    /**
     * Extracts iOS coordinates from log lines and converts them to Mac screen coordinates.
     * @param logLines List of log lines containing iOS coordinates
     * @return List of pairs representing Mac screen coordinates (x, y)
     */
    fun processLogLines(logLines: List<String>): List<Pair<Int, Int>> {
        val iosCoordinates = extractCoordinatesFromLogs(logLines)
        return convertCoordinates(iosCoordinates)
    }

    /**
     * Converts a list of iOS simulator coordinates to Mac screen coordinates.
     * @param iosCoordinates List of pairs representing iOS simulator coordinates (x, y)
     * @return List of pairs representing Mac screen coordinates (x, y)
     */
    fun convertCoordinates(iosCoordinates: List<Pair<Float, Float>>): List<Pair<Int, Int>> {
        return iosCoordinates.map { (iosX, iosY) ->
            val macX = (simulatorPosition.first + iosX + xOffset).toInt()
            val macY = (simulatorPosition.second + iosY + yOffset).toInt()

            Pair(macX, macY)
        }
    }

    /**
     * Extracts iOS coordinates from log lines.
     * @param logLines List of log lines containing iOS coordinates
     * @return List of pairs representing iOS simulator coordinates (x, y)
     */
    fun extractCoordinatesFromLogs(logLines: List<String>): List<Pair<Float, Float>> {
        val coordinates = mutableListOf<Pair<Float, Float>>()
        val coordinatePattern = "x=([0-9.]+), y=([0-9.]+)".toRegex()

        // Only process "began at" lines to avoid duplicates
        val beganLines = logLines.filter { it.contains("began at") }

        for (line in beganLines) {
            val matchResult = coordinatePattern.find(line)
            if (matchResult != null) {
                val (x, y) = matchResult.destructured
                coordinates.add(Pair(x.toFloat(), y.toFloat()))
            }
        }

        return coordinates
    }

    /**
     * Returns a formatted string with Mac screen coordinates for direct copying.
     * @param macCoordinates List of pairs representing Mac screen coordinates (x, y)
     * @return A single line string with formatted coordinates
     */
    fun formatCoordinatesForCopying(macCoordinates: List<Pair<Int, Int>>): String {
        return macCoordinates.joinToString(", ") { (x, y) -> "($x, $y)" }
    }
}
