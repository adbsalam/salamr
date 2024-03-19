package core

enum class Options(val arg: String) {
    Help("-h"),
    Locate("-l"),
    Multi("-m"),
    Record("-r"),
    Play("-p"),
    Delete("-d")
}

enum class RecordOptions(val arg: String) {
    File("-f")
}