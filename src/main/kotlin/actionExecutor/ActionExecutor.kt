package actionExecutor

import core.Duration
import core.data.ScreenResolutions

interface ActionExecutor {

    companion object {
        val swipeInterceptEvent = SwipeAction.Custom(400, 400, 450, 400, 400)
    }

    fun getScreenResolutions(): ScreenResolutions

    fun recordEmulatorEvents(): List<String>

    fun createScreenDump()

    fun tap(x: Int, y: Int, actionDelay: Duration? = null)

    fun sendText(text: String, actionDelay: Duration? = null)

    fun sendKeyEvent(keyEvent: Int, actionDelay: Duration? = null)

    fun swipe(input: SwipeAction, actionDelay: Duration? = null)

    fun pointerLocation(toggle: Int)

    val systemExit: SystemExit
}

