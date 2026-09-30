package com.lmalecic.iis.backend.weather

import com.lmalecic.iis.contracts.weather.GetTemperaturesRequest
import com.lmalecic.iis.contracts.weather.GetTemperaturesResponse
import com.lmalecic.iis.contracts.weather.TemperatureObservation
import com.lmalecic.iis.contracts.weather.WeatherServiceGrpc
import io.grpc.Status
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import org.springframework.grpc.server.service.GrpcService

@GrpcService
class WeatherGrpcService(
    private val weatherService: WeatherService,
) : WeatherServiceGrpc.WeatherServiceImplBase() {

    private val logger = LoggerFactory.getLogger(WeatherGrpcService::class.java)

    override fun getTemperatures(
        request: GetTemperaturesRequest,
        responseObserver: StreamObserver<GetTemperaturesResponse>
    ) {
        val response = try {
            val observations = this.weatherService.getTemperatures(request.cityQuery)

            GetTemperaturesResponse.newBuilder()
                .addAllObservations(observations.map {
                    it.toProto()
                }).build()
        } catch (exception: DhmzException) {
            this.logger.warn("DHMZ lookup failed", exception)

            responseObserver.onError(
                Status.UNAVAILABLE
                    .withDescription("Weather data is currently unavailable")
                    .asRuntimeException()
            )
            return
        } catch (exception: InterruptedException) {
            Thread.currentThread().interrupt()

            responseObserver.onError(
                Status.CANCELLED
                    .withDescription("Weather lookup was interrupted")
                    .asRuntimeException()
            )
            return
        } catch (exception: Exception) {
            this.logger.error("Unexpected weather lookup failure", exception)

            responseObserver.onError(
                Status.INTERNAL
                    .withDescription("Could not process the weather request")
                    .asRuntimeException()
            )
            return
        }

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }

    private fun WeatherObservation.toProto(): TemperatureObservation {
        val builder = TemperatureObservation.newBuilder()
            .setCityName(this.cityName)

        this.temperatureCelsius?.let { temperature ->
            builder.setTemperatureCelsius(temperature)
        }

        return builder.build()
    }
}