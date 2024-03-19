package record

import actionExecutor.ActionExecutor
import actionExecutor.ActionExecutorImpl
import core.Logger.log
import core.RecordOptions


/**
 * record helps record user inputs that cna be played
 */
class Record(
    private val actionExecutor: ActionExecutor = ActionExecutorImpl(),
    private val eventLogManager: EventLogManager = EventLogManager()
) {
    /**
     * Start task to start recording inputs
     * keep collecting until process is stopped
     * user can stop this process by simply pressing any keyboard key
     * once process stops, collect all event logs and start processing them
     * once refinement is done for data, store it as a json into .salamr/recorded.json file
     */
    fun run(args: Array<String>) {
        val fileName = getFileNameOrError(args)

        log("getting screen resolutions")
        val dimensions = actionExecutor.getScreenResolutions()

        log("recording emulator inputs, press any key to stop recording...")
        val eventList = actionExecutor.recordEmulatorEvents()

        log("processing current input recording")
        eventLogManager.extractAndOutputEvents(eventList, dimensions, fileName)
    }

    private fun getFileNameOrError(args: Array<String>): String? {
        return try {
            val option = args.getOrNull(1) ?: return null
            if (option.trim().isEmpty()) return null
            if (option.trim().contains(RecordOptions.File.arg)) {
                val fileName = args.getOrNull(2)
                if (fileName.isNullOrEmpty()) {
                    actionExecutor.systemExit.exitWithHelp("no file name given for -r -f, please see usage below")
                }
                fileName
            } else {
                actionExecutor.systemExit.exitWithHelp("invalid option usage, $option is not a valid -r option, please see usage below")
            }
        } catch (e: Exception) {
            actionExecutor.systemExit.exit()
        }
    }
}
