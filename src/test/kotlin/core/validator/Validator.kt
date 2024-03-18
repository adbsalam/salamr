package core.validator

import core.fakes.ExceptionType
import kotlin.test.assertEquals

fun assertThrowsSystemExit(type: ExceptionType, action: () -> Unit) {
    val exception = try {
        action()
        null
    } catch (e: Throwable) {
        e
    }
    assertEquals(exception?.message, type.msg)
}