package com.lmalecic.iis.backend.contenttype

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.xml.bind.annotation.XmlEnum
import jakarta.xml.bind.annotation.XmlEnumValue

@XmlEnum
enum class Mimetype {
    @XmlEnumValue("attachment")
    @JsonProperty("attachment")
    ATTACHMENT,
    @XmlEnumValue("plaintext")
    @JsonProperty("plaintext")
    PLAIN_TEXT,
    @XmlEnumValue("image")
    @JsonProperty("image")
    IMAGE,
    @XmlEnumValue("audio")
    @JsonProperty("audio")
    AUDIO,
    @XmlEnumValue("video")
    @JsonProperty("video")
    VIDEO,
    @XmlEnumValue("richtext")
    @JsonProperty("richtext")
    RICH_TEXT,
    @XmlEnumValue("presentation")
    @JsonProperty("presentation")
    PRESENTATION,
    @XmlEnumValue("spreadsheet")
    @JsonProperty("spreadsheet")
    SPREADSHEET,
    @XmlEnumValue("pdfdocument")
    @JsonProperty("pdfdocument")
    PDF_DOCUMENT,
    @XmlEnumValue("archive")
    @JsonProperty("archive")
    ARCHIVE,
    @XmlEnumValue("code")
    @JsonProperty("code")
    CODE,
    @XmlEnumValue("markup")
    @JsonProperty("markup")
    MARKUP,
    @XmlEnumValue("message")
    @JsonProperty("message")
    MESSAGE
}