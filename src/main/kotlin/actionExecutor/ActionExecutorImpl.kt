package actionExecutor

import core.Duration
import core.SystemExitImpl

class ActionExecutorImpl(
    private val adbProcess: ADBProcess = ADBProcess(),
    private val sysExit: SystemExit = SystemExitImpl()
) : ActionExecutor {

    /**
     * Retrieves the screen resolutions using ADB.
     * @return a list of screen resolutions.
     */
    override fun getScreenResolutions() = adbProcess.adbGetScreenResolutions()

    /**
     * Starts recording emulator events using ADB.
     */
    override fun recordEmulatorEvents() = adbProcess.adbEventListeningProcess()

    /**
     * Creates a screen dump using ADB.
     */
    override fun createScreenDump() = adbProcess.startScreenDumpProcess()

    /**
     * Sends a tap event to specified coordinates on the screen.
     * @param x the x-coordinate.
     * @param y the y-coordinate.
     * @param actionDelay optional delay before performing the action.
     */
    override fun tap(x: Int, y: Int, actionDelay: Duration?) = adbProcess.adbTapProcess(x, y, actionDelay)

    /**
     * Sends text input to the device using ADB.
     * @param text the text to be sent.
     * @param actionDelay optional delay before performing the action.
     */
    override fun sendText(text: String, actionDelay: Duration?) {
        adbProcess.adbSendTextProcess(text, null)
    }

    /**
     * Sends a key event to the device using ADB.
     * @param keyEvent the key event to be sent.
     * @param actionDelay optional delay before performing the action.
     */
    override fun sendKeyEvent(keyEvent: KeyEvent, actionDelay: Duration?) {
        adbProcess.adbSendKeyEventBack(keyEvent, actionDelay)
    }

    /**
     * Performs a swipe action on the device screen.
     * @param input the swipe action to perform.
     * @param actionDelay optional delay before performing the action.
     */
    override fun swipe(input: SwipeAction, actionDelay: Duration?) {
        when (input) {
            is SwipeAction.Directional -> {
                val (screenWidth, screenHeight) = adbProcess.adbGetScreenDimensions()
                val startX = screenWidth / 2
                val startY = screenHeight / 2
                var endX = startX
                var endY = startY

                when (input.direction) {
                    Direction.UpToDown -> endY = startY + 800
                    Direction.DownToUp -> endY = startY - 800
                    Direction.LeftToRight -> endX = startX - 800
                    Direction.RightToLeft -> endX = startX + 800
                }
                adbProcess.sendSwipeEvent(startX, startY, endX, endY, actionDelay = actionDelay)
            }

            is SwipeAction.Custom ->
                adbProcess.sendSwipeEvent(
                    startX = input.startX,
                    startY = input.startY,
                    endX = input.endX,
                    endY = input.endY,
                    duration = input.duration,
                    actionDelay = actionDelay
                )
        }
    }

    /**
     * Gets the system exit instance.
     */
    override val systemExit: SystemExit
        get() = sysExit
}

