package record

import core.ADBProcess
import core.DirManager
import core.Logger.log
import core.data.Coordinates
import core.data.EventLogType.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import java.io.File

/**
 * record helps record user inputs that cna be played
 */
class Record(
    private val adbProcess: ADBProcess = ADBProcess(),
    private val dirManager: DirManager = DirManager()
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

        val (width, height) = adbProcess.adbGetScreenResolutions()

        log("recording emulator inputs, press any key to stop recording...")
        val eventList = mutableListOf<String>()
        val process = adbProcess.adbEventListeningProcess(eventList)
        readlnOrNull()
        process.destroy()
        log("processing current input recording")
        val map = extractCoordinates(eventList, width, height)
        writeCoordinatesToFile(map)
    }

    /**
     * Extract coordinates from event logs
     * ABS_MT_TRACKING_ID is used as end of an event
     * once end of event is reached we collect x and y values from it.
     */
    private fun extractCoordinates(events: List<String>, width: Int, height: Int): List<Coordinates> {
        val coordinates = mutableListOf<Coordinates>()
        var x: Int? = null
        var y: Int? = null

        for (event in events) {
            val parts = event.trim().split("\\s+".toRegex())
            if (parts.size < 5) {
                continue
            }

            val valueType = parts[4]
            val value = parts[5]

            when (valueType) {
                ABS_MT_POSITION_X.name -> x = value.toInt(16)
                ABS_MT_POSITION_Y.name -> y = value.toInt(16)
                ABS_MT_TRACKING_ID.name -> {
                    if (value.isEndOfTouchEventValue()) {
                        if (x != null && y != null) {
                            val scaledX = (x * width) / 32767
                            val scaledY = (y * height) / 32767
                            coordinates.add(Coordinates(scaledX, scaledY))
                        }
                        x = null
                        y = null
                    }
                }
            }
        }
        return coordinates
    }

    /**
     * write stored coordinates into temp file
     * this will be stored as json so this can be deserialized into data class to play
     */
    private fun writeCoordinatesToFile(coordinates: List<Coordinates>) {
        if (coordinates.isEmpty()) {
            log("no recorded inputs found..")
            return
        }
        log("saving current input recording")
        dirManager.validateTempDir()
        val json = Json.encodeToJsonElement(coordinates)
        File(dirManager.coordinatesFile).writeText(json.toString())
        log("process complete. recorded ${coordinates.size} inputs")
    }

    private fun String.isEndOfTouchEventValue(): Boolean {
        return this == "00000000" || this == "ffffffff"
    }
}