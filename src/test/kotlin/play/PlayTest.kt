package play

import actionExecutor.ActionExecutor
import actionExecutor.SwipeAction
import core.Delay
import core.DirManager
import core.Duration
import core.fakes.recordedInputJsonFile
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PlayTest {

    private val actionExecutor: ActionExecutor = mockk()
    private val dirManager: DirManager = mockk()
    private val snapshotReportGenerator: SnapshotReportGenerator = mockk()
    private val snapshotManager: SnapshotManager = mockk()
    private val imageComparisonManager: ImageComparisonManager = mockk()
    private val play =
        Play(
            dirManager = dirManager,
            actionExecutor = actionExecutor,
            snapshotReportGenerator = snapshotReportGenerator,
            snapshotManager = snapshotManager,
            imageComparisonManager = imageComparisonManager
        )

    @BeforeEach
    fun setup() {
        every { dirManager.getRecordedJsonFileText() }.returns(recordedInputJsonFile)
        every { dirManager.getRecordedInputFileText(any()) }.returns(recordedInputJsonFile)
        every { actionExecutor.swipe(any(), any()) }.answers {}
        every { actionExecutor.tap(any(), any(), any()) }.answers { }
        every { dirManager.reportDir }.answers { mockk() }
        every { dirManager.reportDir.exists() }.answers { false }
        every { dirManager.snapshotDirectory.exists() }.answers { true }
        every { snapshotReportGenerator.generateHtmlFromReportFiles(any()) }.answers {  }
        mockkObject(Delay)
        every { Delay.ofSeconds(any()) }.answers { }
    }

    @Test
    fun `when play, and valid input and no file name`() {
        play.run(null, null)
        verify(exactly = 1) { actionExecutor.swipe(SwipeAction.Custom(599, 1951, 599, 1315, 145), null) }
        verify(exactly = 1) { actionExecutor.swipe(ActionExecutor.swipeInterceptEvent, Duration(0.5)) }
    }

    @Test
    fun `when play, and valid input and multiple file name`() {
        play.run(null, "first,second")
        verify(exactly = 2) { actionExecutor.swipe(SwipeAction.Custom(599, 1951, 599, 1315, 145), null) }
        verify(exactly = 2) { actionExecutor.swipe(ActionExecutor.swipeInterceptEvent, Duration(0.5)) }
    }
}

