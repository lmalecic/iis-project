package com.lmalecic.iis.client.ui.util

import com.jakewharton.mosaic.ui.Color

fun List<Color>.colorAt(fraction: Float): Color {
    require(this.isNotEmpty()) {
        "Gradient must contain at least one color"
    }

    if (this.size == 1) {
        return first()
    }

    val t = fraction.coerceIn(0f, 1f)
    val scaled = t * (this.size - 1)

    val startIndex = scaled.toInt().coerceAtMost(lastIndex - 1)
    val endIndex = startIndex + 1
    val localFraction = scaled - startIndex

    return this[startIndex].lerp(this[endIndex], localFraction)
}

fun Color.lerp(other: Color, fraction: Float): Color {
    val t = fraction.coerceIn(0f, 1f)

    return Color(
        this.component1() + (other.component1() - this.component1()) * t,
        this.component2() + (other.component2() - this.component2()) * t,
        this.component3() + (other.component3() - this.component3()) * t,
    )
}