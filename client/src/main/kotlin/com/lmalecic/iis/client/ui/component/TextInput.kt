package com.lmalecic.iis.client.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.jakewharton.mosaic.modifier.Modifier
import com.jakewharton.mosaic.text.SpanStyle
import com.jakewharton.mosaic.text.buildAnnotatedString
import com.jakewharton.mosaic.text.withStyle
import com.jakewharton.mosaic.ui.Color
import com.jakewharton.mosaic.ui.Row
import com.jakewharton.mosaic.ui.Text
import com.jakewharton.mosaic.ui.TextStyle
import com.lmalecic.iis.client.ui.focus.navigationFocusable
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

data class TextInputValue(
    val text: String = "",
    val cursor: Int = text.length
)

@Composable
fun TextInput(
    value: TextInputValue,
    placeholder: String = "",
    modifier: Modifier = Modifier,
    maxLength: Int = Int.MAX_VALUE,
    focusOrder: Int = 0,
    onValueChange: (TextInputValue) -> Unit,
    onSubmit: () -> Unit,
) {
    var focused by remember { mutableStateOf(false) }

    val text = value.text
    val cursor = value.cursor.coerceIn(0, text.length)

    var cursorVisible by remember(focused, text, cursor) {
        mutableStateOf(true)
    }

    LaunchedEffect(focused, text, cursor) {
        if (focused) {
            while (true) {
                delay(500.milliseconds)
                cursorVisible = !cursorVisible
            }
        }
    }

    Row(
        modifier = modifier.navigationFocusable(
            order = focusOrder,
            onFocusChanged = { focused = it.isFocused },
            onKeyEvent = input@{ event ->
                if (!focused || event.ctrl || event.alt) {
                    return@input false
                }

                val updated = when (event.key) {
                    "ArrowLeft" -> value.copy(cursor = (cursor - 1).coerceAtLeast(0))
                    "ArrowRight" -> value.copy(cursor = (cursor + 1).coerceAtMost(text.length))
                    "Home" -> value.copy(cursor = 0)
                    "End" -> value.copy(cursor = text.length)
                    "Backspace" -> {
                        if (cursor > 0) {
                            TextInputValue(
                                text.removeRange(cursor - 1, cursor),
                                cursor - 1
                            )
                        } else value
                    }
                    "Delete" -> {
                        if (cursor < text.length) {
                            TextInputValue(
                                text.removeRange(cursor, cursor + 1),
                                cursor,
                            )
                        } else value
                    }
                    "Enter" -> {
                        onSubmit()
                        return@input true
                    }
                    else -> {
                        val character = event.key.singleOrNull()
                        if (character == null || character !in ' '..'~') {
                            return@input false
                        }

                        if (text.length >= maxLength) {
                            return@input true
                        }

                        TextInputValue(
                            text.take(cursor) + character + text.drop(cursor),
                            cursor + 1,
                        )
                    }
                }

                onValueChange(updated)
                true
            },
        )
    ) {
        val showingPlaceholder = text.isEmpty()
        val displayText = if (showingPlaceholder) placeholder else text
        val textStyle = if (showingPlaceholder) TextStyle.Dim + TextStyle.Italic else TextStyle.Unspecified

        Text(
            value = "> ",
            textStyle = TextStyle.Dim
        )

        if (focused) {
            Text(
                value = displayText.take(cursor),
                textStyle = textStyle,
            )

            val highlightCursor = cursorVisible

            Text(
                value = displayText.getOrNull(cursor)?.toString() ?: " ",
                color = if (highlightCursor) Color.Black else Color.Unspecified,
                background = if (highlightCursor) Color.White else Color.Unspecified,
                textStyle = textStyle,
            )

            Text(
                value = displayText.drop(cursor + 1),
                textStyle = textStyle,
            )
        } else {
            Text(displayText, textStyle = textStyle)
        }
    }
}