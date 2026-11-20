package com.lmalecic.iis.backend.contenttype.contentful

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ContentfulClientLiveTest @Autowired constructor(
    private val client: ContentfulClient,
) {
    @Test
    fun `fetches Game and Review from Contentful`() {
        val response = client.fetchContentTypes()

        assertThat(response.items.map { it.sys.id })
            .contains("game", "review", "fieldTypeSandbox")
    }
}