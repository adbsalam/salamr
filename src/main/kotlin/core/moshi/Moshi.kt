package core.moshi

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

val moshi: Moshi
    get() = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
