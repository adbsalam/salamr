package record

import core.AdbLog
import core.DirManager
import core.Logger
import core.data.EventLogType
import core.data.RecordedEvents
import core.data.ScreenResolutions
import core.data.UserInput

class EventLogManager(private val dirManager: DirManager = DirManager()) {

    private lateinit var screenResolutions: ScreenResolutions
    private var fileName: String? = null

    /**
     * Extracts and outputs events based on the provided input logs.
     *
     * @param input The list of input logs.
     * @param screenRes The screen resolutions.
     */
    fun extractAndOutputEvents(input: List<String>, screenRes: ScreenResolutions, file: String?) {
        screenResolutions = screenRes
        fileName = file
        val eventLogsByTypes = splitInputLogsByType(input.joinToString("\n"))
        val eventsList = eventLogsByTypes.mapNotNull { getUserInput(it) }
        writeCoordinatesToFile(eventsList)
    }

    /**
     * Extracts user input events from a list of input strings based on the provided screen dimensions.
     * Each input string represents a user input event log.
     *
     * @param input The list of input strings representing user input event logs.
     * @param screenRes Current emulator screen resolutions
     * @return A list of UserInput objects representing the extracted user input events.
     */
    fun extractEvents(input: List<String>, screenRes: ScreenResolutions): List<UserInput> {
        screenResolutions = screenRes
        val eventLogsByTypes = splitInputLogsByType(input.joinToString("\n"))
        return eventLogsByTypes.mapNotNull { getUserInput(it) }
    }

    /**
     * Splits the input logs by chunks delineated by lines containing following 2 patterns.
     * Touch start / finger press ABS_MT_TRACKING_ID   00000000
     * Touch end / finger removed ABS_MT_TRACKING_ID   ffffffff
     *
     * @param input The input string containing logs to be split.
     * @return A list of lists where each inner list represents a chunk of logs.
     */
    private fun splitInputLogsByType(input: String): List<List<String>> {
        val chunks = mutableListOf<List<String>>()
        val lines = input.trim().split("\n")
        val chunkLines = mutableListOf<String>()
        var isInChunk = false

        for (line in lines) {
            if (line.contains("ABS_MT_TRACKING_ID   00000000")) {
                isInChunk = true
            }

            if (isInChunk) {
                chunkLines.add(line)
            }

            if (line.contains("ABS_MT_TRACKING_ID   ffffffff")) {
                isInChunk = false
                chunks.add(chunkLines.toList())
                chunkLines.clear()
            }
        }

        return chunks
    }

    /**
     * Parses a list of event logs to determine the corresponding user input.
     *
     * @param list The list of event logs to be parsed.
     * @return A UserInput object representing the parsed user input, or null if input is invalid or ambiguous.
     */
    private fun getUserInput(list: List<String>): UserInput? {
        val isMultipleX = list.filter { it.contains(EventLogType.ABS_MT_POSITION_X.name) }.size > 1
        val isMultipleY = list.filter { it.contains(EventLogType.ABS_MT_POSITION_Y.name) }.size > 1

        if (!isMultipleX && !isMultipleY) {
            val logX = list.firstOrNull { it.contains(EventLogType.ABS_MT_POSITION_X.name) }
            val logY = list.firstOrNull { it.contains(EventLogType.ABS_MT_POSITION_Y.name) }
            if (logX != null && logY != null) {
                return getTapEvent(logX, logY)
            }
        } else {
            return getSwipeEvent(list)
        }

        return null
    }

    /**
     * Constructs a tap event based on the provided X and Y position logs.
     *
     * @param logX The event log representing the X position.
     * @param logY The event log representing the Y position.
     * @return A Tap event object with scaled coordinates, or null if the coordinates are invalid.
     */
    private fun getTapEvent(logX: String, logY: String): UserInput.Tap? {
        val x = convertToAdbLog(logX)?.value?.toInt(16) ?: 0
        val y = convertToAdbLog(logY)?.value?.toInt(16) ?: 0

        val scaledX = (x * screenResolutions.width) / 32767
        val scaledY = (y * screenResolutions.height) / 32767

        if (scaledX != 0 && scaledY != 0) {
            return UserInput.Tap(scaledX, scaledY)
        }
        return null
    }

