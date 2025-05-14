package actionExecutor

import core.data.UserInput

enum class KeyEvent(val input: Int) {
    Back(4),
    ForwardKey(111), //for keyboard hide
    KeyUp(19),
    KeyDown(20),
    KeyLeft(21),
    KeyRight(22);

    companion object {
        fun getKeyCodeForEvent(key: UserInput.KeyboardKey): Int {
            return when (key) {
                UserInput.KeyboardKey.KEY_UP -> KeyUp.input
                UserInput.KeyboardKey.KEY_DOWN -> KeyDown.input
                UserInput.KeyboardKey.KEY_LEFT -> KeyLeft.input
                UserInput.KeyboardKey.KEY_RIGHT -> KeyRight.input
                else -> 0
            }
        }
    }
}