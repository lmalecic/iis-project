package com.lmalecic.iis.backend.weather

import org.springframework.stereotype.Service

@Service
class WeatherService(
    private val dhmzClient: DhmzClient,
) {

    fun getTemperatures(cityQuery: String): List<WeatherObservation> {
        val query = cityQuery.trim()
        val observations = this.dhmzClient.fetchObservations()

        return if (query.isEmpty()) {
            observations
        } else {
            observations.filter {
                it.cityName.contains(other = query, ignoreCase = true)
            }
        }
    }
}