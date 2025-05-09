package ios.positionfinder

import ios.actionExecutor.IOSActionExecutor
import ios.actionExecutor.IOSActionExecutorImpl
import kotlin.system.exitProcess

class SimulatorWindowInfo(
    private val ioSimulator: IOSActionExecutor = IOSActionExecutorImpl(),
) {
    fun getSimulatorWindowInfo(): SimulatorWindow {
        val simulatorWindowInfo = ioSimulator.getSimulatorWindowInfo()
        val regex = """Position: \{(\d+), (\d+)\}, Size: \{(\d+), (\d+)\}""".toRegex()
        val matchResult = regex.find(simulatorWindowInfo)

        if (matchResult != null) {
            val (x, y, width, height) = matchResult.destructured
            return SimulatorWindow(
                position = SimulatorWindow.WindowCoordinates(x.toInt(), y.toInt()),
                size = SimulatorWindow.WindowSize(width.toInt(), height.toInt()),
            )
        } else {
            println("Cannot find simulator window info, please make sure the simulator is running.")
            exitProcess(0)
        }
    }
}

data class SimulatorWindow(
    val position: WindowCoordinates,
    val size: WindowSize,
) {
    data class WindowCoordinates(
        val x: Int,
        val y: Int,
    )

    data class WindowSize(
        val width: Int,
        val height: Int,
    )
}
