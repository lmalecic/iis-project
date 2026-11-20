package com.lmalecic.iis.backend.contenttype.contentful

import tools.jackson.core.JsonParser
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.JsonNode
import tools.jackson.databind.ValueDeserializer

class ContentfulFieldDtoDeserializer : ValueDeserializer<ContentfulFieldDto>() {

    override fun deserialize(
        parser: JsonParser,
        context: DeserializationContext
    ): ContentfulFieldDto {
        val node = context.readTree(parser)

        val id = node.text(ContentfulFieldDto::id.name)
        val name = node.text(ContentfulFieldDto::name.name)

        if (!node.isObject || id == null || name == null) {
            return context.reportInputMismatch(
                ContentfulFieldDto::class.java,
                "Expected a Contentful field object with a string id and name",
            )
        }

        val rawType = node.text(ContentfulFieldDto::type.name)
        val rawLinkType = node.text(LinkFieldDto<*>::linkType.name)

        val items = node.get(ContentfulArrayFieldDto::items.name)
        val rawItemType = items?.text(ContentfulArrayItemsDto::type.name)
        val rawItemLinkType = items?.text(LinkItemsDto::linkType.name)

        val type = ContentfulFieldType.fromValue(rawType)
        val linkType = ContentfulLinkType.fromValue(rawLinkType)
        val itemType = ContentfulFieldType.fromValue(rawItemType)
        val itemLinkType = ContentfulLinkType.fromValue(rawItemLinkType)

        val target = type?.dtoClass(
            linkType = linkType,
            itemType = itemType,
            itemLinkType = itemLinkType,
        )

        if (target == null) {
            return context.reportInputMismatch(
                ContentfulFieldDto::class.java,
                "Unsupported Contentful field '%s': type=%s, linkType=%s, items.type=%s, items.linkType=%s",
                id, rawType, rawLinkType, rawItemType, rawItemLinkType
            )
        }

        return context.readTreeAsValue(node, target)
    }

    private fun JsonNode.text(key: String): String? =
        get(key)?.takeIf { it.isString }?.stringValue()
}