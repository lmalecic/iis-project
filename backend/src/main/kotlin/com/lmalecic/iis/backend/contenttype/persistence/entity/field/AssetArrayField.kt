package com.lmalecic.iis.backend.contenttype.persistence.entity.field

import com.lmalecic.iis.backend.contenttype.persistence.entity.ContentType
import jakarta.persistence.Embedded
import jakarta.persistence.Entity

@Entity
class AssetArrayField(
    contentType: ContentType,
    identifier: String,
    name: String,
) : ArrayField(contentType, identifier, name) {
    @Embedded
    var itemValidations: AssetValidations? = null
}
