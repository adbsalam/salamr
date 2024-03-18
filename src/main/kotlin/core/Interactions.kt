package core

enum class Interactions(val inputName: String) {
    SystemBack("B"),
    SwipeUp("SU"),
    SwipeDown("SD"),
    SwipeRight("SR"),
    SwipeLeft("SL"),
    DelayIn("D"),
    Other("")
}