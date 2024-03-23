package pointer

import actionExecutor.ActionExecutor
import core.fakes.ExceptionType.EXIT_WITH_HELP
import core.fakes.FakeSystemExit
import core.validator.assertThrowsSystemExit
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PointerTest {
    private val actionExecutor: ActionExecutor = mockk()
    private val pointer = Pointer(actionExecutor)

    @BeforeEach
    fun setup() {
        every { actionExecutor.pointerLocation(any()) }.answers {}
        every { actionExecutor.systemExit }.returns(FakeSystemExit())
    }

    @Test
    fun `when pointer location is called, and toggle is on`() {
        pointer.run("on")

        verify { actionExecutor.pointerLocation(1) }
    }

    @Test
    fun `when pointer location is called, and toggle is off`() {
        pointer.run("off")

        verify { actionExecutor.pointerLocation(0) }
    }

    @Test
    fun `when pointer location is called, and toggle is invalid arg, not on or off`() {
        assertThrowsSystemExit(EXIT_WITH_HELP) {
            pointer.run("test")
        }
    }
}