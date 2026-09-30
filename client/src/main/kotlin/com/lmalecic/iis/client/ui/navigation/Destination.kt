package com.lmalecic.iis.client.ui.navigation

import androidx.compose.runtime.Composable
import com.lmalecic.iis.client.ui.screen.WeatherGrpcScreen
import com.lmalecic.iis.client.ui.screen.HomeScreen

enum class Destination(
    val label: String,
    val content: @Composable () -> Unit,
) {
    Home("\uF015 ", { HomeScreen() }),
    WeatherGrpc("Weather gRPC", { WeatherGrpcScreen() })
}
