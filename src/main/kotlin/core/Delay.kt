package core

object Delay {

    fun ofSeconds(duration: Duration) {
        val delay = duration.seconds * 1000
        Thread.sleep(delay.toLong())
    }
}

data class Duration(val seconds: Double)

