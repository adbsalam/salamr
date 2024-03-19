package core

import core.data.EventLogType

data class AdbLog(val type: EventLogType, val value: String, val time: Double? = null)