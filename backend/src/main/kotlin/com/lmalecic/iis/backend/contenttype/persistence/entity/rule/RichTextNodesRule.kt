package com.lmalecic.iis.backend.contenttype.persistence.entity.rule

import com.lmalecic.iis.backend.contenttype.persistence.entity.field.Field
import com.lmalecic.iis.backend.contenttype.persistence.entity.field.RichTextField
import jakarta.persistence.CascadeType
import jakarta.persistence.CheckConstraint
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.MapsId
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
class RichTextNodesRule(
    @MapsId
    @OneToOne(optional = false)
    @JoinColumn(name = "field_id", nullable = false)
    var field: RichTextField,

    @Embedded
    var restrictions: RichTextNodeRestrictions? = null,

    var message: String? = null
) {
    @Id
    var fieldId: Long? = null
        protected set
}

@Embeddable
class RichTextNodeRestrictions(
    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    var embeddedEntryBlock: RichTextEntryNodeRestriction? = null,

    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    var embeddedEntryInline: RichTextEntryNodeRestriction? = null,

    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    var entryHyperlink: RichTextEntryNodeRestriction? = null,

    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    var embeddedAssetBlock: RichTextAssetNodeRestriction? = null,

    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    var assetHyperlink: RichTextAssetNodeRestriction? = null,

)

@Entity
@Table(
    check = [
        CheckConstraint(
            constraint = "min_count IS NULL OR max_count IS NULL OR min_count <= max_count",
        ),
        CheckConstraint(
            constraint = "size_message IS NULL OR min_count IS NOT NULL OR max_count IS NOT NULL"
        )
    ]
)
class RichTextEntryNodeRestriction(
    @Embedded
    var size: RichTextNodeSize? = null,

    @OneToOne(mappedBy = "node", cascade = [CascadeType.ALL], orphanRemoval = true)
    var linkContentType: RichTextNodeLinkContentTypeRule? = null,

    var message: String? = null,
) {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set
}

@Entity
@Table(
    check = [
        CheckConstraint(constraint = "min_count IS NOT NULL OR max_count IS NOT NULL"),
        CheckConstraint(constraint = "min_count IS NULL OR max_count IS NULL OR min_count <= max_count")
    ]
)
class RichTextAssetNodeRestriction(
    @Embedded
    var size: RichTextNodeSize,
) {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set
}

@Embeddable
class RichTextNodeSize(
    @Column(name = "min_count")
    var min: Long? = null,

    @Column(name = "max_count")
    var max: Long? = null,

    @Column(name = "size_message")
    var message: String? = null,
)
