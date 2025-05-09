package core

enum class Options(
    val arg: String,
) {
    Help("-h"),
    Multi("-m"),
    Record("-r"),
    Play("-p"),
    Delete("-d"),
    Track("-t"),
    Pointer("-s"),
}

enum class IOSOptions(
    val arg: String,
) {
    Record("-r"),
    Play("-p"),
    ;

    companion object {
        const val IOS_ARG = "-ios"
    }
}

enum class SnapshotArgs(
    val arg: String,
) {
    Record("-record-snapshot"),
    Verify("-verify-snapshot"),
    None("-none"),
}

enum class RecordOptions(
    val arg: String,
) {
    File("-f"),
}
