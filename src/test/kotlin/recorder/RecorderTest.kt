package recorder

import core.ADBProcess
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import record.EventLogManager
import record.Record

class RecorderTest {

    private val adbProcess: ADBProcess = mockk()
    private val eventLogManager: EventLogManager = mockk()
    private val record = Record(adbProcess, eventLogManager)

    @Test
    fun `when record is called, then adb process is called`() {
        val process = mockk<Process>()
        every { adbProcess.adbGetScreenResolutions() }.returns(mockk())
        every { adbProcess.adbEventListeningProcess(any()) }.returns(mockk())
        every { adbProcess.adbEventListeningProcess(any()) }.returns(process)
        every { eventLogManager.extractAndOutputEvents(any(), any()) }.returns(mockk())
        every { process.destroy() }.answers { }

        record.run()

        verify { adbProcess.adbGetScreenResolutions() }
        verify { adbProcess.adbEventListeningProcess(any()) }
        verify { eventLogManager.extractAndOutputEvents(any(), any()) }
    }
}