package com.lmalecic.iis.client.ui.component

import androidx.compose.runtime.Composable
import com.jakewharton.mosaic.layout.fillMaxWidth
import com.jakewharton.mosaic.modifier.Modifier
import com.jakewharton.mosaic.text.SpanStyle
import com.jakewharton.mosaic.text.buildAnnotatedString
import com.jakewharton.mosaic.text.withStyle
import com.jakewharton.mosaic.ui.Alignment
import com.jakewharton.mosaic.ui.Arrangement
import com.jakewharton.mosaic.ui.Color
import com.jakewharton.mosaic.ui.Row
import com.jakewharton.mosaic.ui.Text
import com.lmalecic.iis.client.ui.util.Gradient

private val LOGO_ART = listOf(
    "   ▄   ",
    " ▄▀ ▀▄▄",
    "▄▀▄▀▀▀▄",
    "▀▀ ▀▀▀▀"
)

private val LOGO_GRADIENT = Gradient(
    colors = listOf(
        Color(0xFF,0x6B,0x01),
        Color(0xFF,0x02,0x6B),
        Color(0xFF,0x02,0x80),
    ),
    angle = -45F,
)

private val TITLE_ART = """
 ▄▄▄▄ ▄▄▄▄  ▄▄▄
  █▀   █▀  █▄ ▀
 ▄█   ▄█  ▄ ▀█
▀▀▀▀ ▀▀▀▀ ▀▀▀
""".trimIndent()

@Composable
fun Header() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(2, Alignment.CenterHorizontally)
    ) {
        Logo()
        Text(TITLE_ART)
    }
}

@Composable
fun Logo() {
    val width = LOGO_ART.maxOf { it.length }
    val height = LOGO_ART.size

    Text(
        buildAnnotatedString {
            LOGO_ART.forEachIndexed { y, line ->
                line.forEachIndexed { x, char ->
                    val t = x.toFloat() / (width - 1).coerceAtLeast(1)
                    val color = LOGO_GRADIENT.colorAt(x, y, width, height)

                    withStyle(SpanStyle(color = color)) {
                        append(char)
                    }
                }

                if (y != LOGO_ART.lastIndex) {
                    append('\n')
                }
            }
        }
    )
}
