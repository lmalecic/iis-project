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
import java.math.BigInteger

@Entity
@Table(
    check = [
        CheckConstraint(
            constraint = "min IS NULL OR min BETWEEN ${ULong.MIN_VALUE} AND ${ULong.MAX_VALUE}"
        ),
        CheckConstraint(
            constraint = "max IS NULL OR max BETWEEN ${ULong.MIN_VALUE} AND ${ULong.MAX_VALUE}"
        ),
        CheckConstraint(
            constraint = "min IS NOT NULL OR max IS NOT NULL"
        ),
        CheckConstraint(
            constraint = "min IS NULL OR max IS NULL OR min <= max"
        ),
    ]
)
class AssetFileSizeRule(
    @MapsId
    @OneToOne(optional = false)
    @JoinColumn(name = "field_id", nullable = false)
    var field: Field,

    @Column(precision = 20, scale = 0)
    var min: BigInteger? = null,
    @Column(precision = 20, scale = 0)
    var max: BigInteger? = null,

    var message: String? = null,
) {
    @Id
    var fieldId: Long? = null
        protected set
}