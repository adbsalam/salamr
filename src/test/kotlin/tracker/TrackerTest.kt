package tracker

import actionExecutor.ActionExecutor
import core.DirManager
import core.fakes.mockEventLogs
import core.fakes.mockScreenResolutions
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import record.EventLogManager

class TrackerTest {

    private val dirManager: DirManager = mockk()
    private val eventLogManager = EventLogManager(dirManager)
    private val actionExecutor: ActionExecutor = mockk()
    private val tracker = Tracker(actionExecutor, eventLogManager)

    @BeforeEach
    fun setup() {
        every { dirManager.validateTempDir() }.answers { }
        every { dirManager.tempProjectDir }.answers { mockk() }
        every { dirManager.writeToFile(any(), any()) }.answers { }
        every { actionExecutor.getScreenResolutions() }.returns(mockScreenResolutions)
        every { actionExecutor.recordEmulatorEvents() }.returns(mockEventLogs)
    }

    @Test
    fun `when tracker is called, should track items correctly`() {
        tracker.run()
    }

}