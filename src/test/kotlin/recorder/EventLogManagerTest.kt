package recorder

import core.DirManager
import core.data.ScreenResolutions
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import readTextFileFromResources
import record.EventLogManager

class EventLogManagerTest {

    private val dirManager: DirManager = mockk()
    private val eventLogManager = EventLogManager(dirManager)

    private val expectedOutput =
        "[{\"swipe\":{\"startX\":599,\"startY\":1951,\"endX\":599,\"endY\":1315,\"duration\":145}},{\"tap\":{\"x\":57,\"y\":23}}]"

    @Test
    fun `when extractAndOutputEvents called, with valid data, correct events should be mapped and output`() {
        val eventLogs = readTextFileFromResources("adb/eventLog.txt").split("/n")
        val screenResolutions = ScreenResolutions(1440, 3320)

        every { dirManager.validateTempDir() }.answers { }
        every { dirManager.tempProjectDir }.answers { mockk() }
        every { dirManager.writeToFile(any()) }.answers { }
        eventLogManager.extractAndOutputEvents(eventLogs, screenResolutions)

        verify { dirManager.writeToFile(expectedOutput) }
    }
}