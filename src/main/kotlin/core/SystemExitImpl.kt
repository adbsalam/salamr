package core

import actionExecutor.SystemExit
import help.showHelp
import kotlin.system.exitProcess

class SystemExitImpl : SystemExit {
    override fun exitWithHelp(text: String): Nothing {
        showHelp(text)
        exitProcess(1)
    }

    override fun exit(): Nothing {
        exitProcess(1)
    }
}