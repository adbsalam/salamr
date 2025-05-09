package record

import java.util.UUID

interface UUIDGenerator {
    fun generate(): String
}

class DefaultUUIDGenerator : UUIDGenerator {
    override fun generate() = UUID.randomUUID().toString()
}
