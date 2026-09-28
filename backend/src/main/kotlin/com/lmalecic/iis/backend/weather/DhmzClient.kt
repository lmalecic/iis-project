package com.lmalecic.iis.backend.weather

import jakarta.xml.bind.JAXBContext
import org.springframework.stereotype.Component
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import javax.xml.XMLConstants
import javax.xml.parsers.DocumentBuilderFactory

private const val FEED_URL = "https://vrijeme.hr/hrvatska_n.xml"

class DhmzException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)

@Component
class DhmzClient {

    private val httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build()

    private val feedUri = URI.create(FEED_URL)
    private val jaxbContext = JAXBContext.newInstance(DhmzResponse::class.java)

    fun fetchObservations(): List<WeatherObservation> {
        val request = HttpRequest.newBuilder(feedUri)
            .timeout(Duration.ofSeconds(10))
            .GET()
            .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray())

        if (response.statusCode() != 200) {
            throw DhmzException("DHMZ returned HTTP ${response.statusCode()}")
        }

        return parseObservations(response.body())
    }

    internal fun parseObservations(xml: ByteArray): List<WeatherObservation> {
        val factory = DocumentBuilderFactory.newInstance().apply {
            isNamespaceAware = true
            setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true)
            setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
        }

        val document = xml.inputStream().use { input ->
            factory.newDocumentBuilder().parse(input)
        }

        val response = jaxbContext.createUnmarshaller()
            .unmarshal(document) as DhmzResponse

        return response.cities.map { city ->
            WeatherObservation(
                cityName = city.name
                    ?.trim()
                    ?.takeIf { it.isNotEmpty() }
                    ?: throw DhmzException("DHMZ record is missing GradIme"),
                temperatureCelsius = city.data
                    ?.temperature
                    ?.trim()
                    ?.toDoubleOrNull()
                    ?.takeIf { it.isFinite() }
            )
        }
    }
}