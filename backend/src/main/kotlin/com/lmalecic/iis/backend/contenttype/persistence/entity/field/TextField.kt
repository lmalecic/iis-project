package com.lmalecic.iis.backend.contenttype.persistence.entity.field

import com.lmalecic.iis.backend.contenttype.persistence.converter.LocaleTagConverter
import com.lmalecic.iis.backend.contenttype.persistence.entity.ContentType
import com.lmalecic.iis.backend.contenttype.persistence.entity.rule.ProhibitRegexRule
import com.lmalecic.iis.backend.contenttype.persistence.entity.rule.RegexRule
import com.lmalecic.iis.backend.contenttype.persistence.entity.rule.SizeRule
import com.lmalecic.iis.backend.contenttype.persistence.entity.rule.SymbolAllowedValuesRule
import com.lmalecic.iis.backend.contenttype.persistence.entity.rule.TextAllowedValuesRule
import jakarta.persistence.CascadeType
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.ElementCollection
import jakarta.persistence.Embeddable
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.MapKeyColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.util.Locale

@Entity
class TextField(
    contentType: ContentType,
    identifier: String,
    name: String
) : Field(contentType, identifier, name) {
    @Embedded
    var validations: TextValidations? = null

    @ElementCollection
    @CollectionTable(
        joinColumns = [
            JoinColumn(nullable = false)
        ],
        uniqueConstraints = [
            UniqueConstraint(columnNames = ["text_field_id", "locale"])
        ]
    )
    @MapKeyColumn(name = "locale", nullable = false)
    @Convert(attributeName = "key", converter = LocaleTagConverter::class)
    @Column(name = "default_value", nullable = false, length = 50_000)
    var defaultValueByLocale: MutableMap<Locale, String> = linkedMapOf()
}

@Embeddable
class TextValidations(
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
    var allowedValues: TextAllowedValuesRule? = null,

    @OneToOne(
        mappedBy = "field",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
    )
    var regex: RegexRule? = null,

    @OneToOne(
        mappedBy = "field",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
    )
    var prohibitRegex: ProhibitRegexRule? = null
)