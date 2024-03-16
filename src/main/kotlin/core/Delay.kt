package core

object Delay {
    fun ofSeconds(seconds: Int) {
        val duration = seconds * 1000
        Thread.sleep(duration.toLong())
    }

    fun ofSeconds(seconds: Double) {
        val duration = seconds * 1000
        Thread.sleep(duration.toLong())
    }
}

