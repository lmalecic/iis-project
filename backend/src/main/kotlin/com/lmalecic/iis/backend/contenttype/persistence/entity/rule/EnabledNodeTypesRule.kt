package com.lmalecic.iis.backend.contenttype.persistence.entity.rule

import com.lmalecic.iis.backend.contenttype.NodeType
import com.lmalecic.iis.backend.contenttype.persistence.entity.field.Field
import com.lmalecic.iis.backend.contenttype.persistence.entity.field.RichTextField
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
class EnabledNodeTypesRule(
    @MapsId
    @OneToOne(optional = false)
    @JoinColumn(name = "field_id", nullable = false)
    var field: RichTextField,

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @Column(name = "node_type", nullable = false)
    var nodeTypes: MutableSet<NodeType>,

    var message: String? = null
) {
    @Id
    var fieldId: Long? = null
        protected set
}