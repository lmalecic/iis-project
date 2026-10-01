package com.lmalecic.iis.client.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.jakewharton.mosaic.LocalTerminalState
import com.jakewharton.mosaic.focus.focusGroup
import com.jakewharton.mosaic.layout.fillMaxWidth
import com.jakewharton.mosaic.layout.height
import com.jakewharton.mosaic.layout.padding
import com.jakewharton.mosaic.layout.width
import com.jakewharton.mosaic.modifier.Modifier
import com.jakewharton.mosaic.ui.Arrangement
import com.jakewharton.mosaic.ui.Box
import com.jakewharton.mosaic.ui.Column
import com.jakewharton.mosaic.ui.Text
import com.lmalecic.iis.client.ui.component.Header
import com.lmalecic.iis.client.ui.component.Navbar
import com.lmalecic.iis.client.ui.focus.FocusAxis
import com.lmalecic.iis.client.ui.focus.FocusNavigation
import com.lmalecic.iis.client.ui.focus.FocusNavigationGroup
import com.lmalecic.iis.client.ui.focus.rememberFocusNavigation
import com.lmalecic.iis.client.ui.navigation.Destination

@Composable
fun App() {
    var destination by remember { mutableStateOf(Destination.entries.first()) }
    var screenNavigation by remember { mutableStateOf<FocusNavigation?>(null) }
    val navbarNavigation = rememberFocusNavigation(
        axis = FocusAxis.Horizontal,
        onEnter = { screenNavigation?.requestFirst() },
    )
    val terminal = LocalTerminalState.current

    Column(
        modifier = Modifier.width(terminal.size.columns)
            .height((terminal.size.rows - 1).coerceAtLeast(0))
            .padding(horizontal = 2),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(1),
        ) {
            Header()
            Navbar(
                selected = destination,
                onSelect = { destination = it },
                navigation = navbarNavigation,
            )
            key(destination) {
                val navigation = rememberFocusNavigation(
                    axis = FocusAxis.Vertical,
                    exitAtStart = true,
                    onExit = { navbarNavigation.requestFocus(destination.ordinal) },
                )
                SideEffect { screenNavigation = navigation }
                FocusNavigationGroup(navigation) {
                    Box(Modifier.fillMaxWidth().weight(1f).focusGroup()) {
                        destination.content()
                    }
                }
            }
        }
        Text(
            modifier = Modifier.padding(left = 1),
            value = "←/→ Focus tab | Enter Open | ↓ Enter screen | Esc Back | Ctrl+C Exit",
        )
    }
}
