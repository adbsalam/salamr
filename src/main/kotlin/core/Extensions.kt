package core

fun Array<String>.extractScreenshotArg(): SnapshotArgs? {
   val entry = this.firstOrNull { arg ->
        val entry = SnapshotArgs.entries.firstOrNull { it.arg == arg }
        entry != null
    }

    return if (entry != null) {
        SnapshotArgs.entries.first { it.arg == entry }
    } else {
        null
    }
}
