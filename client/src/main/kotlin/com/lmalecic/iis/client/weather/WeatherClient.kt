package com.lmalecic.iis.client.weather

import com.lmalecic.iis.contracts.weather.GetTemperaturesRequest
import com.lmalecic.iis.contracts.weather.WeatherServiceGrpc
import io.grpc.ManagedChannelBuilder
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible

data class WeatherRow(
    val cityName: String,
    val temperatureCelsius: Double? = null,
)

class WeatherClient(
    host: String = "localhost",
    port: Int = 9090,
) : AutoCloseable {

    private val channel = ManagedChannelBuilder.forAddress(host, port)
        .usePlaintext()
        .build()

    private val stub = WeatherServiceGrpc.newBlockingStub(channel)

    suspend fun fetchTemperatures(query: String = ""): List<WeatherRow> = runInterruptible(Dispatchers.IO) {
        val request = GetTemperaturesRequest.newBuilder()
            .setCityQuery(query)
            .build()

        val response = stub.withDeadlineAfter(20, TimeUnit.SECONDS)
            .getTemperatures(request)

        response.observationsList.map { observation ->
            WeatherRow(
                cityName = observation.cityName,
                temperatureCelsius = if (observation.hasTemperatureCelsius()) observation.temperatureCelsius else null
            )
        }
    }

    override fun close() {
        channel.shutdown()
    }
}