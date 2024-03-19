package locator

import actionExecutor.ActionExecutor
import actionExecutor.KeyEvent
import core.DirManager
import core.Duration
import core.fakes.ExceptionType
import core.fakes.FakeSystemExit
import core.fakes.FakeXmlParser
import core.validator.assertThrowsSystemExit
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


class LocatorTest {

    private val dirManager: DirManager = mockk()
    private val actionExecutor: ActionExecutor = mockk()
    private val xmlParser: XmlParser = FakeXmlParser() // creates adb/dump.xml as document
    private val locator = Locator(dirManager, actionExecutor, xmlParser)

    @BeforeEach
    fun setup() {
        every { dirManager.validateTempDir() }.answers { }
        every { dirManager.dumpFileLocation }.returns("")
        every { actionExecutor.createScreenDump() }.answers { }
        every { actionExecutor.tap(any(), any(), any()) }.answers { }
        every { actionExecutor.sendText(any(), any()) }.answers { }
        every { actionExecutor.sendKeyEvent(any(), any()) }.answers { }
        every { actionExecutor.systemExit }.returns(FakeSystemExit())
    }


    @Test
    fun `when element is requested without index, and element exists on screen, find and tap element`() {
        locator.run("@adb_salam")

        verify(exactly = 1) { actionExecutor.tap(920, 1148, null) }
    }

    @Test
    fun `when element is requested with index, and element exists on screen, find and tap element`() {
        locator.run("@adb_salam[0]")

        verify(exactly = 1) { actionExecutor.tap(920, 1148, null) }
    }

    @Test
    fun `when element is requested with invalid index, and element does not exist on screen, find and tap element`() {
        assertThrowsSystemExit(ExceptionType.EXIT_WITH_HELP) {
            locator.run("@adb_salam[1]")
        }
    }

    @Test
    fun `when element exists twice, calling element with index, find and tap correct element`() {
        locator.run("Videos[0]")
        verify(exactly = 1) { actionExecutor.tap(897, 1625, null) }

        locator.run("Videos[1]")
        verify(exactly = 1) { actionExecutor.tap(903, 2953, null) }
    }

    @Test
    fun `when element is text field TF and a text is passed, and element exists on screen, find and tap element`() {
        locator.run("TF[0](test)")

        verify(exactly = 1) { actionExecutor.sendText("test", Duration(1.0)) }
        verify(exactly = 1) { actionExecutor.sendKeyEvent(keyEvent = KeyEvent.ForwardKey) }
    }

    @Test
    fun `when element is text field TF no idex and a text is passed, and element exists on screen, should exit process`() {
        assertThrowsSystemExit(ExceptionType.EXIT_WITH_HELP) {
            locator.run("TF(test)")
        }
    }

    @Test
    fun `when element is text field TF no idex and no text passed, and element exists on screen, should exit process`() {
        assertThrowsSystemExit(ExceptionType.EXIT_WITH_HELP) {
            locator.run("TF")
        }
    }

}