package com.lmalecic.iis.client.ui.component

import androidx.compose.runtime.Composable
import com.jakewharton.mosaic.layout.fillMaxWidth
import com.jakewharton.mosaic.layout.padding
import com.jakewharton.mosaic.modifier.Modifier
import com.jakewharton.mosaic.ui.Arrangement
import com.jakewharton.mosaic.ui.Color
import com.jakewharton.mosaic.ui.Row
import com.jakewharton.mosaic.ui.Text
import com.jakewharton.mosaic.ui.TextStyle
import com.lmalecic.iis.client.ui.navigation.Destination
import com.lmalecic.iis.client.ui.theme.AppTheme

@Composable
fun Navbar(selected: Destination) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Destination.entries.forEach { destination ->
            val isSelected = destination == selected

            Text(
                modifier = Modifier.padding(horizontal = 1),
                value = destination.label,
                color = if (isSelected) {
                    AppTheme.colorScheme.primary
                } else {
                    Color.Unspecified
                },
                textStyle = if (isSelected) {
                    TextStyle.Bold
                } else {
                    TextStyle.Dim
                }
            )

            if (destination.ordinal != Destination.entries.lastIndex) {
                Text("//")
            }
        }
    }
}