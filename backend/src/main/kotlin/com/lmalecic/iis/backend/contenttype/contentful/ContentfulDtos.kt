package com.lmalecic.iis.backend.contenttype.contentful

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import java.math.BigDecimal

@JsonIgnoreProperties(ignoreUnknown = true)
data class ContentfulContentTypesResponse(
    val total: Int,
    val skip: Int,
    val limit: Int,
    val items: List<ContentfulContentTypeDto>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ContentfulContentTypeDto(
    val sys: ContentfulContentTypeSysDto,
    val name: String,
    val description: String? = null,
    val displayField: String? = null,
    val fields: List<ContentfulFieldDto>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ContentfulContentTypeSysDto(val id: String)

enum class ContentfulFieldType(
    @get:JsonValue val value: String,
    private val dto: Class<out ContentfulFieldDto>? = null,
) {
    SYMBOL("Symbol", SymbolFieldDto::class.java),
    TEXT("Text", TextFieldDto::class.java),
    RICH_TEXT("RichText", RichTextFieldDto::class.java),
    INTEGER("Integer", IntegerFieldDto::class.java),
    NUMBER("Number", DecimalFieldDto::class.java),
    DATE("Date", DateFieldDto::class.java),
    LOCATION("Location", LocationFieldDto::class.java),
    BOOLEAN("Boolean", BooleanFieldDto::class.java),
    OBJECT("Object", ObjectFieldDto::class.java),
    LINK("Link"),
    ARRAY("Array"),
    RESOURCE_LINK("ResourceLink", ResourceLinkFieldDto::class.java);

    fun dtoClass(
        linkType: ContentfulLinkType? = null,
        itemType: ContentfulFieldType? = null,
        itemLinkType: ContentfulLinkType? = null,
    ): Class<out ContentfulFieldDto>? = when (this) {
        LINK -> linkType?.fieldDto
        ARRAY -> when (itemType) {
            SYMBOL -> SymbolArrayFieldDto::class.java
            RESOURCE_LINK -> ResourceLinkArrayFieldDto::class.java
            LINK -> itemLinkType?.arrayDto
            else -> null
        }
        else -> dto
    }

    companion object {
        fun fromValue(value: String?): ContentfulFieldType? =
            entries.firstOrNull { it.value == value }
    }
}

enum class ContentfulLinkType(
    @get:JsonValue val value: String,
    internal val fieldDto: Class<out ContentfulFieldDto>,
    internal val arrayDto: Class<out ContentfulFieldDto>,
) {
    ASSET("Asset", AssetFieldDto::class.java, AssetArrayFieldDto::class.java),
    ENTRY("Entry", EntryFieldDto::class.java, EntryArrayFieldDto::class.java),;

    companion object {
        fun fromValue(value: String?): ContentfulLinkType? =
            entries.firstOrNull { it.value == value }
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
sealed interface ContentfulFieldDto {
    val id: String
    val name: String
    val required: Boolean
    val localized: Boolean
    val disabled: Boolean
    val omitted: Boolean
    val deleted: Boolean?
    val apiName: String?
    val validations: List<ContentfulValidationDto>

    @get:JsonProperty(access = JsonProperty.Access.READ_ONLY)
    val type: ContentfulFieldType
}

sealed class ContentfulValidatedFieldDto<R : ContentfulValidationDto>(
    final override val id: String,
    final override val name: String,
) : ContentfulFieldDto {
    final override var required: Boolean = false
    final override var localized: Boolean = false
    final override var disabled: Boolean = false
    final override var omitted: Boolean = false
    final override var deleted: Boolean? = null
    final override var apiName: String? = null
    final override var validations: List<R> = emptyList()
}

sealed class ContentfulFieldWithDefaultDto<T : Any, R : ContentfulValidationDto>(
    id: String,
    name: String,
) : ContentfulValidatedFieldDto<R>(id, name) {
    var defaultValue: Map<String, T?>? = null
}

class SymbolFieldDto(id: String, name: String) : ContentfulFieldWithDefaultDto<String, ContentfulValidationDto.Symbol>(id, name) {
    override val type: ContentfulFieldType
        get() = ContentfulFieldType.SYMBOL
}

class TextFieldDto(id: String, name: String) : ContentfulFieldWithDefaultDto<String, ContentfulValidationDto.Text>(id, name) {
    override val type: ContentfulFieldType
        get() = ContentfulFieldType.TEXT
}

class IntegerFieldDto(id: String, name: String) : ContentfulFieldWithDefaultDto<Long, ContentfulValidationDto.Integer>(id, name) {
    override val type: ContentfulFieldType
        get() = ContentfulFieldType.INTEGER
}

class DecimalFieldDto(id: String, name: String) : ContentfulFieldWithDefaultDto<BigDecimal, ContentfulValidationDto.Decimal>(id, name) {
    override val type: ContentfulFieldType
        get() = ContentfulFieldType.NUMBER
}

class DateFieldDto(id: String, name: String) : ContentfulFieldWithDefaultDto<String, ContentfulValidationDto.Date>(id, name) {
    override val type: ContentfulFieldType
        get() = ContentfulFieldType.DATE
}

class BooleanFieldDto(id: String, name: String) : ContentfulFieldWithDefaultDto<Boolean, ContentfulValidationDto.None>(id, name) {
    override val type: ContentfulFieldType
        get() = ContentfulFieldType.BOOLEAN
}

class RichTextFieldDto(id: String, name: String) : ContentfulValidatedFieldDto<ContentfulValidationDto.RichText>(id, name) {
    override val type: ContentfulFieldType
        get() = ContentfulFieldType.RICH_TEXT
}

class LocationFieldDto(id: String, name: String) : ContentfulValidatedFieldDto<ContentfulValidationDto.None>(id, name) {
    override val type: ContentfulFieldType
        get() = ContentfulFieldType.LOCATION
}

class ObjectFieldDto(id: String, name: String) : ContentfulValidatedFieldDto<ContentfulValidationDto.Size>(id, name) {
    override val type: ContentfulFieldType
        get() = ContentfulFieldType.OBJECT
}

sealed class LinkFieldDto<R : ContentfulValidationDto>(id: String, name: String) : ContentfulValidatedFieldDto<R>(id, name) {
    final override val type: ContentfulFieldType
        get() = ContentfulFieldType.LINK

    @get:JsonProperty(access = JsonProperty.Access.READ_ONLY)
    abstract val linkType: ContentfulLinkType
}

class AssetFieldDto(id: String, name: String) : LinkFieldDto<ContentfulValidationDto.Asset>(id, name) {
    override val linkType: ContentfulLinkType
        get() = ContentfulLinkType.ASSET
}

class EntryFieldDto(id: String, name: String) : LinkFieldDto<ContentfulValidationDto.Entry>(id, name) {
    override val linkType: ContentfulLinkType
        get() = ContentfulLinkType.ENTRY
}

sealed interface ContentfulArrayFieldDto : ContentfulFieldDto {
    val items: ContentfulArrayItemsDto

    override val type: ContentfulFieldType
        get() = ContentfulFieldType.ARRAY
}

class SymbolArrayFieldDto(
    id: String,
    name: String,
    override val items: SymbolItemsDto
) : ContentfulFieldWithDefaultDto<List<String>, ContentfulValidationDto.Size>(id, name), ContentfulArrayFieldDto

class AssetArrayFieldDto(
    id: String,
    name: String,
    override val items: AssetItemsDto
) : ContentfulValidatedFieldDto<ContentfulValidationDto.Size>(id, name), ContentfulArrayFieldDto

class EntryArrayFieldDto(
    id: String,
    name: String,
    override val items: EntryItemsDto
) : ContentfulValidatedFieldDto<ContentfulValidationDto.Size>(id, name), ContentfulArrayFieldDto

class ResourceLinkFieldDto(
    id: String,
    name: String,
    val allowedResources: List<ContentfulAllowedResourceDto>? = null,
) : ContentfulValidatedFieldDto<ContentfulValidationDto.None>(id, name) {
    override val type: ContentfulFieldType
        get() = ContentfulFieldType.RESOURCE_LINK
}

class ResourceLinkArrayFieldDto(
    id: String,
    name: String,
    override val items: ResourceLinkItemsDto,
    val allowedResources: List<ContentfulAllowedResourceDto>? = null,
) : ContentfulValidatedFieldDto<ContentfulValidationDto.Size>(id, name), ContentfulArrayFieldDto

@JsonIgnoreProperties(ignoreUnknown = true)
sealed interface ContentfulArrayItemsDto {
    val validations: List<ContentfulValidationDto>
    @get:JsonProperty(access = JsonProperty.Access.READ_ONLY)
    val type: ContentfulFieldType
}

sealed interface LinkItemsDto : ContentfulArrayItemsDto {
    override val type: ContentfulFieldType
        get() = ContentfulFieldType.LINK

    @get:JsonProperty(access = JsonProperty.Access.READ_ONLY)
    val linkType: ContentfulLinkType
}

data class SymbolItemsDto(
    override val validations: List<ContentfulValidationDto.Text> = emptyList()
) : ContentfulArrayItemsDto {
    override val type: ContentfulFieldType
        get() = ContentfulFieldType.SYMBOL
}

data class ResourceLinkItemsDto(
    override val validations: List<ContentfulValidationDto.None> = emptyList()
) : ContentfulArrayItemsDto {
    override val type: ContentfulFieldType
        get() = ContentfulFieldType.RESOURCE_LINK
}

data class AssetItemsDto(
    override val validations: List<ContentfulValidationDto.Asset> = emptyList()
) : LinkItemsDto {
    override val linkType: ContentfulLinkType
        get() = ContentfulLinkType.ASSET
}

data class EntryItemsDto(
    override val validations: List<ContentfulValidationDto.Entry> = emptyList()
) : LinkItemsDto {
    override val linkType: ContentfulLinkType
        get() = ContentfulLinkType.ENTRY
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class ContentfulAllowedResourceDto(
    val type: String,
    val source: String? = null,
    val contentTypes: List<String>? = null,
)

sealed interface ContentfulValidationDto {
    val message: String?

    sealed interface Symbol : ContentfulValidationDto
    sealed interface Text : Symbol
    sealed interface Integer : ContentfulValidationDto
    sealed interface Decimal : ContentfulValidationDto
    sealed interface Date : ContentfulValidationDto
    sealed interface Size : ContentfulValidationDto
    sealed interface RichText : ContentfulValidationDto
    sealed interface Asset : ContentfulValidationDto
    sealed interface Entry : ContentfulValidationDto
    sealed interface RichTextEntry : ContentfulValidationDto
    sealed interface None : ContentfulValidationDto

    data class Bounds<T : Any>(
        val min: T? = null,
        val max: T? = null,
    )

    data class RegexPattern(
        val pattern: String,
        val flags: String? = null,
    )

    data class TextAllowedValuesRule(
        @param:JsonProperty("in")
        @get:JsonProperty("in")
        val values: List<String>,
        override val message: String? = null,
    ) : Text

    data class IntegerAllowedValuesRule(
        @param:JsonProperty("in")
        @get:JsonProperty("in")
        val values: List<Long>,
        override val message: String? = null,
    ) : Integer

    data class DecimalAllowedValuesRule(
        @param:JsonProperty("in")
        @get:JsonProperty("in")
        val values: List<BigDecimal>,
        override val message: String? = null,
    ) : Decimal

    data class UniqueRule(
        val unique: Boolean,
        override val message: String? = null,
    ) : Symbol, Integer, Decimal

    data class SizeRule(
        val size: Bounds<Long>,
        override val message: String? = null,
    ) : Text, Size, RichText, RichTextEntry

    data class RangeRule(
        val range: Bounds<BigDecimal>,
        override val message: String? = null,
    ) : Integer, Decimal

    data class DateRangeRule(
        val dateRange: Bounds,
        override val message: String? = null,
    ) : Date {
        data class Bounds(
            val min: String? = null,
            val max: String? = null,
            val after: String? = null,
            val before: String? = null,
        )
    }

    data class RegexRule(
        val regexp: RegexPattern,
        override val message: String? = null,
    ) : Text

    data class ProhibitRegexRule(
        val prohibitRegexp: RegexPattern,
        override val message: String? = null,
    ) : Text

    data class LinkContentTypeRule(
        val linkContentType: List<String>,
        override val message: String? = null,
    ) : Entry, RichTextEntry

    data class LinkMimetypeGroupRule(
        val linkMimetypeGroup: List<String>,
        override val message: String? = null,
    ) : Asset

    data class AssetFileSizeRule(
        val assetFileSize: Bounds<Long>,
        override val message: String? = null,
    ) : Asset

    data class AssetImageDimensionsRule(
        val assetImageDimensions: Dimensions,
        override val message: String? = null,
    ) : Asset {
        data class Dimensions(
            val width: Bounds<Int>? = null,
            val height: Bounds<Int>? = null,
        )
    }

    data class EnabledMarksRule(
        val enabledMarks: List<String>,
        override val message: String? = null,
    ) : RichText

    data class EnabledNodeTypesRule(
        val enabledNodeTypes: List<String>,
        override val message: String? = null,
    ) : RichText

    data class NodesRule(
        val nodes: Restrictions,
        override val message: String? = null,
    ) : RichText {
        data class Restrictions(
            @param:JsonProperty("embedded-entry-block")
            @get:JsonProperty("embedded-entry-block")
            val embeddedEntryBlock: List<RichTextEntry>? = null,

            @param:JsonProperty("embedded-entry-inline")
            @get:JsonProperty("embedded-entry-inline")
            val embeddedEntryInline: List<RichTextEntry>? = null,

            @param:JsonProperty("entry-hyperlink")
            @get:JsonProperty("entry-hyperlink")
            val entryHyperlink: List<RichTextEntry>? = null,

            @param:JsonProperty("embedded-asset-block")
            @get:JsonProperty("embedded-asset-block")
            val embeddedAssetBlock: List<Size>? = null,

            @param:JsonProperty("asset-hyperlink")
            @get:JsonProperty("asset-hyperlink")
            val assetHyperlink: List<Size>? = null,

            @param:JsonProperty("embedded-resource-block")
            @get:JsonProperty("embedded-resource-block")
            val embeddedResourceBlock: ResourceRestrictions? = null,

            @param:JsonProperty("embedded-resource-inline")
            @get:JsonProperty("embedded-resource-inline")
            val embeddedResourceInline: ResourceRestrictions? = null,

            @param:JsonProperty("resource-hyperlink")
            @get:JsonProperty("resource-hyperlink")
            val resourceHyperlink: ResourceRestrictions? = null,
        )

        data class ResourceRestrictions(
            val validations: List<Size> = emptyList(),
            val allowedResources: List<ContentfulAllowedResourceDto> = emptyList(),
        )
    }
}
