package com.lmalecic.iis.backend.contenttype.persistence.entity.field

import com.lmalecic.iis.backend.contenttype.persistence.converter.LocaleTagConverter
import com.lmalecic.iis.backend.contenttype.persistence.entity.ContentType
import com.lmalecic.iis.backend.contenttype.persistence.entity.rule.ProhibitRegexRule
import com.lmalecic.iis.backend.contenttype.persistence.entity.rule.RegexRule
import com.lmalecic.iis.backend.contenttype.persistence.entity.rule.SizeRule
import com.lmalecic.iis.backend.contenttype.persistence.entity.rule.SymbolAllowedValuesRule
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Embeddable
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapKey
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.util.Locale

@Entity
class SymbolArrayField(
    contentType: ContentType,
    identifier: String,
    name: String,
) : ArrayField(contentType, identifier, name) {
    @Embedded
    var itemValidations: SymbolArrayItemValidations? = null

    @OneToMany(
        mappedBy = "field",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
    )
    @MapKey(name = "locale")
    var defaultValuesByLocale: MutableMap<Locale, SymbolArrayLocaleDefaults> = linkedMapOf()
}

@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["field_id", "locale"]),
    ]
)
class SymbolArrayLocaleDefaults(
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(nullable = false)
    var field: SymbolArrayField,

    @Column(nullable = false)
    @Convert(converter = LocaleTagConverter::class)
    var locale: Locale,
) {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    @OneToMany(
        mappedBy = "localeDefaults",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
    )
    var values: MutableSet<SymbolArrayDefaultValue> = linkedSetOf()
}

@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["locale_defaults_id", "default_value"]),
    ],
)
class SymbolArrayDefaultValue(
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(nullable = false)
    var localeDefaults: SymbolArrayLocaleDefaults,

    @Column(name = "default_value", nullable = false, length = 256)
    var value: String,

    @Column(nullable = false)
    var order: Int,
) {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set
}

@Embeddable
class SymbolArrayItemValidations(
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
    var allowedValues: SymbolAllowedValuesRule? = null,

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
    var prohibitRegex: ProhibitRegexRule? = null,
)