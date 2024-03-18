package play

import core.ADBProcess
import core.DirManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class PlayTest {

    private val dirManager: DirManager = mockk()
    private val adbProcess: ADBProcess = mockk()
    private val play = Play(dirManager, adbProcess)

    private val recordedJsonFile =
        "[{\"swipe\":{\"startX\":599,\"startY\":1951,\"endX\":599,\"endY\":1315,\"duration\":145}},{\"tap\":{\"x\":57,\"y\":23}}]"

    @Test
    fun `when play, and valid input exists`() {
        every { dirManager.getRecordedJsonFileText() }.returns(recordedJsonFile)
        every { adbProcess.sendSwipeEvent(any(), any(), any(), any(), any()) }.answers {}
        every { adbProcess.adbTapProcess(any(), any()) }.answers { }

        play.run()

        verify(atMost = 1) { adbProcess.sendSwipeEvent(599, 1951, 599, 1315, 145) }
        verify(atMost = 1) { adbProcess.adbTapProcess(10, 1400) }
    }
}

