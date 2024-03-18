package actionExecutor

sealed class SwipeAction {
    data class Directional(val direction: Direction) : SwipeAction()
    data class Custom(val startX: Int, val startY: Int, val endX: Int, val endY: Int, val duration: Int?) :
        SwipeAction()
}