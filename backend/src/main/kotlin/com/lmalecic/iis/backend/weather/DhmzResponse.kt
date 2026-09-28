package com.lmalecic.iis.backend.weather

import jakarta.xml.bind.annotation.XmlAccessType
import jakarta.xml.bind.annotation.XmlAccessorType
import jakarta.xml.bind.annotation.XmlElement
import jakarta.xml.bind.annotation.XmlRootElement

@XmlRootElement(name = "Hrvatska")
@XmlAccessorType(XmlAccessType.FIELD)
class DhmzResponse {

    @field:XmlElement(name = "Grad")
    var cities: MutableList<DhmzCity> = mutableListOf()
}

@XmlAccessorType(XmlAccessType.FIELD)
class DhmzCity {

    @field:XmlElement(name = "GradIme")
    var name: String? = null

    @field:XmlElement(name = "Podatci")
    var data: DhmzData? = null
}

@XmlAccessorType(XmlAccessType.FIELD)
class DhmzData {

    @field:XmlElement(name = "Temp")
    var temperature: String? = null
}