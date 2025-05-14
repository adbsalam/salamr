package play

import actionExecutor.ActionExecutor
import actionExecutor.ActionExecutorImpl
import actionExecutor.KeyEvent
import actionExecutor.SwipeAction.Custom
import core.DirManager
import core.Duration
import core.Logger.log
import core.SnapshotArgs
import core.data.RecordedEvents
import core.data.UserInput
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

/**
 * Play helps play recorded inputs by user
 */
class Play(
    private val dirManager: DirManager = DirManager(),
    private val actionExecutor: ActionExecutor = ActionExecutorImpl(),
    private val snapshotReportGenerator: SnapshotReportGenerator = SnapshotReportGenerator(),
    private val snapshotManager: SnapshotManager = SnapshotManager(),
    private val imageComparisonManager: ImageComparisonManager = ImageComparisonManager(),
) {
    private var failedSnapshots = mutableListOf<ReportFile>()

    /**
     * Collect saved coordinates from .salamr file
     * map to Coordinates data class and perform action for each
     */
    fun run(snapshotArg: SnapshotArgs?, files: String?) {
        log("\uD83C\uDFAC playing recorded inputs")
        if (files.isNullOrEmpty()) {
            val jsonString = dirManager.getRecordedJsonFileText()
            playbackInputs(eventsJson = jsonString, snapshotArg)
        } else {
            val fileToPlay = files.split(",")
            fileToPlay.forEach { file ->
                val fileJson = dirManager.getRecordedInputFileText(file)
                if (fileJson.isNotEmpty()) {
                    log("playing recorded file $file")
                    playbackInputs(fileJson, snapshotArg)
                }
            }
        }
    }

    /**
     * Plays back the recorded inputs stored in the provided JSON string.
     * @param eventsJson the JSON string containing recorded events.
     */
    private fun playbackInputs(eventsJson: String, snapshotArg: SnapshotArgs?) {
        if (dirManager.reportDir.exists()) {
            dirManager.reportDir.deleteRecursively()
            dirManager.reportDir.mkdir()
        }
        if (!dirManager.snapshotDirectory.exists()) {
            dirManager.snapshotDirectory.mkdir()
        }
        failedSnapshots = mutableListOf()
        val eventsList = RecordedEvents.recordedEventAdapter.fromJson(eventsJson) ?: emptyList()
        eventsList.forEachIndexed { index, event ->
            when {
                event.tap != null -> {
                    log("playing recorded input - type: TAP")
                    actionExecutor.tap(
                        x = event.tap.x, y = event.tap.y, actionDelay = Duration(1.0)
                    )
                    addSnapshotDelayIfRequired(snapshotArg)
                    performSnapshotActions(event.tap.uuid, snapshotArg)
                }

                event.swipe != null -> {
                    log("playing recorded input - type: SWIPE")
                    actionExecutor.swipe(
                        Custom(
                            startX = event.swipe.startX,
                            startY = event.swipe.startY,
                            endX = event.swipe.endX,
                            endY = event.swipe.endY,
                            duration = event.swipe.duration
                        )
                    )
                    actionExecutor.swipe(
                        input = ActionExecutor.swipeInterceptEvent, actionDelay = Duration(0.5)
                    )
                    addSnapshotDelayIfRequired(snapshotArg)
                    performSnapshotActions(event.swipe.uuid, snapshotArg)
                }

                event.keyboardEvent != null -> {
                    val keyboardKey = UserInput.KeyboardKey.entries.first { it.name == event.keyboardEvent.key }
                    if (keyboardKey == UserInput.KeyboardKey.KEY_S) {
                        log("playing recorded input - type: SNAPSHOT")
                        addSnapshotDelayIfRequired(snapshotArg)
                        performSnapshotActions(event.keyboardEvent.uuid, snapshotArg)
                    } else {
                        val keyCode = KeyEvent.getKeyCodeForEvent(keyboardKey)
                        log("playing recorded input - type: ${keyboardKey.name}")
                        actionExecutor.sendKeyEvent(keyEvent = keyCode, actionDelay = Duration(seconds = 0.5))

                    }
                }
            }
        }

        when (snapshotArg) {
            SnapshotArgs.Record -> log("snapshots recorded \uD83D\uDCF7")
            SnapshotArgs.Verify ->
                snapshotReportGenerator.generateHtmlFromReportFiles(failedSnapshots)

            else -> {}
        }
    }

    /**
     * Performs snapshot actions based on the provided snapshot arguments.
     *
     * @param eventUUID the UUID of the event for which the snapshot action is performed.
     * @param snapshotArgs the snapshot argument indicating the type of action.
     */
    private fun performSnapshotActions(eventUUID: String, snapshotArgs: SnapshotArgs?) {
        when (snapshotArgs) {
            SnapshotArgs.Record -> snapshotManager.takeScreenshot(eventUUID)
            SnapshotArgs.Verify -> try {
                imageComparisonManager.compareImage(eventUUID)?.let {
                    failedSnapshots.add(it)
                }
            } catch (e: Exception) {
                println("Something went wrong while image comparison")
            }

            else -> {}
        }
    }

    /**
     * Adds a delay before taking a snapshot if required.
     * @param snapshotArg the snapshot argument indicating the type of action.
     *
     * Delay is required to let pending animations end.
     */
    private fun addSnapshotDelayIfRequired(snapshotArg: SnapshotArgs?) {
        when (snapshotArg) {
            SnapshotArgs.Record,
            SnapshotArgs.Verify -> runBlocking { delay(500) }

            else -> {}
        }
    }

}