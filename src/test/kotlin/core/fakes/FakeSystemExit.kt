package core.fakes

import actionExecutor.SystemExit
import core.fakes.ExceptionType.EXIT_NO_HELP
import core.fakes.ExceptionType.EXIT_WITH_HELP

enum class ExceptionType(val msg: String) {
    EXIT_WITH_HELP("exit with help"),
    EXIT_NO_HELP("exit")
}

class FakeSystemExit : SystemExit {
    override fun exitWithHelp(text: String): Nothing {
        throw Throwable(EXIT_WITH_HELP.msg)
    }

    override fun exit(): Nothing {
        throw Throwable(EXIT_NO_HELP.msg)
    }
}