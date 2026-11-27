package com.lmalecic.iis.backend.contenttype.persistence.entity

import com.lmalecic.iis.backend.contenttype.persistence.entity.field.Field
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.MapKey
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.OrderBy

@Entity
class ContentType(
    @Id @Column(length = 64)
    var id: String,

    @Column(nullable = false, length = 50)
    var name: String,

    @Column(length = 500)
    var description: String? = null
) {
    @OneToOne(fetch = FetchType.EAGER)
    var displayField: Field? = null

    @OneToMany(
        mappedBy = "contentType",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    @MapKey(name = "identifier")
    @OrderBy("order ASC")
    var fields: MutableMap<String, Field> = linkedMapOf()
}