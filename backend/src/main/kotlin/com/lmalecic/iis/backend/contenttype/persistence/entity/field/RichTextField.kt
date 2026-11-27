package com.lmalecic.iis.backend.contenttype.persistence.entity.field

import com.lmalecic.iis.backend.contenttype.persistence.entity.ContentType
import com.lmalecic.iis.backend.contenttype.persistence.entity.rule.EnabledMarksRule
import com.lmalecic.iis.backend.contenttype.persistence.entity.rule.EnabledNodeTypesRule
import com.lmalecic.iis.backend.contenttype.persistence.entity.rule.RichTextNodesRule
import com.lmalecic.iis.backend.contenttype.persistence.entity.rule.SizeRule
import jakarta.persistence.CascadeType
import jakarta.persistence.Embeddable
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.OneToOne

@Entity
class RichTextField(
    contentType: ContentType,
    identifier: String,
    name: String
) : Field(contentType, identifier, name) {
    @Embedded
    var validations: RichTextValidations? = null
}

@Embeddable
class RichTextValidations(
    @OneToOne(
        mappedBy = "field",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
    )
    var size: SizeRule? = null,

    @OneToOne(
        mappedBy = "field",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
    )
    var enabledMarks: EnabledMarksRule? = null,

    @OneToOne(
        mappedBy = "field",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
    )
    var enabledNodeTypes: EnabledNodeTypesRule? = null,

    @OneToOne(
        mappedBy = "field",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
    )
    var nodes: RichTextNodesRule? = null,
)
