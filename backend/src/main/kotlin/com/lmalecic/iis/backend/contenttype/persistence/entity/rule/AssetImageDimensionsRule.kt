package com.lmalecic.iis.backend.contenttype.persistence.entity.rule

import com.lmalecic.iis.backend.contenttype.persistence.entity.field.Field
import jakarta.persistence.AttributeOverride
import jakarta.persistence.AttributeOverrides
import jakarta.persistence.CheckConstraint
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.Embedded
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
            constraint = "width_min IS NULL OR width_min BETWEEN ${UInt.MIN_VALUE} AND ${UInt.MAX_VALUE}"
        ),
        CheckConstraint(
            constraint = "width_max IS NULL OR width_max BETWEEN ${UInt.MIN_VALUE} AND ${UInt.MAX_VALUE}"
        ),
        CheckConstraint(
            constraint = "height_min IS NULL OR height_min BETWEEN ${UInt.MIN_VALUE} AND ${UInt.MAX_VALUE}"
        ),
        CheckConstraint(
            constraint = "height_max IS NULL OR height_max BETWEEN ${UInt.MIN_VALUE} AND ${UInt.MAX_VALUE}"
        ),
        CheckConstraint(
            constraint = "width_min IS NOT NULL OR width_max IS NOT NULL OR " +
                    "height_min IS NOT NULL OR height_max IS NOT NULL"
        ),
        CheckConstraint(
            constraint = "width_min IS NULL OR width_max IS NULL OR width_min <= width_max"
        ),
        CheckConstraint(
            constraint = "height_min IS NULL OR height_max IS NULL OR height_min <= height_max"
        ),
    ]
)
class AssetImageDimensionsRule(
    @MapsId
    @OneToOne(optional = false)
    @JoinColumn(name = "field_id", nullable = false)
    var field: Field,

    @Embedded
    @AttributeOverrides(
        value = [
            AttributeOverride(name = "min", column = Column(name = "width_min")),
            AttributeOverride(name = "max", column = Column(name = "width_max")),
        ]
    )
    var width: ImageDimensionBounds? = null,

    @Embedded
    @AttributeOverrides(
        value = [
            AttributeOverride(name = "min", column = Column(name = "height_min")),
            AttributeOverride(name = "max", column = Column(name = "height_max")),
        ]
    )
    var height: ImageDimensionBounds? = null,

    var message: String? = null,
) {
    @Id
    var fieldId: Long? = null
        protected set
}

@Embeddable
class ImageDimensionBounds(
    var min: Long? = null,
    var max: Long? = null,
)