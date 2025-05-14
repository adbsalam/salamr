package core.data

sealed class UserInput {
    data class Tap(val x: Int, val y: Int) : UserInput()
    data class Swipe(val startX: Int, val startY: Int, val endX: Int, val endY: Int, val duration: Int?) : UserInput()
    data class KeyBoardEvent(val key: KeyboardKey) : UserInput()

    enum class KeyboardKey(val rawValue: String) {
        KEY_UP("EY_UP"),
        KEY_DOWN("EY_DOWN"),
        KEY_LEFT("EY_LEFT"),
        KEY_RIGHT("EY_RIGHT"),
        KEY_S("KEY_S");

        companion object {
            fun containsKeyboardKey(input: String): Boolean {
                return KeyboardKey.entries.firstOrNull { input.contains(it.rawValue) } != null
            }

            fun getKeyForInput(input: String): KeyboardKey? {
                return KeyboardKey.entries.firstOrNull { input.contains(it.rawValue) }
            }
        }
    }
}

