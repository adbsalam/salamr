package ios.core

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Types
import core.moshi.moshi
import kotlinx.serialization.Serializable

@Serializable
data class IOSEventType(
    val uuid: String,
    val x: Int,
    val y: Int,
) {
    companion object {
        val recordedEventAdapter: JsonAdapter<List<IOSEventType>>
            get() {
                val type = Types.newParameterizedType(List::class.java, IOSEventType::class.java)
                return moshi.adapter(type)
            }
    }
}
