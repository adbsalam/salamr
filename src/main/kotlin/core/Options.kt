package core

enum class Options(val arg: String) {
    Help("-h"),
    Multi("-m"),
    Record("-r"),
    Play("-p"),
    Delete("-d"),
    Track("-t"),
    Pointer("-s")
}

enum class RecordOptions(val arg: String) {
    File("-f")
}