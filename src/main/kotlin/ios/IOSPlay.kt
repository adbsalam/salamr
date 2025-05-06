package ios

import core.DirManager
import core.Logger
import ios.actionExecutor.IOSActionExecutor
import ios.positionfinder.ScreenDimensionsInfo
import ios.positionfinder.SimulatorWindowInfo

class IOSPlay(
    private val dirManager: DirManager = DirManager()
) {

    fun run() {
        Logger.log("\uD83C\uDFAC playing recorded inputs")
        val lines = readLogsFromFile()
        val simulatorWindow = SimulatorWindowInfo().getSimulatorWindowInfo()
        val screenDimension = ScreenDimensionsInfo().getScreenResolution()

        val loggg = SimulatorCoordinateConverter(
            screenResolution = screenDimension ?: Pair(0, 0),
            simulatorPosition = simulatorWindow?.first ?: Pair(0, 0),
            simulatorSize = simulatorWindow?.second ?: Pair(0, 0)
        ).processLogLines(lines)

        IOSActionExecutor().executeCliClickCommands(loggg)
    }

    private fun readLogsFromFile(): List<String> {
        if (!dirManager.iosLogsFile.exists()) {
            println("Log file does not exist at: ${dirManager.iosLogsFile.absolutePath}")
            return emptyList()
        }

        return dirManager.iosLogsFile.readLines()
    }
}