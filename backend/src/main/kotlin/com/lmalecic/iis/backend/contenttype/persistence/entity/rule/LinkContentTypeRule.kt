package com.lmalecic.iis.backend.contenttype.persistence.entity.rule

import com.lmalecic.iis.backend.contenttype.persistence.entity.field.Field
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.MapsId
import jakarta.persistence.OneToOne
import jakarta.persistence.UniqueConstraint

@Entity
class LinkContentTypeRule(
    @MapsId
    @OneToOne(optional = false)
    @JoinColumn(name = "field_id", nullable = false)
    var field: Field,

    @ElementCollection
    @CollectionTable(
        joinColumns = [
            JoinColumn(name = "rule_id", nullable = false)
        ],
        uniqueConstraints = [
            UniqueConstraint(columnNames = ["rule_id", "content_type_id"])
        ]
    )
    @Column(name = "content_type_id", nullable = false)
    var contentTypeIds: MutableSet<String>,

    var message: String? = null
) {
    @Id
    var fieldId: Long? = null
        protected set
}