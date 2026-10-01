package com.lmalecic.iis.client.ui.focus

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.jakewharton.mosaic.focus.FocusRequester

@Composable
fun rememberFocusRequester() =
    remember { FocusRequester() }