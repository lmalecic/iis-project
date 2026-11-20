package com.lmalecic.iis.backend.contenttype.contentful

import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

@Component
class ContentfulClient(
    builder: RestClient.Builder,
    private val properties: ContentfulProperties,
) {
    private val client = builder.baseUrl("https://api.contentful.com").build()

    fun fetchContentTypes(): ContentfulContentTypesResponse {
        val response = client.get()
            .uri("/spaces/{spaceId}/content_types", properties.spaceId)
            .headers { it.setBearerAuth(properties.token) }
            .retrieve()
            .body<ContentfulContentTypesResponse>()

        return checkNotNull(response) {
            "Contentful returned an empty response body"
        }
    }
}