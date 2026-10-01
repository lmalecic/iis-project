package com.lmalecic.iis.client.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import com.jakewharton.mosaic.layout.fillMaxSize
import com.jakewharton.mosaic.layout.fillMaxWidth
import com.jakewharton.mosaic.modifier.Modifier
import com.jakewharton.mosaic.ui.Color
import com.jakewharton.mosaic.ui.Column
import com.jakewharton.mosaic.ui.Row
import com.jakewharton.mosaic.ui.Text
import com.jakewharton.mosaic.ui.TextStyle
import com.lmalecic.iis.client.ui.component.BorderedTitledBox
import com.lmalecic.iis.client.ui.component.DottedSpacer
import com.lmalecic.iis.client.ui.component.TextInput
import com.lmalecic.iis.client.ui.component.TextInputValue
import com.lmalecic.iis.client.ui.focus.navigationFocusable
import com.lmalecic.iis.client.ui.modifier.verticalScroll
import com.lmalecic.iis.client.ui.state.rememberScrollState
import com.lmalecic.iis.client.ui.theme.AppTheme
import com.lmalecic.iis.client.ui.util.Gradient
import com.lmalecic.iis.client.ui.util.colorAt
import com.lmalecic.iis.client.ui.util.pluralizeWithCount
import com.lmalecic.iis.client.weather.WeatherClient
import com.lmalecic.iis.client.weather.WeatherRow
import io.grpc.StatusRuntimeException
import kotlinx.coroutines.CancellationException
import org.w3c.dom.Text

private val TEMPERATURE_COLOR_MAPPING = listOf(
    Color.Blue,
    Color.Cyan,
    Color.Green,
    Color.Yellow,
    Color.Red
)

private const val MIN_TEMPERATURE = 0f
private const val MAX_TEMPERATURE = 40f

@Composable
fun WeatherGrpcScreen() {
    val weatherClient = remember { WeatherClient() }
    DisposableEffect(weatherClient) {
        onDispose { weatherClient.close() }
    }
    WeatherGrpcContent(fetchTemperatures = weatherClient::fetchTemperatures)
}

@Composable
internal fun WeatherGrpcContent(fetchTemperatures: suspend (String) -> List<WeatherRow>) {
    var queryInput by remember { mutableStateOf(TextInputValue()) }
    val resultsScroll = rememberScrollState()
    val fetch = rememberUpdatedState(fetchTemperatures)
    var submittedQuery by remember { mutableStateOf("") }
    var requestVersion by remember { mutableIntStateOf(0) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var results by remember { mutableStateOf<List<WeatherRow>>(emptyList()) }

    // Runs once on entry with an empty query (all observations), then once per submission.
    LaunchedEffect(requestVersion) {
        loading = true
        error = null
        results = emptyList()
        resultsScroll.scrollTo(0)
        try {
            results = fetch.value(submittedQuery)
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

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        BorderedTitledBox(
            modifier = Modifier.fillMaxWidth(),
            title = "Query"
        ) {
            TextInput(
                value = queryInput,
                placeholder = "Enter full or partial city name...",
                onValueChange = {
                    queryInput = it
                },
                onSubmit = {
                    if (!loading) {
                        submittedQuery = queryInput.text
                        loading = true
                        requestVersion++
                    }
                }
            )
        }

        BorderedTitledBox(
            modifier = Modifier.fillMaxWidth()
                .weight(1f),
            title = "Results" + when {
                loading || error != null || results.isEmpty() -> ""
                else -> " • ${"observation".pluralizeWithCount(results.size)}"
            }
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
                    .verticalScroll(resultsScroll)
            ) {
                val message: String? = when {
                    loading -> "Loading..."
                    error != null -> "Error: $error"
                    results.isEmpty() && submittedQuery.isNotBlank() -> "No matching observations found."
                    results.isEmpty() -> "No observations found."
                    else -> null
                }

                val messageColor: Color = when {
                    error != null -> AppTheme.colorScheme.error
                    else -> Color.Unspecified
                }

                if (message != null) {
                    Text(
                        value = message,
                        color = messageColor,
                        textStyle = TextStyle.Italic + TextStyle.Dim
                    )
                } else {
                    results.forEachIndexed { index, observation ->
                        WeatherResultItem(
                            observation = observation,
                            order = index + 1,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WeatherResultItem(observation: WeatherRow, order: Int) {
    var focused by remember { mutableStateOf(false) }
    val rowColor = if (focused) AppTheme.colorScheme.primary else Color.Unspecified
    val temperatureColor = observation.temperatureCelsius?.let {
        TEMPERATURE_COLOR_MAPPING.colorAt(it.toFloat().coerceIn(MIN_TEMPERATURE, MAX_TEMPERATURE) / MAX_TEMPERATURE)
    } ?: rowColor

    Row(
        modifier = Modifier.fillMaxWidth()
            .navigationFocusable(
                order = order,
                onFocusChanged = { focused = it.isFocused }
            ),
    ) {
        Text(
            value = (if (focused) "> " else "") + observation.cityName,
            color = rowColor,
        )
        DottedSpacer(color = rowColor)
        Text(
            value = observation.temperatureCelsius?.let { "${it}°C" } ?: "Unavailable",
            color = temperatureColor,
        )
    }
}
