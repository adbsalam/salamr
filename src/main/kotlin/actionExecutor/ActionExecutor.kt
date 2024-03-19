package actionExecutor

import core.Duration
import core.data.ScreenResolutions

interface ActionExecutor {

    fun getScreenResolutions(): ScreenResolutions

    fun recordEmulatorEvents(): List<String>

    fun createScreenDump()

    fun tap(x: Int, y: Int, actionDelay: Duration? = null)

    fun sendText(text: String, actionDelay: Duration? = null)

    fun sendKeyEvent(keyEvent: KeyEvent, actionDelay: Duration? = null)

    fun swipe(input: SwipeAction, actionDelay: Duration? = null)

    val systemExit: SystemExit
}

