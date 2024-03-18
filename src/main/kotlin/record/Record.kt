package record

import core.ADBProcess
import core.Logger.log
import core.data.EventLogType

data class AdbLog(val type: EventLogType, val value: String, val time: Double? = null)


/**
 * record helps record user inputs that cna be played
 */
class Record(
    private val adbProcess: ADBProcess = ADBProcess(),
    private val eventLogManager: EventLogManager = EventLogManager()
) {
    /**
     * Start task to start recording inputs
     * keep collecting until process is stopped
     * user can stop this process by simply pressing any keyboard key
     * once process stops, collect all event logs and start processing them
     * once refinement is done for data, store it as a json into .salamr/recorded.json file
     */
    fun run() {
        log("getting screen resolutions")
        val dimensions = adbProcess.adbGetScreenResolutions()

        log("recording emulator inputs, press any key to stop recording...")
        val eventList = mutableListOf<String>()
        val process = adbProcess.adbEventListeningProcess(eventList)
        readlnOrNull()
        process.destroy()

        log("processing current input recording")
        eventLogManager.extractAndOutputEvents(eventList, dimensions)
    }
}