    /**
     * Constructs a swipe event based on the provided list of logs.
     * important
     * duration is calculated from first touch down to last x or y in list
     * scaling of pixels is needed, as adb logs come in as highest pixels scale, this needs to be converts
     * to current screen resolutions
     *
     * @param logs The list of logs representing the swipe event.
     * @return A Swipe event object with scaled coordinates and duration, or null if the coordinates are invalid.
     */
    private fun getSwipeEvent(logs: List<String>): UserInput.Swipe? {

        infix fun Int.scaleTo(screenRes: Int) = (this * screenRes) / 32767

        val logsList = logs.map { convertToAdbLog(it) }

        val startX = logsList.firstOrNull { it?.type == EventLogType.ABS_MT_POSITION_X }?.value?.toInt(16) ?: 0
        val startY = logsList.firstOrNull { it?.type == EventLogType.ABS_MT_POSITION_Y }?.value?.toInt(16) ?: 0
        val endX = logsList.lastOrNull { it?.type == EventLogType.ABS_MT_POSITION_X }?.value?.toInt(16) ?: 0
        val endY = logsList.lastOrNull { it?.type == EventLogType.ABS_MT_POSITION_Y }?.value?.toInt(16) ?: 0

        val scaledX = startX scaleTo screenResolutions.width
        val scaledY = startY scaleTo screenResolutions.height
        val scaledEndX = endX scaleTo screenResolutions.width
        val scaledEndY = endY scaleTo screenResolutions.height

        val firstLog =
            logsList.firstOrNull { it?.type == EventLogType.ABS_MT_POSITION_X || it?.type == EventLogType.ABS_MT_POSITION_Y }

        val lastLog =
            logsList.lastOrNull { it?.type == EventLogType.ABS_MT_POSITION_X || it?.type == EventLogType.ABS_MT_POSITION_Y }

        val duration = (lastLog?.time ?: 0.0) - (firstLog?.time ?: 0.0)
        val durationMillis = if (duration == 0.0) null else ((duration * 1000).toInt())

        if (scaledX != 0 && scaledY != 0 && scaledEndX != 0 && scaledEndY != 0) {
            return UserInput.Swipe(scaledX, scaledY, scaledEndX, scaledEndY, durationMillis?.coerceAtLeast(50))
        }
        return null
    }

    /**
     * Converts a string representation of an event log to an AdbLog object.
     *
     * @param eventLogs The string representing the event log.
     * @return An AdbLog object representing the event log, or null if the conversion fails.
     */
    private fun convertToAdbLog(eventLogs: String): AdbLog? {
        val line = removeExtraSpaces(eventLogs).replace("[ ", "[")
        val parts = line.split(" ")
        if (parts.size >= 5) {
            val time = parts[0].removePrefix("[").removeSuffix("]").toDoubleOrNull()
            val code = parts[3]
            val value = parts[4]
            if (value.isNotEmpty() && code.isNotEmpty()) {
                val eventLogType = EventLogType.entries.firstOrNull { it.name == code } ?: EventLogType.OTHER
                return AdbLog(eventLogType, value, time)
            }
        }
        return null
    }

    /**
     * @param input adb event logs inout
     *
     * @return remove all extra spaces from string as an event log looks like below
     * input [  135065.485653] /dev/input/event1: EV_SYN       SYN_REPORT           00000000
     * output [135065.485653] /dev/input/event1: EV_SYN SYN_REPORT 00000000
     * leaving 1 space between blocks
     */
    private fun removeExtraSpaces(input: String): String {
        return input.replace(Regex("\\s+"), " ")
    }

    /**
     * write stored coordinates into temp file
     * this will be stored as json so this can be deserialized into data class to play
     */
    private fun writeCoordinatesToFile(userInputs: List<UserInput>) {
        if (userInputs.isEmpty()) {
            Logger.log("no recorded inputs found..")
            return
        }
        Logger.log("saving current input recording")
        dirManager.validateTempDir()

        val listOfRecordedEvents: List<RecordedEvents> = userInputs.map {
            when (it) {
                is UserInput.Tap -> RecordedEvents(tap = RecordedEvents.Tap(it.x, it.y))
                is UserInput.Swipe -> RecordedEvents(
                    swipe = RecordedEvents.Swipe(
                        startX = it.startX,
                        startY = it.startY,
                        endX = it.endX,
                        endY = it.endY,
                        duration = it.duration
                    )
                )
            }
        }
        val eventsList = RecordedEvents.recordedEventAdapter.toJson(listOfRecordedEvents)
        Logger.log("process complete. recorded ${userInputs.size} inputs")
        dirManager.writeToFile(eventsList.toString(), fileName)
    }
}