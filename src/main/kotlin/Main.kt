import core.Options
import delete.Delete
import help.showHelp
import multiLocator.MultiLocator
import play.Play
import pointer.Pointer
import record.Record
import tracker.Tracker

/**
 * Main entry point for salamr
 * check and validate usage and args
 * user will not be able to use salamr unless correct args and options are provided
 */
fun main(args: Array<String>) {
    when {
        args.isEmpty() || args.isBlank() -> showHelp("No option passed for salamr. Read below information on usage:")
        args.isInvalidOption() -> showHelp("${args[0]} is an invalid option. Read below information on usage:")
        args.isInputSizeInvalid() -> showHelp("invalid usage, salamr only requires 1 arg. Read below information on usage:")
        else -> handleOption(args)
    }
}

/**
 * get args and run task based on user inputs
 */
private fun handleOption(
    args: Array<String>,
    multiLocator: MultiLocator = MultiLocator(),
    record: Record = Record(),
    play: Play = Play(),
    delete: Delete = Delete(),
    tracker: Tracker = Tracker(),
    pointer: Pointer = Pointer()
) {
    // option provided by user
    when (Options.entries.first { it.arg == args[0] }) {
        Options.Help -> showHelp(showElementSummary = true)
        Options.Multi -> multiLocator.run(args.getOrNull(1))
        Options.Record -> record.run(args)
        Options.Play -> play.run(args.getOrNull(1))
        Options.Delete -> delete.run(args.getOrNull(1))
        Options.Pointer -> pointer.run(args.getOrNull(1))
        Options.Track -> tracker.run()
    }
}

/**
 * user must have passed a valid option
 */
private fun Array<String>.isInvalidOption(): Boolean {
    return !Options.entries.any { it.arg == this[0] }
}

/**
 * size 2 is minimum as 1 option and 1 arg anything more is wrong
 */
private fun Array<String>.isInputSizeInvalid(): Boolean {
    return this.isEmpty()
}

/**
 * validate if args are null or empty
 */
private fun Array<String>.isBlank(): Boolean {
    val emptyArg = this[0].isEmpty() || this[0].isBlank()
    return this.isEmpty() || (this.size == 1 && emptyArg)
}