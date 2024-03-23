package multi

import actionExecutor.*
import core.Delay
import core.Duration
import core.fakes.ExceptionType
import core.fakes.FakeSystemExit
import core.validator.assertThrowsSystemExit
import io.mockk.*
import locator.Locator
import multiLocator.MultiLocator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MultiLocatorTest {

    private val locator: Locator = mockk()
    private val actionExecutor: ActionExecutor = mockk()
    private val multiLocator = MultiLocator(locator, actionExecutor)

    @BeforeEach
    fun setup() {
        every { actionExecutor.tap(any(), any(), any()) }.answers { }
        every { actionExecutor.sendText(any(), any()) }.answers { }
        every { actionExecutor.sendKeyEvent(any(), any()) }.answers { }
        every { actionExecutor.swipe(any(), any()) }.answers { }
        every { actionExecutor.systemExit }.returns(FakeSystemExit())
        every { locator.run(any()) }.answers { }
        mockkObject(Delay)
        every { Delay.ofSeconds(any()) }.answers {  }
    }

    @Test
    fun `when system back is passed, should execute system back correctly`() {
        multiLocator.run("B")
        verify(exactly = 1) { actionExecutor.sendKeyEvent(keyEvent = KeyEvent.Back.input) }
    }

    @Test
    fun `when swipe down is passed, should execute system back correctly`() {
        multiLocator.run("SD")
        verify(exactly = 1) { actionExecutor.swipe(SwipeAction.Directional(direction = Direction.UpToDown)) }

        multiLocator.run("SD(100,100,300,500)")
        verify(exactly = 1) {
            actionExecutor.swipe(
                actionDelay = Duration(1.0),
                input = SwipeAction.Custom(
                    startY = 100,
                    startX = 100,
                    endX = 100,
                    endY = 400,
                    duration = 500
                )
            )
        }
    }

    @Test
    fun `when swipe up is passed, should execute system back correctly`() {
        multiLocator.run("SU")
        verify(exactly = 1) { actionExecutor.swipe(SwipeAction.Directional(direction = Direction.DownToUp)) }

        multiLocator.run("SU(100,100,300,500)")
        verify(exactly = 1) {
            actionExecutor.swipe(
                actionDelay = Duration(1.0),
                input = SwipeAction.Custom(
                    startY = 100,
                    startX = 100,
                    endX = 100,
                    endY = -200,
                    duration = 500
                )
            )
        }
    }

    @Test
    fun `when swipe right is passed, should execute system back correctly`() {
        multiLocator.run("SR")
        verify(exactly = 1) { actionExecutor.swipe(SwipeAction.Directional(direction = Direction.RightToLeft)) }

        multiLocator.run("SR(100,100,300,500)")
        verify(exactly = 1) {
            actionExecutor.swipe(
                actionDelay = Duration(1.0),
                input = SwipeAction.Custom(
                    startY = 100,
                    startX = 100,
                    endX = 400,
                    endY = 100,
                    duration = 500
                )
            )
        }
    }

    @Test
    fun `when swipeleft is passed, should execute system back correctly`() {
        multiLocator.run("SL")
        verify(exactly = 1) { actionExecutor.swipe(SwipeAction.Directional(direction = Direction.LeftToRight)) }

        multiLocator.run("SL(100,100,300,500)")
        verify(exactly = 1) {
            actionExecutor.swipe(
                actionDelay = Duration(1.0),
                input = SwipeAction.Custom(
                    startY = 100,
                    startX = 100,
                    endX = -200,
                    endY = 100,
                    duration = 500
                )
            )
        }
    }

    @Test
    fun `when invalid swipe inputs, should exit system with help`() {
        assertThrowsSystemExit(ExceptionType.EXIT_WITH_HELP) {
            multiLocator.run("SU(100)") // 2 inputs missing
        }

        assertThrowsSystemExit(ExceptionType.EXIT_WITH_HELP) {
            multiLocator.run("SU(100,1)") // 1 inputs missing
        }
    }

    @Test
    fun `when any other element is found, run locator correctly`() {
        multiLocator.run("test")
        verify(exactly = 1) { locator.run("test") }
    }

    @Test
    fun `when multiple elements are sent, should trigger all inputs correctly`() {
        multiLocator.run("SU|SD|@adb_salam|B|TF[0](text here)|SR|SL|C(100,200)|K(11,22)")

        verifyOrder {
            actionExecutor.swipe(SwipeAction.Directional(direction = Direction.DownToUp))
            actionExecutor.swipe(SwipeAction.Directional(direction = Direction.UpToDown))
            locator.run("@adb_salam")
            actionExecutor.sendKeyEvent(keyEvent = KeyEvent.Back.input)
            locator.run("TF[0](text here)")
            actionExecutor.swipe(SwipeAction.Directional(direction = Direction.RightToLeft))
            actionExecutor.swipe(SwipeAction.Directional(direction = Direction.LeftToRight))
            actionExecutor.tap(100, 200)
            actionExecutor.sendKeyEvent(11)
            actionExecutor.sendKeyEvent(22)
        }
    }

    @Test
    fun `when input is T with correct x and y, should perform a tap`() {
        multiLocator.run("C(11,200)")
        verify { actionExecutor.tap(11, 200) }
    }

    @Test
    fun `when input is T with missing y or x, should exit with help`() {
        assertThrowsSystemExit(ExceptionType.EXIT_WITH_HELP) {
            multiLocator.run("C(11)")
        }

        assertThrowsSystemExit(ExceptionType.EXIT_WITH_HELP) {
            multiLocator.run("C(,1)")
        }
        assertThrowsSystemExit(ExceptionType.EXIT_WITH_HELP) {
            multiLocator.run("C")
        }
    }

    @Test
    fun `when input is K keycode, should perform key events correctly`() {
        multiLocator.run("K(11,200)")
        verify(exactly = 1) { actionExecutor.sendKeyEvent(11) }
        verify(exactly = 1) { actionExecutor.sendKeyEvent(200) }
    }

}

