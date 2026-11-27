package com.lmalecic.iis.backend.contenttype.persistence.entity.rule

import com.lmalecic.iis.backend.contenttype.persistence.entity.field.IntegerField
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
class IntegerAllowedValuesRule(
    @MapsId
    @OneToOne(optional = false)
    @JoinColumn(name = "field_id", nullable = false)
    var field: IntegerField,

    @ElementCollection
    @CollectionTable(
        joinColumns = [
            JoinColumn(name = "rule_id", nullable = false)
        ],
        uniqueConstraints = [
            UniqueConstraint(columnNames = ["rule_id", "allowed_value"])
        ]
    )
    @Column(name = "allowed_value", nullable = false)
    var values: MutableSet<Long>,

    var message: String? = null
) {
    @Id
    var fieldId: Long? = null
        protected set
}