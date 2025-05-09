package ios.record

import core.DirManager
import core.Logger
import ios.actionExecutor.IOSActionExecutor
import ios.actionExecutor.IOSActionExecutorImpl
import ios.core.IOSEventType
import java.util.UUID

class IOSRecord(
    private val dirManager: DirManager = DirManager(),
    private val iosActionExecutor: IOSActionExecutor = IOSActionExecutorImpl(),
) {
    fun run() {
        val logs = iosActionExecutor.readLogs()
        val iosEvents = convertLogsToIOSEvents(logs)
        writeToFile(iosEvents)
    }

    private fun convertLogsToIOSEvents(logs: List<String>): List<IOSEventType> {
        val recordedEvents = mutableListOf<IOSEventType>()
        val regex = """Location: ([\d.]+), ([\d.]+)""".toRegex()
        for (log in logs) {
            val matchResult = regex.find(log)
            matchResult?.let {
                recordedEvents.add(
                    IOSEventType(
                        uuid = UUID.randomUUID().toString(),
                        x = it.groupValues[1].toFloat().toInt(), // x coordinate
                        y = it.groupValues[2].toFloat().toInt(), // y coordinate
                    ),
                )
            }
        }
        return recordedEvents
    }

    private fun writeToFile(events: List<IOSEventType>) {
        if (!dirManager.iosLogsFile.exists()) {
            dirManager.iosLogsFile.createNewFile()
        } else {
            dirManager.iosLogsFile.deleteRecursively()
            dirManager.iosLogsFile.createNewFile()
        }

        if (events.isNotEmpty()) {
            Logger.log("saving current input recording..")
            val serializedEvents = IOSEventType.recordedEventAdapter.toJson(events)
            dirManager.writeToFile(serializedEvents, dirManager.iosLogsFile.name.replace(".json", ""))
        } else {
            Logger.log("No events found..")
        }
    }
}
