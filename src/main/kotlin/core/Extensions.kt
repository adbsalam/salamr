package core

fun Array<String>.extractScreenshotArg() = this.getOrNull(1)?.let { arg ->
    SnapshotArgs.entries.firstOrNull { it.arg == arg }
}
