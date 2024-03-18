package core.data

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Types
import core.moshi.moshi

@JsonClass(generateAdapter = true)
data class RecordedEvents(
    val tap: Tap? = null,
    val swipe: Swipe? = null
) {
    @JsonClass(generateAdapter = true)
    data class Tap(val x: Int, val y: Int)

    @JsonClass(generateAdapter = true)
    data class Swipe(val startX: Int, val startY: Int, val endX: Int, val endY: Int, val duration: Int?)

    companion object {
        val recordedEventAdapter: JsonAdapter<List<RecordedEvents>>
            get() {
                val type = Types.newParameterizedType(List::class.java, RecordedEvents::class.java)
                return moshi.adapter(type)
            }
    }
}