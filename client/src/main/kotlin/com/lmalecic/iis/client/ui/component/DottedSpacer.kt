package com.lmalecic.iis.client.ui.component

import androidx.compose.runtime.Composable
import com.jakewharton.mosaic.layout.drawBehind
import com.jakewharton.mosaic.layout.height
import com.jakewharton.mosaic.modifier.Modifier
import com.jakewharton.mosaic.ui.Box
import com.jakewharton.mosaic.ui.Color
import com.jakewharton.mosaic.ui.RowScope
import com.jakewharton.mosaic.ui.Text
import com.jakewharton.mosaic.ui.TextStyle

@Composable
fun RowScope.DottedSpacer(
    color: Color = Color.Unspecified,
    textStyle: TextStyle = TextStyle.Dim,
) {
    Box(
        modifier = Modifier.weight(1f).height(1).drawBehind {
            drawText(row = 0, column = 1, string = ".".repeat(width - 2), foreground = color, textStyle = textStyle)
        }
    )
}