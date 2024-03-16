package core

object Logger {
    private const val TAG = "## "

    fun log(message: String) {
        println("$TAG $message")
    }
}