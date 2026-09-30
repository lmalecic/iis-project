package com.lmalecic.iis.client

import androidx.compose.runtime.LaunchedEffect
import com.jakewharton.mosaic.runMosaicBlocking
import com.lmalecic.iis.client.ui.App
import com.lmalecic.iis.client.ui.theme.AppTheme
import kotlinx.coroutines.awaitCancellation

fun main() {
    print("\u001B[?1049h\u001B[H")
    System.out.flush()

    try {
        runMosaicBlocking {
            LaunchedEffect(Unit) {
                awaitCancellation()
            }

            AppTheme {
                App()
            }
        }
    } finally {
        print("\u001B[?1049l")
        System.out.flush()
    }
}
