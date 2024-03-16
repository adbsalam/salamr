package play

import core.ADBProcess
import core.DirManager
import core.Logger
import core.Logger.log
import core.data.Coordinates
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.io.File

/**
 * Play helps play recorded inputs by user
 */
class Play(
    private val dirManager: DirManager = DirManager(),
    private val adbProcess: ADBProcess = ADBProcess()
) {

    /**
     * Collect saved coordinates from .salamr file
     * map to Coordinates data class and perform action for each
     */
    fun run() {
        log("playing recorded inputs")
        val jsonString = File(dirManager.coordinatesFile).readText()
        val coordinatesList = Json.decodeFromString(ListSerializer(Coordinates.serializer()), jsonString)
        coordinatesList.forEachIndexed { index, coordinates ->
            log("playing recorded input ${index + 1}")
            adbProcess.startScreenDumpProcess()
            adbProcess.adbTapProcess(coordinates.x, coordinates.y)
        }
    }
}