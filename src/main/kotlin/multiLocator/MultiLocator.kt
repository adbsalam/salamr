package multiLocator

import core.ADBProcess
import core.Delay
import core.Logger
import core.Logger.log
import core.actions.Swipe
import locator.Locator

/**
 * multi locator helps performs actions on multiple elements as a sequence
 */
class MultiLocator(
    private val adbProcess: ADBProcess = ADBProcess(),
    private val locator: Locator = Locator(),
    private val swipe: Swipe = Swipe()
) {
    /**
     * args are a string list joint with ","
     * separate this list into list of strings
     * perform actions on each item as needed
     */
    fun run(args: String) {
        args.split(",").forEach {
            log("processing element: $it")
            when (it) {
                "B" -> {
                    Delay.ofSeconds(1)
                    adbProcess.adbBackProcess()
                    Delay.ofSeconds(1)
                }

                "S" -> {
                    Delay.ofSeconds(1)
                    swipe.run()
                    Delay.ofSeconds(0.5)
                }

                else -> locator.run(it)
            }
        }
    }
}