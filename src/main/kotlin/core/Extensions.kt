package core

fun Array<String>.extractScreenshotArg() =
    this.getOrNull(1)?.let { arg ->
        SnapshotArgs.entries.firstOrNull { it.arg == arg }
    }

fun Array<String>.extractScreenshotArgIos() =
    this.getOrNull(2)?.let { arg ->
        SnapshotArgs.entries.firstOrNull { it.arg == arg }
    }
