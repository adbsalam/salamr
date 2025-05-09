package ios.actionExecutor

import ios.core.IOSEventType

interface IOSActionExecutor {
    fun readLogs(): List<String>

    fun getSimulatorWindowInfo(): String

    fun executeSimulatorEvent(
        event: IOSEventType,
        delay: Double = 1.0,
    )
}
