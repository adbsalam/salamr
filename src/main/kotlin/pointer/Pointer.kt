package pointer

import actionExecutor.ActionExecutor
import actionExecutor.ActionExecutorImpl
import core.Logger
import core.Options
import java.util.*

class Pointer(
    private val actionExecutor: ActionExecutor = ActionExecutorImpl(),
) {

    /**
     * Executes an action based on the input string.
     * If the input is "on" (case-insensitive), it enables pointer location.
     * If the input is "off" (case-insensitive), it disables pointer location.
     *
     * @param input The input string to process.
     */
    fun run(input: String?) {

        if (input.isNullOrEmpty()) {
            actionExecutor.systemExit.exitWithHelp("invalid value for ${Options.Pointer.arg}")
        }

        val trimmedInput = input.trim()
        when (trimmedInput.lowercase(Locale.getDefault())) {
            "on" -> {
                actionExecutor.pointerLocation(1)
                Logger.log("pointer locations turned on..")
            }

            "off" -> {
                actionExecutor.pointerLocation(0)
                Logger.log("pointer locations turned off..")
            }

            else -> actionExecutor.systemExit.exitWithHelp("invalid value for ${Options.Pointer.arg}")
        }
    }
}