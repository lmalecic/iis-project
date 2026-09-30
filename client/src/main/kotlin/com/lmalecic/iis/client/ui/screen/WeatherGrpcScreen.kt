package com.lmalecic.iis.client.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.jakewharton.mosaic.layout.KeyEvent
import com.jakewharton.mosaic.layout.fillMaxSize
import com.jakewharton.mosaic.layout.fillMaxWidth
import com.jakewharton.mosaic.layout.onKeyEvent
import com.jakewharton.mosaic.modifier.Modifier
import com.jakewharton.mosaic.ui.Column
import com.jakewharton.mosaic.ui.Text
import com.lmalecic.iis.client.ui.component.BorderedTitledBox
import com.lmalecic.iis.client.ui.component.TextInput
import com.lmalecic.iis.client.ui.component.TextInputValue
import com.lmalecic.iis.client.ui.util.pluralizeWithCount
import com.lmalecic.iis.client.weather.WeatherClient
import com.lmalecic.iis.client.weather.WeatherRow
import io.grpc.StatusRuntimeException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private enum class Control {
    None,
    Query
}

@Composable
fun WeatherGrpcScreen() {
    var focusedControl: Control by remember { mutableStateOf(Control.None) }
    var queryInput by remember { mutableStateOf(TextInputValue()) }

    val weatherClient = remember { WeatherClient() }
    val scope = rememberCoroutineScope()

    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var results by remember { mutableStateOf<List<WeatherRow>>(emptyList()) }

    DisposableEffect(weatherClient) {
        onDispose {
            weatherClient.close()
        }
    }

    Column(
        modifier = Modifier.onKeyEvent { event ->
            val offset = when (event) {
                KeyEvent("ArrowUp") -> -1
                KeyEvent("ArrowDown") -> 1
                else -> return@onKeyEvent false
            }

            val controls = Control.entries
            val nextIndex = (focusedControl.ordinal + offset)
                .coerceIn(controls.indices)

            focusedControl = controls[nextIndex]
            true
        }
    ) {
        BorderedTitledBox(
            modifier = Modifier.fillMaxWidth(),
            title = "Query"
        ) {
            TextInput(
                value = queryInput,
                placeholder = "Enter full or partial city name...",
                focused = focusedControl == Control.Query,
                onRequestFocusLoss = {
                    focusedControl = Control.None
                },
                onValueChange = {
                    queryInput = it
                },
                onSubmit = {
                    if (!loading) {
                        val submittedQuery = queryInput.text

                        loading = true
                        error = null
                        results = emptyList()

                        scope.launch {
                            try {
                                results = withContext(Dispatchers.IO) {
                                    weatherClient.fetchTemperatures(submittedQuery)
                                }
                            } catch (e: CancellationException) {
                                throw e
                            } catch (e: StatusRuntimeException) {
                                error = e.status.description ?: e.status.code.name
                            } catch (e: Exception) {
                                error = "Could not load temperatures."
                            } finally {
                                loading = false
                            }
                        }
                    }
                }
            )
        }

        BorderedTitledBox(
            modifier = Modifier.fillMaxSize(),
            title = "Results" + when {
                loading || error != null || results.isEmpty() -> ""
                else -> " • ${"observation".pluralizeWithCount(results.size)}"
            }
        ) {
            Column {
                when {
                    loading -> {
                        Text("Loading temperatures...")
                    }

                    error != null -> {
                        Text("Error: $error")
                    }

                    results.isEmpty() -> {
                        Text("No matching observations found.")
                    }

                    else -> {
                        results.forEach { observation ->
                            val temperature = observation.temperatureCelsius?.let { "$it °C" } ?: "Unavailable"
                            Text("${observation.cityName}: $temperature")
                        }
                    }
                }
            }
        }
    }
}