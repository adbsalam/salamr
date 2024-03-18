package play

import actionExecutor.ActionExecutor
import actionExecutor.ActionExecutorImpl
import actionExecutor.SwipeAction.Custom
import core.DirManager
import core.Duration
import core.Logger.log
import core.data.RecordedEvents

/**
 * Play helps play recorded inputs by user
 */
class Play(
    private val dirManager: DirManager = DirManager(),
    private val actionExecutor: ActionExecutor = ActionExecutorImpl(),
) {

    /**
     * Collect saved coordinates from .salamr file
     * map to Coordinates data class and perform action for each
     */
    fun run(files: String?) {
        log("playing recorded inputs")
        if (files.isNullOrEmpty()) {
            val jsonString = dirManager.getRecordedJsonFileText()
            playbackInputs(eventsJson = jsonString)
        } else {
            val fileToPlay = files.split(",")
            fileToPlay.forEach { file ->
                val fileJson = dirManager.getRecordedInputFileText(file)
                if (fileJson.isNotEmpty()) {
                    log("playing recorded file $file")
                    playbackInputs(fileJson)
                }
            }
        }
    }

    /**
     * Plays back the recorded inputs stored in the provided JSON string.
     * @param eventsJson the JSON string containing recorded events.
     */
    private fun playbackInputs(eventsJson: String) {
        val eventsList = RecordedEvents.recordedEventAdapter.fromJson(eventsJson) ?: emptyList()
        eventsList.forEachIndexed { index, event ->
            log("playing recorded input ${index + 1}")
            if (event.tap != null) {
                actionExecutor.tap(event.tap.x, event.tap.y, Duration(1.0))
            } else if (event.swipe != null) {
                actionExecutor.swipe(
                    Custom(
                        startX = event.swipe.startX,
                        startY = event.swipe.startY,
                        endX = event.swipe.endX,
                        endY = event.swipe.endY,
                        duration = event.swipe.duration
                    )
                )
                actionExecutor.tap(10, 1400, Duration(1.0))
            }
        }
    }
}