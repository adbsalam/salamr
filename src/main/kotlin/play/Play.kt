package play

import core.ADBProcess
import core.Delay
import core.DirManager
import core.Logger.log
import core.data.RecordedEvents

/**
 * Play helps play recorded inputs by user
 */
class Play(
    private val dirManager: DirManager = DirManager(),
    private val adbProcess: ADBProcess = ADBProcess(),
) {

    /**
     * Collect saved coordinates from .salamr file
     * map to Coordinates data class and perform action for each
     */
    fun run() {
        log("playing recorded inputs")
        val jsonString = dirManager.getRecordedJsonFileText()
        val eventsList = RecordedEvents.recordedEventAdapter.fromJson(jsonString) ?: emptyList()

        eventsList.forEachIndexed { index, event ->
            log("playing recorded input ${index + 1}")
            if (event.tap != null) {
                adbProcess.adbTapProcess(event.tap.x, event.tap.y)
                Delay.ofSeconds(1)
            } else if (event.swipe != null) {
                adbProcess.sendSwipeEvent(
                    startX = event.swipe.startX,
                    startY = event.swipe.startY,
                    endX = event.swipe.endX,
                    endY = event.swipe.endY,
                    duration = event.swipe.duration
                )
                adbProcess.adbTapProcess(10, 1400)
                Delay.ofSeconds(1)
            }
        }
    }
}