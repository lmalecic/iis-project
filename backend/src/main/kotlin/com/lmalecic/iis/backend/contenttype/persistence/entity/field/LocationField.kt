package com.lmalecic.iis.backend.contenttype.persistence.entity.field

import com.lmalecic.iis.backend.contenttype.persistence.entity.ContentType
import jakarta.persistence.Entity

@Entity
class LocationField(
    contentType: ContentType,
    identifier: String,
    name: String
) : Field(contentType, identifier, name)
