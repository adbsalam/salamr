package recorder

import core.DirManager
import core.fakes.mockEventLogs
import core.fakes.mockScreenResolutions
import core.fakes.recordedInputJsonFile
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import record.EventLogManager

class EventLogManagerTest {

    private val dirManager: DirManager = mockk()
    private val eventLogManager = EventLogManager(dirManager)

    @BeforeEach
    fun setup() {
        every { dirManager.validateTempDir() }.answers { }
        every { dirManager.tempProjectDir }.answers { mockk() }
        every { dirManager.writeToFile(any(), any()) }.answers { }
    }

    @Test
    fun `when extractAndOutputEvents called, with valid data and no file name, correct events should be mapped and output`() {
        eventLogManager.extractAndOutputEvents(mockEventLogs, mockScreenResolutions, null)

        verify { dirManager.writeToFile(recordedInputJsonFile, null) }
    }

    @Test
    fun `when extractAndOutputEvents called, with valid data with file name, correct events should be mapped and output`() {
        eventLogManager.extractAndOutputEvents(mockEventLogs, mockScreenResolutions, "test")

        verify { dirManager.writeToFile(recordedInputJsonFile, eq("test")) }
    }
}