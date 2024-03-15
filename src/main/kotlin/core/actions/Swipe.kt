package core.actions

import core.ADBProcess

/**
 * @param adbProcess to execute adb processes
 *
 * get current screen size and perform swipe action
 * swipe is based on screen size, we will only swipe part of screen
 * -500 to avoid subtract system bar area for swipe
 */
class Swipe(
    private val adbProcess: ADBProcess = ADBProcess()
) {
    fun run() {
        val (screenWidth, screenHeight) = adbProcess.adbGetScreenDimensions()
        val startX = screenWidth / 2
        val startY = screenHeight - 500
        val endX = screenWidth / 2
        val endY = screenHeight / 4
        adbProcess.sendSwipeEvent(startX, startY, endX, endY)
    }
}