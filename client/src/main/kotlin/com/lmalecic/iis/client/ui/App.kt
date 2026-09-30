package com.lmalecic.iis.client.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.jakewharton.mosaic.LocalTerminalState
import com.jakewharton.mosaic.layout.KeyEvent
import com.jakewharton.mosaic.layout.fillMaxSize
import com.jakewharton.mosaic.layout.fillMaxWidth
import com.jakewharton.mosaic.layout.height
import com.jakewharton.mosaic.layout.onKeyEvent
import com.jakewharton.mosaic.layout.padding
import com.jakewharton.mosaic.layout.width
import com.jakewharton.mosaic.modifier.Modifier
import com.jakewharton.mosaic.ui.Alignment
import com.jakewharton.mosaic.ui.Arrangement
import com.jakewharton.mosaic.ui.Box
import com.jakewharton.mosaic.ui.Column
import com.jakewharton.mosaic.ui.Row
import com.jakewharton.mosaic.ui.Text
import com.lmalecic.iis.client.ui.component.Logo
import com.lmalecic.iis.client.ui.component.Navbar
import com.lmalecic.iis.client.ui.component.border
import com.lmalecic.iis.client.ui.navigation.Destination

var TITLE_ART = """
 ▄▄▄▄ ▄▄▄▄  ▄▄▄
  █▀   █▀  █▄ ▀
 ▄█   ▄█  ▄ ▀█
▀▀▀▀ ▀▀▀▀ ▀▀▀
""".trimIndent()

@Composable
fun App() {
    var destination by remember {
        mutableStateOf(Destination.Home)
    }

    val terminal = LocalTerminalState.current

    Column(
        modifier = Modifier.width(terminal.size.columns)
            .height((terminal.size.rows - 1).coerceAtLeast(0))
            .padding(horizontal = 2)
            .onKeyEvent { event ->
            val offset = when (event) {
                KeyEvent("ArrowLeft") -> -1
                KeyEvent("ArrowRight") -> 1
                else -> return@onKeyEvent false
            }

            val destinations = Destination.entries
            val nextIndex = (destination.ordinal + offset).coerceIn(0, destinations.size - 1)

            destination = destinations[nextIndex]
            true
        },
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(1)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2, Alignment.CenterHorizontally)
            ) {
                Logo()
                Text(TITLE_ART)
            }

            Navbar(selected = destination)

            Box(
                modifier = Modifier.fillMaxWidth()
                    .weight(1f)
            ) {
                destination.content()
            }
        }

        Text(
            modifier = Modifier.padding(left = 1),
            value = "←/→ Switch screen | Ctrl+C Exit",
        )
    }
}