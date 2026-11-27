package com.lmalecic.iis.backend.contenttype.persistence.entity.rule

import com.lmalecic.iis.backend.contenttype.persistence.entity.field.Field
import jakarta.persistence.CheckConstraint
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.MapsId
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.math.BigDecimal

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
class RangeRule(
    @MapsId
    @OneToOne(optional = false)
    @JoinColumn(name = "field_id", nullable = false)
    var field: Field,

    @Column(precision = 38, scale = 10)
    var min: BigDecimal? = null,
    @Column(precision = 38, scale = 10)
    var max: BigDecimal? = null,

    var message: String? = null,
) {
    @Id
    var fieldId: Long? = null
        protected set
}