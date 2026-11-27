package com.lmalecic.iis.backend.contenttype.persistence.entity.rule

import com.lmalecic.iis.backend.contenttype.persistence.entity.field.ArrayField
import jakarta.persistence.CheckConstraint
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.MapsId
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(
    check = [
        CheckConstraint(
            constraint = "min IS NOT NULL OR max IS NOT NULL"
        ),
        CheckConstraint(
            constraint = "min IS NULL OR max IS NULL OR min <= max"
        ),
    ]
)
class ArraySizeRule(
    @MapsId
    @OneToOne(optional = false)
    @JoinColumn(name = "field_id", nullable = false)
    var field: ArrayField,

    var min: Long? = null,
    var max: Long? = null,
    var message: String? = null,
) {
    @Id
    var fieldId: Long? = null
        protected set
}