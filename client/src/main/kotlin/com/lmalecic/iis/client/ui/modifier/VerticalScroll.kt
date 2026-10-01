package com.lmalecic.iis.client.ui.modifier

import com.jakewharton.mosaic.layout.BeyondBoundsLayout
import com.jakewharton.mosaic.layout.BeyondBoundsLayoutProviderModifierNode
import com.jakewharton.mosaic.layout.BringIntoViewModifierNode
import com.jakewharton.mosaic.layout.ContentDrawScope
import com.jakewharton.mosaic.layout.DrawModifier
import com.jakewharton.mosaic.layout.LayoutCoordinates
import com.jakewharton.mosaic.layout.Remeasurement
import com.jakewharton.mosaic.layout.RemeasurementModifier
import com.jakewharton.mosaic.layout.bringIntoView
import com.jakewharton.mosaic.layout.clipToBounds
import com.jakewharton.mosaic.layout.layout
import com.jakewharton.mosaic.modifier.Modifier
import com.jakewharton.mosaic.node.ModifierNodeElement
import com.jakewharton.mosaic.node.requireLayoutCoordinates
import com.jakewharton.mosaic.ui.TextStyle
import com.jakewharton.mosaic.ui.unit.Constraints
import com.jakewharton.mosaic.ui.unit.IntOffset
import com.jakewharton.mosaic.ui.unit.IntRect
import com.jakewharton.mosaic.ui.unit.IntSize
import com.jakewharton.mosaic.ui.unit.constrainHeight
import com.jakewharton.mosaic.ui.unit.constrainWidth
import com.lmalecic.iis.client.ui.state.ScrollState

/** Scrolls focused descendants into view; overflow indicators overlay the first/last row only when needed. */
fun Modifier.verticalScroll(state: ScrollState, showOverflow: Boolean = true): Modifier =
    this.clipToBounds()
        .then(ScrollRelocationElement(state, showOverflow))
        .then(ScrollRemeasurementElement(state))
        .then(OverflowIndicator(state, showOverflow))
        .layout { measurable, constraints ->
            check(constraints.hasBoundedHeight) { "verticalScroll requires a bounded height" }
            val content = measurable.measure(
                constraints.copy(minHeight = 0, maxHeight = Constraints.Infinity)
            )
            val width = constraints.constrainWidth(content.width)
            val height = constraints.constrainHeight(content.height)
            state.updateBounds(content.height, height)
            layout(width, height) { content.place(0, -state.value) }
        }

private data class ScrollRelocationElement(val state: ScrollState, val showOverflow: Boolean) : ModifierNodeElement<ScrollRelocationNode>() {
    override fun create() = ScrollRelocationNode(state, showOverflow)
    override fun update(node: ScrollRelocationNode) { node.state = state; node.showOverflow = showOverflow }
}

private class ScrollRelocationNode(var state: ScrollState, var showOverflow: Boolean) : Modifier.Node(),
    BringIntoViewModifierNode, BeyondBoundsLayoutProviderModifierNode {
    // Every row is already measured. Keeping a provider in the focus tree preserves clipped
    // targets so requestFocus and Tab can discover them and trigger bringIntoView.
    override val beyondBoundsLayout = object : BeyondBoundsLayout {
        override fun <T> layout(
            direction: BeyondBoundsLayout.LayoutDirection,
            block: BeyondBoundsLayout.BeyondBoundsScope.() -> T?,
        ): T? {
            if (direction == BeyondBoundsLayout.LayoutDirection.Left ||
                direction == BeyondBoundsLayout.LayoutDirection.Right) return null
            return block(object : BeyondBoundsLayout.BeyondBoundsScope {
                override val hasMoreContent = false
            })
        }
    }

    override suspend fun bringIntoView(childCoordinates: LayoutCoordinates, boundsProvider: () -> IntRect?) {
        if (!isAttached || !childCoordinates.isAttached) return
        val viewport = requireLayoutCoordinates()
        // Revealing a row can make an indicator appear/disappear. Recheck the new visible
        // area after synchronous layout so the focused row never remains behind an overlay.
        while (isAttached && childCoordinates.isAttached) {
            val bounds = boundsProvider() ?: return
            val indicators = state.indicators(showOverflow)
            val visibleTop = if (indicators.top.isNotEmpty()) 1 else 0
            val visibleBottom = state.viewportSize - if (indicators.bottom.isNotEmpty()) 1 else 0
            val top = childCoordinates.position.y + bounds.top - viewport.position.y
            val bottom = childCoordinates.position.y + bounds.bottom - viewport.position.y
            val delta = when {
                bounds.height > visibleBottom - visibleTop -> top - visibleTop
                top < visibleTop -> top - visibleTop
                bottom > visibleBottom -> bottom - visibleBottom
                else -> 0
            }
            if (!state.scrollBy(delta)) break
            state.remeasurement?.forceRemeasure()
        }
        // Continue relocation through any enclosing scroll viewport.
        bringIntoView { IntRect(IntOffset.Zero, viewport.size) }
    }
}

private data class ScrollRemeasurementElement(val state: ScrollState) : RemeasurementModifier {
    override fun onRemeasurementAvailable(remeasurement: Remeasurement) {
        state.remeasurement = remeasurement
    }
}

private data class OverflowIndicator(val state: ScrollState, val enabled: Boolean) : DrawModifier {
    override fun ContentDrawScope.draw() {
        drawContent()
        if (!enabled || state.maxValue == 0 || height <= 1 || width == 0) return
        val indicators = state.indicators(enabled)
        if (indicators.top.isNotEmpty()) drawIndicator(0, indicators.top)
        if (indicators.bottom.isNotEmpty()) drawIndicator(height - 1, indicators.bottom)
    }

    private fun ContentDrawScope.drawIndicator(row: Int, label: String) {
        drawRect(char = ' ', topLeft = IntOffset(0, row), size = IntSize(width, 1))
        drawText(row = row, column = 0, string = label.take(width), textStyle = TextStyle.Dim)
    }
}


private data class ScrollIndicators(val top: String = "", val bottom: String = "")

private fun ScrollState.indicators(enabled: Boolean): ScrollIndicators {
    if (!enabled || maxValue == 0 || viewportSize <= 1) return ScrollIndicators()
    val above = value > 0
    val below = value < maxValue
    // At least one content row must remain visible in a two-row terminal viewport.
    if (viewportSize == 2 && above && below) return ScrollIndicators(bottom = "↑ ... ↓")
    return ScrollIndicators(
        top = if (above) "↑ ..." else "",
        bottom = if (below) "↓ ..." else "",
    )
}
