package tracker

import actionExecutor.ActionExecutor
import core.Delay
import core.DirManager
import core.fakes.mockEventLogs
import core.fakes.mockScreenResolutions
import core.fakes.mockUserInputList
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import record.EventLogManager

class TrackerTest {

    private val eventLogManager: EventLogManager = mockk()
    private val actionExecutor: ActionExecutor = mockk()
    private val tracker = Tracker(actionExecutor, eventLogManager)

    @BeforeEach
    fun setup() {
        every { eventLogManager.extractEvents(any(), any()) }.returns(mockUserInputList)
        every { actionExecutor.getScreenResolutions() }.returns(mockScreenResolutions)
        every { actionExecutor.recordEmulatorEvents() }.returns(mockEventLogs)
        mockkObject(Delay)
        every { Delay.ofSeconds(any()) }.answers { }
    }

    @Test
    fun `when tracker is called, should track items correctly`() {
        tracker.run()

        verify { actionExecutor.getScreenResolutions() }
        verify { actionExecutor.recordEmulatorEvents() }
        verify { eventLogManager.extractEvents(mockEventLogs, mockScreenResolutions) }
    }

}