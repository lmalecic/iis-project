package com.lmalecic.iis.backend.contenttype.persistence.entity.rule

import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.MapsId
import jakarta.persistence.OneToOne

@Entity
class RichTextNodeLinkContentTypeRule(
    @MapsId
    @OneToOne(optional = false)
    @JoinColumn(name = "node_id", nullable = false)
    var node: RichTextEntryNodeRestriction,

    @ElementCollection
    @CollectionTable(joinColumns = [JoinColumn(name = "node_id", nullable = false)])
    @Column(name = "content_type_id", nullable = false, length = 64)
    var contentTypeIds: MutableSet<String>,

    var message: String? = null,
) {
    @Id
    var nodeId: Long? = null
        protected set
}