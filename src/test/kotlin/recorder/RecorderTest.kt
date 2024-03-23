package recorder

import actionExecutor.ActionExecutor
import core.Delay
import core.fakes.*
import core.fakes.ExceptionType.EXIT_WITH_HELP
import core.validator.assertThrowsSystemExit
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import record.EventLogManager
import record.Record
import kotlin.test.assertEquals

class RecorderTest {

    private val eventLogManager: EventLogManager = mockk()
    private val actionExecutor: ActionExecutor = mockk()
    private val record = Record(actionExecutor, eventLogManager)

    @BeforeEach
    fun setup() {
        every { actionExecutor.getScreenResolutions() }.returns(mockScreenResolutions)
        every { actionExecutor.recordEmulatorEvents() }.returns(mockEventLogs)
        every { actionExecutor.systemExit }.returns(FakeSystemExit())
        mockkObject(Delay)
        every { Delay.ofSeconds(any()) }.answers {  }
    }

    @Test
    fun `when record is called, when no file name, then eventLogManager called without file name`() {
        every { eventLogManager.extractAndOutputEvents(any(), any(), null) }.returns(mockk())

        record.run(emptyArray())

        verify { eventLogManager.extractAndOutputEvents(eq(mockEventLogs), eq(mockScreenResolutions), null) }
    }

    @Test
    fun `when record is called, when contains file name, then eventLogManager is called with file name`() {
        every { eventLogManager.extractAndOutputEvents(any(), any(), any()) }.returns(mockk())

        record.run(arrayOf("-r", "-f", "test"))

        verify { eventLogManager.extractAndOutputEvents(any(), eq(mockScreenResolutions), eq("test")) }
    }

    @Test
    fun `when record is called, with file name and wrong option, should exit process with help`() {
        every { eventLogManager.extractAndOutputEvents(any(), any(), null) }.returns(mockk())

        assertThrowsSystemExit(EXIT_WITH_HELP) {
            record.run(arrayOf("-r", "-k", "test"))
        }
    }

    @Test
    fun `when record is called, with file name and no -f option, should exit process with help`() {
        every { eventLogManager.extractAndOutputEvents(any(), any(), null) }.returns(mockk())

        assertThrowsSystemExit(EXIT_WITH_HELP) {
            record.run(arrayOf("-r", "test"))
        }
    }

}

