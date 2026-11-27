package com.lmalecic.iis.backend.contenttype.persistence.entity.field

import com.lmalecic.iis.backend.contenttype.persistence.entity.ContentType
import com.lmalecic.iis.backend.contenttype.persistence.entity.rule.AssetFileSizeRule
import com.lmalecic.iis.backend.contenttype.persistence.entity.rule.AssetImageDimensionsRule
import com.lmalecic.iis.backend.contenttype.persistence.entity.rule.LinkMimetypeGroupRule
import com.lmalecic.iis.backend.contenttype.persistence.entity.rule.SizeRule
import jakarta.persistence.CascadeType
import jakarta.persistence.Embeddable
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.OneToOne

@Entity
class AssetField(
    contentType: ContentType,
    identifier: String,
    name: String
) : Field(contentType, identifier, name) {
    @Embedded
    var validations: AssetValidations? = null
}

@Embeddable
class AssetValidations(
    @OneToOne(
        mappedBy = "field",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
    )
    var fileSize: AssetFileSizeRule? = null,

    @OneToOne(
        mappedBy = "field",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
    )
    var imageDimensions: AssetImageDimensionsRule? = null,

    @OneToOne(
        mappedBy = "field",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
    )
    var linkMimetypeGroup: LinkMimetypeGroupRule? = null,
)
