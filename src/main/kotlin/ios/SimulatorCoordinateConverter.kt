package ios

import ios.core.IOSEventType
import ios.positionfinder.SimulatorWindowInfo

class SimulatorCoordinateConverter(
    private val simulatorWindowInfo: SimulatorWindowInfo = SimulatorWindowInfo(),
    private val xOffset: Int = 5,
    private val yOffset: Int = 5,
) {
    fun mapCoordinatesToSimulatorOffset(events: List<IOSEventType>): List<IOSEventType> {
        val simulatorPosition = simulatorWindowInfo.getSimulatorWindowInfo().position
        return events.map {
            IOSEventType(
                uuid = it.uuid,
                x = it.x + simulatorPosition.x + xOffset,
                y = it.y + simulatorPosition.y + yOffset,
            )
        }
    }
}
