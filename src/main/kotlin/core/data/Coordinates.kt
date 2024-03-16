package core.data

import kotlinx.serialization.Serializable

/**
 * @param x X coordinate of element
 * @param y Y coordinate of element
 *
 * used for serialization and storing coordinates data
 */
@Serializable
data class Coordinates(
    val x: Int,
    val y: Int
)