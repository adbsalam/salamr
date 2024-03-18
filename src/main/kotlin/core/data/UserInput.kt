package core.data

sealed class UserInput {
    data class Tap(val x: Int, val y: Int) : UserInput()
    data class Swipe(val startX: Int, val startY: Int, val endX: Int, val endY: Int, val duration: Int?) : UserInput()
}