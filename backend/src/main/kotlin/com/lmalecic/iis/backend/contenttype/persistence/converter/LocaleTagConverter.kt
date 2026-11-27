package com.lmalecic.iis.backend.contenttype.persistence.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.util.Locale

@Converter
class LocaleTagConverter : AttributeConverter<Locale, String> {
    override fun convertToDatabaseColumn(locale: Locale?): String? =
        locale?.toLanguageTag()

    override fun convertToEntityAttribute(tag: String?): Locale? =
        tag?.let { Locale.forLanguageTag(it) }
}