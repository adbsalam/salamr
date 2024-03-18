package actionExecutor

interface SystemExit {
    fun exitWithHelp(text: String): Nothing

    fun exit(): Nothing
}