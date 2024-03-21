package tracker

import actionExecutor.ActionExecutor
import actionExecutor.ActionExecutorImpl
import core.Logger.log
import record.EventLogManager

class Tracker(
    private val actionExecutor: ActionExecutor = ActionExecutorImpl(),
    private val eventLogManager: EventLogManager = EventLogManager()
) {

    fun run() {
        log("getting screen resolutions")
        val resolutions = actionExecutor.getScreenResolutions()

        log("recording emulator inputs, press any key to stop recording...")
        val eventList = actionExecutor.recordEmulatorEvents()

        val userInputs = eventLogManager.extractEvents(eventList, resolutions)

        log("processing inputs")

        if (userInputs.isEmpty()) {
            log("no events were recorded...")
            return
        }

        log("recorded inputs")
        userInputs.forEachIndexed { index, item ->
            println("   $index : $item")
        }
    }

}