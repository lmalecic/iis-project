package com.lmalecic.iis.backend.contenttype.persistence.entity.rule

import com.lmalecic.iis.backend.contenttype.Mimetype
import com.lmalecic.iis.backend.contenttype.persistence.entity.field.Field
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.MapsId
import jakarta.persistence.OneToOne

@Entity
class LinkMimetypeGroupRule(
    @MapsId
    @OneToOne(optional = false)
    @JoinColumn(name = "field_id", nullable = false)
    var field: Field,

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @Column(name = "mimetype", nullable = false)
    var groups: MutableSet<Mimetype>,

    var message: String? = null
) {
    @Id
    var fieldId: Long? = null
        protected set
}