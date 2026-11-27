package com.lmalecic.iis.backend.contenttype.persistence.entity.field

import com.lmalecic.iis.backend.contenttype.persistence.converter.LocaleTagConverter
import com.lmalecic.iis.backend.contenttype.persistence.entity.ContentType
import com.lmalecic.iis.backend.contenttype.persistence.entity.rule.DateRangeRule
import com.lmalecic.iis.backend.contenttype.persistence.entity.rule.NumberAllowedValuesRule
import com.lmalecic.iis.backend.contenttype.persistence.entity.rule.RangeRule
import com.lmalecic.iis.backend.contenttype.persistence.entity.rule.UniqueRule
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
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.Locale

@Entity
class DateField(
    contentType: ContentType,
    identifier: String,
    name: String
) : Field(contentType, identifier, name) {
    @Embedded
    var validations: DateValidations? = null

    @ElementCollection
    @CollectionTable(
        joinColumns = [
            JoinColumn(nullable = false)
        ],
        uniqueConstraints = [
            UniqueConstraint(columnNames = ["date_field_id", "locale"])
        ]
    )
    @MapKeyColumn(name = "locale", nullable = false)
    @Convert(attributeName = "key", converter = LocaleTagConverter::class)
    @Column(name = "default_value", nullable = false)
    var defaultValueByLocale: MutableMap<Locale, OffsetDateTime> = linkedMapOf()
}

@Embeddable
class DateValidations(
    @OneToOne(
        mappedBy = "field",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
    )
    var dateRange: DateRangeRule? = null,
)
