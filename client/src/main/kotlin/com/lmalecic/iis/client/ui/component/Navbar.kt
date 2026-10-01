package com.lmalecic.iis.client.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.jakewharton.mosaic.focus.focusGroup
import com.jakewharton.mosaic.layout.fillMaxWidth
import com.jakewharton.mosaic.layout.padding
import com.jakewharton.mosaic.modifier.Modifier
import com.jakewharton.mosaic.ui.Arrangement
import com.jakewharton.mosaic.ui.Color
import com.jakewharton.mosaic.ui.Row
import com.jakewharton.mosaic.ui.Text
import com.jakewharton.mosaic.ui.TextStyle
import com.lmalecic.iis.client.ui.focus.FocusNavigation
import com.lmalecic.iis.client.ui.focus.FocusNavigationGroup
import com.lmalecic.iis.client.ui.focus.navigationFocusable
import com.lmalecic.iis.client.ui.navigation.Destination
import com.lmalecic.iis.client.ui.theme.AppTheme

@Composable
fun Navbar(
    selected: Destination,
    onSelect: (Destination) -> Unit,
    navigation: FocusNavigation,
    modifier: Modifier = Modifier,
) {
    FocusNavigationGroup(navigation) {
        Row(
            modifier = modifier.fillMaxWidth().focusGroup(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Destination.entries.forEach { destination ->
                var focused by remember(destination) { mutableStateOf(false) }
                Text(
                    value = destination.label,
                    color = if (destination == selected) AppTheme.colorScheme.primary else Color.Unspecified,
                    textStyle = when {
                        focused -> TextStyle.Bold
                        destination == selected -> TextStyle.Bold
                        else -> TextStyle.Dim
                    },
                    modifier = Modifier.padding(horizontal = 1).navigationFocusable(
                        order = destination.ordinal,
                        autoFocus = destination == Destination.entries.first(),
                        onFocusChanged = { focused = it.isFocused },
                        onKeyEvent = { event ->
                            if (event.ctrl || event.alt || event.shift) false else when (event.key) {
                                "Enter" -> { onSelect(destination); true }
                                "Escape" -> { navigation.requestFocus(selected.ordinal); true }
                                else -> false
                            }
                        },
                    ),
                )
                if (destination != Destination.entries.last()) Text("//")
            }
        }
    }
}
