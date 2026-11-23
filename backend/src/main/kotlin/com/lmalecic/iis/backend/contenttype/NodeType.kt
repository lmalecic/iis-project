package com.lmalecic.iis.backend.contenttype

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.xml.bind.annotation.XmlEnum
import jakarta.xml.bind.annotation.XmlEnumValue

@XmlEnum
enum class NodeType {
    @XmlEnumValue("heading-1")
    @JsonProperty("heading-1")
    HEADING_1,
    @XmlEnumValue("heading-2")
    @JsonProperty("heading-2")
    HEADING_2,
    @XmlEnumValue("heading-3")
    @JsonProperty("heading-3")
    HEADING_3,
    @XmlEnumValue("heading-4")
    @JsonProperty("heading-4")
    HEADING_4,
    @XmlEnumValue("heading-5")
    @JsonProperty("heading-5")
    HEADING_5,
    @XmlEnumValue("heading-6")
    @JsonProperty("heading-6")
    HEADING_6,
    @XmlEnumValue("ordered-list")
    @JsonProperty("ordered-list")
    ORDERED_LIST,
    @XmlEnumValue("unordered-list")
    @JsonProperty("unordered-list")
    UNORDERED_LIST,
    @XmlEnumValue("hr")
    @JsonProperty("hr")
    HR,
    @XmlEnumValue("blockquote")
    @JsonProperty("blockquote")
    BLOCKQUOTE,
    @XmlEnumValue("embedded-entry-block")
    @JsonProperty("embedded-entry-block")
    EMBEDDED_ENTRY_BLOCK,
    @XmlEnumValue("embedded-asset-block")
    @JsonProperty("embedded-asset-block")
    EMBEDDED_ASSET_BLOCK,
    @XmlEnumValue("table")
    @JsonProperty("table")
    TABLE,
    @XmlEnumValue("asset-hyperlink")
    @JsonProperty("asset-hyperlink")
    ASSET_HYPERLINK,
    @XmlEnumValue("embedded-entry-inline")
    @JsonProperty("embedded-entry-inline")
    EMBEDDED_ENTRY_INLINE,
    @XmlEnumValue("entry-hyperlink")
    @JsonProperty("entry-hyperlink")
    ENTRY_HYPERLINK,
    @XmlEnumValue("hyperlink")
    @JsonProperty("hyperlink")
    HYPERLINK
}