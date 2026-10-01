package com.lmalecic.iis.client.ui.focus

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.staticCompositionLocalOf
import com.jakewharton.mosaic.focus.FocusRequester
import com.jakewharton.mosaic.focus.FocusState
import com.jakewharton.mosaic.focus.focusRequester
import com.jakewharton.mosaic.focus.focusable
import com.jakewharton.mosaic.focus.onFocusChanged
import com.jakewharton.mosaic.layout.KeyEvent
import com.jakewharton.mosaic.layout.onKeyEvent
import com.jakewharton.mosaic.modifier.Modifier

enum class FocusAxis { Horizontal, Vertical }

/** Ordered arrow navigation; Tab remains owned by Mosaic. */
class FocusNavigation internal constructor(
    val axis: FocusAxis,
    private val exit: () -> Unit,
    private val enter: () -> Unit,
    private val exitAtStart: Boolean,
) {
    private val targets = mutableMapOf<FocusRequester, Int>()
    private var current: FocusRequester? = null

    internal fun register(requester: FocusRequester, order: Int) {
        targets[requester] = order
    }

    internal fun unregister(requester: FocusRequester) {
        targets.remove(requester)
        if (current === requester) current = null
    }

    internal fun focused(requester: FocusRequester) { current = requester }

    private fun orderedTargets() = targets.entries.sortedBy { it.value }.map { it.key }

    fun requestFirst(): Boolean = orderedTargets().firstOrNull()?.requestFocus() ?: false

    fun requestFocus(order: Int): Boolean =
        targets.entries.firstOrNull { it.value == order }?.key?.requestFocus() ?: false

    fun requestCurrent(): Boolean = current?.requestFocus() ?: requestFirst()

    internal fun handle(event: KeyEvent, requester: FocusRequester): Boolean {
        if (event.ctrl || event.alt || event.shift) return false
        if (event.key == "Escape") {
            exit()
            return true
        }
        val offset = when (axis) {
            FocusAxis.Horizontal -> when (event.key) {
                "ArrowLeft" -> -1
                "ArrowRight" -> 1
                "ArrowDown" -> { enter(); return true }
                "ArrowUp" -> return true
                else -> return false
            }
            FocusAxis.Vertical -> when (event.key) {
                "ArrowUp" -> -1
                "ArrowDown" -> 1
                "ArrowLeft", "ArrowRight" -> return true
                else -> return false
            }
        }
        val ordered = orderedTargets()
        val index = ordered.indexOf(requester)
        if (index < 0) return false
        val next = index + offset
        if (next in ordered.indices) {
            ordered[next].requestFocus()
        } else if (offset < 0 && exitAtStart) {
            exit()
        }
        // Consume arrows at either boundary instead of falling through to spatial search.
        return true
    }
}

val LocalFocusNavigation = staticCompositionLocalOf<FocusNavigation?> { null }

@Composable
fun rememberFocusNavigation(
    axis: FocusAxis,
    exitAtStart: Boolean = false,
    onExit: (() -> Unit)? = null,
    onEnter: () -> Unit = {},
): FocusNavigation {
    val parent = LocalFocusNavigation.current
    val exit = rememberUpdatedState(onExit ?: { parent?.requestCurrent(); Unit })
    val enter = rememberUpdatedState(onEnter)
    return remember(axis, exitAtStart, parent) {
        FocusNavigation(axis, { exit.value() }, { enter.value() }, exitAtStart)
    }
}

@Composable
fun FocusNavigationGroup(navigation: FocusNavigation, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalFocusNavigation provides navigation, content = content)
}

/**
 * Use a unique order within the current group. Component key handling runs first, so inputs
 * retain cursor keys. Nested groups return to their parent's last focused control on Escape.
 */
@Composable
fun Modifier.navigationFocusable(
    order: Int,
    enabled: Boolean = true,
    autoFocus: Boolean = false,
    requester: FocusRequester = rememberFocusRequester(),
    onFocusChanged: (FocusState) -> Unit = {},
    onKeyEvent: (KeyEvent) -> Boolean = { false },
): Modifier {
    val navigation = LocalFocusNavigation.current
    DisposableEffect(navigation, requester, enabled) {
        if (enabled) navigation?.register(requester, order)
        onDispose { navigation?.unregister(requester) }
    }
    SideEffect { if (enabled) navigation?.register(requester, order) }
    return this
        .focusRequester(requester)
        .onFocusChanged {
            if (it.isFocused) navigation?.focused(requester)
            onFocusChanged(it)
        }
        .onKeyEvent {
            onKeyEvent(it) || navigation?.handle(it, requester) == true
        }
        .focusable(enabled = enabled, autoFocus = autoFocus)
}
