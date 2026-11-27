package com.lmalecic.iis.backend.contenttype.persistence.entity.field

import com.lmalecic.iis.backend.contenttype.persistence.entity.ContentType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.hibernate.annotations.ColumnDefault

@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["content_type_id", "identifier"]),
    ]
)
@Inheritance(strategy = InheritanceType.JOINED)
abstract class Field(
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(nullable = false)
    var contentType: ContentType,

    @Column(nullable = false, length = 64)
    var identifier: String,

    @Column(nullable = false, length = 50)
    var name: String,
) {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    @Column(nullable = false)
    var order: Int = 0

    @Column(nullable = false)
    @ColumnDefault("false")
    var required: Boolean = false

    @Column(nullable = false)
    @ColumnDefault("false")
    var localized: Boolean = false

    @Column(nullable = false)
    @ColumnDefault("false")
    var disabled: Boolean = false

    @Column(nullable = false)
    @ColumnDefault("false")
    var omitted: Boolean = false

    var deleted: Boolean? = null
}
