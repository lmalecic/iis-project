package com.lmalecic.iis.backend.contenttype.persistence.entity.field

import com.lmalecic.iis.backend.contenttype.persistence.converter.LocaleTagConverter
import com.lmalecic.iis.backend.contenttype.persistence.entity.ContentType
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.MapKeyColumn
import jakarta.persistence.UniqueConstraint
import java.util.Locale

@Entity
class BooleanField(
    contentType: ContentType,
    identifier: String,
    name: String
) : Field(contentType, identifier, name) {
    @ElementCollection
    @CollectionTable(
        joinColumns = [
            JoinColumn(nullable = false)
        ],
        uniqueConstraints = [
            UniqueConstraint(columnNames = ["boolean_field_id", "locale"])
        ]
    )
    @MapKeyColumn(name = "locale", nullable = false)
    @Convert(attributeName = "key", converter = LocaleTagConverter::class)
    @Column(name = "default_value", nullable = false)
    var defaultValueByLocale: MutableMap<Locale, Boolean> = linkedMapOf()
}
