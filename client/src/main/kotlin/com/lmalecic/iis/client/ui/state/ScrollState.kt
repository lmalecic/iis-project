package com.lmalecic.iis.client.ui.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.jakewharton.mosaic.layout.Remeasurement

@Stable
class ScrollState {
    internal var remeasurement: Remeasurement? = null

    var value by mutableIntStateOf(0)
        private set

    var maxValue by mutableIntStateOf(0)
        private set

    var viewportSize by mutableIntStateOf(0)
        private set

    fun scrollTo(position: Int): Boolean {
        val next = position.coerceIn(0, maxValue)
        if (next == value) {
            return false
        }

        value = next
        return true
    }

    fun scrollBy(rows: Int): Boolean {
        val next = (value.toLong() + rows)
            .coerceIn(0L, maxValue.toLong())
            .toInt()

        return scrollTo(next)
    }

    internal fun updateBounds(contentHeight: Int, viewportHeight: Int) {
        viewportSize = viewportHeight
        maxValue = (contentHeight - viewportHeight).coerceAtLeast(0)
        value = value.coerceAtMost(maxValue)
    }
}

@Composable
fun rememberScrollState(): ScrollState = remember { ScrollState() }
