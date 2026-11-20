package com.lmalecic.iis.backend.contenttype.contentful

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "contentful")
class ContentfulProperties(
    val spaceId: String,
    val token: String,
)
