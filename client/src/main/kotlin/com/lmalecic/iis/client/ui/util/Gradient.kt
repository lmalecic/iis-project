package com.lmalecic.iis.client.ui.util

import com.jakewharton.mosaic.ui.Color
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

data class Gradient(
    var colors: List<Color>,
    var angle: Float = 0f,
) {
    init {
        require(this.colors.isNotEmpty()) {
            "Gradients must have at least one color"
        }
    }

    fun colorAt(
        x: Int,
        y: Int,
        width: Int,
        height: Int,
    ): Color {
        if (this.colors.size == 1) {
            return this.colors.first()
        }

        val normalizedX = if (width <= 1) 0f else x.toFloat() / (width - 1)
        val normalizedY = if (height <= 1) 0f else y.toFloat() / (height - 1)
        val radians = this.angle * PI.toFloat() / 180f

        val directionX = cos(radians)
        val directionY = sin(radians)

        val projections = floatArrayOf(
            0f,
            directionX,
            directionY,
            directionX + directionY
        )

        val minProjection = projections.min()
        val maxProjection = projections.max()
        val projection = normalizedX * directionX + normalizedY * directionY

        val fraction = if (maxProjection == minProjection) 0f else (projection - minProjection) / (maxProjection - minProjection)

        return colors.colorAt(fraction)
    }
}
