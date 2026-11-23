package com.lmalecic.iis.backend.contenttype

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import jakarta.xml.bind.annotation.XmlEnum
import jakarta.xml.bind.annotation.XmlEnumValue

@XmlEnum
enum class Mark {
    @XmlEnumValue("bold")
    @JsonProperty("bold")
    BOLD,
    @XmlEnumValue("italic")
    @JsonProperty("italic")
    ITALIC,
    @XmlEnumValue("underline")
    @JsonProperty("underline")
    UNDERLINE,
    @XmlEnumValue("code")
    @JsonProperty("code")
    CODE,
    @XmlEnumValue("superscript")
    @JsonProperty("superscript")
    SUPERSCRIPT,
    @XmlEnumValue("subscript")
    @JsonProperty("subscript")
    SUBSCRIPT,
    @XmlEnumValue("strikethrough")
    @JsonProperty("strikethrough")
    STRIKETHROUGH
}
