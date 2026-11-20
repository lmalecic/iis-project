package com.lmalecic.iis.backend.contenttype.contentful

import tools.jackson.core.JsonParser
import tools.jackson.databind.DeserializationContext
import tools.jackson.databind.JsonNode
import tools.jackson.databind.ValueDeserializer

class ContentfulValidationDtoDeserializer<R : ContentfulValidationDto>(
    private val expectedType: Class<R>
) : ValueDeserializer<R>() {

    override fun deserialize(
        parser: JsonParser,
        context: DeserializationContext
    ): R {
        val node = context.readTree(parser)

        if (!node.isObject) {
            return context.reportInputMismatch(
                ContentfulValidationDto::class.java,
                "Expected a Contentful validation object"
            )
        }

        val ruleKeys = node.properties()
            .map { it.key }
            .filterNot { it == ContentfulValidationDto::message.name }

        val ruleKey = ruleKeys.singleOrNull() ?: return context.reportInputMismatch(
            expectedType,
            "Expected one rule key per Contentful validation object, found %s",
            ruleKeys
        )

        val target = if (ruleKey == ALLOWED_VALUES_KEY) {
            allowedValuesTarget(node, context)
        } else {
            ruleTypes[ruleKey] ?: return context.reportInputMismatch(
                expectedType,
                "Unsupported Contentful validation rule '%s'",
                ruleKey
            )
        }

        if (!expectedType.isAssignableFrom(target)) {
            return context.reportInputMismatch(
                expectedType,
                "Rule '%s' is incompatible with %s",
                ruleKey,
                expectedType.simpleName
            )
        }

        return context.readTreeAsValue(node, target.asSubclass(expectedType))
    }

    private fun allowedValuesTarget(
        node: JsonNode,
        context: DeserializationContext
    ): Class<out ContentfulValidationDto> {
        val values = node.get(ALLOWED_VALUES_KEY)
        if (values == null || !values.isArray) {
            return context.reportInputMismatch(
                expectedType,
                "Expected an array under '%s'",
                ALLOWED_VALUES_KEY
            )
        }

        val target: Class<out ContentfulValidationDto>
        val expectedValue: String
        val accepts: (JsonNode) -> Boolean

        when (expectedType) {
            ContentfulValidationDto.Symbol::class.java, ContentfulValidationDto.Text::class.java -> {
                target = ContentfulValidationDto.TextAllowedValuesRule::class.java
                expectedValue = "a JSON string"
                accepts = { it.isString }
            }

            ContentfulValidationDto.Integer::class.java -> {
                target = ContentfulValidationDto.IntegerAllowedValuesRule::class.java
                expectedValue = "a JSON integer"
                accepts = { it.isIntegralNumber }
            }

            ContentfulValidationDto.Decimal::class.java -> {
                target = ContentfulValidationDto.DecimalAllowedValuesRule::class.java
                expectedValue = "a JSON number"
                accepts = { it.isNumber }
            }

            else -> return context.reportInputMismatch(
                expectedType,
                "Rule '%s' requires a Symbol, Text, Integer or Decimal validation type",
                ALLOWED_VALUES_KEY
            )
        }

        for ((index, value) in values.withIndex()) {
            if (!accepts(value)) {
                return context.reportInputMismatch(
                    expectedType,
                    "Expected %s at '%s[%d]' for %s",
                    expectedValue,
                    ALLOWED_VALUES_KEY,
                    index,
                    expectedType.simpleName
                )
            }
        }

        return target
    }

    private companion object {
        const val ALLOWED_VALUES_KEY = "in"

        val ruleTypes: Map<String, Class<out ContentfulValidationDto>> = mapOf(
            ContentfulValidationDto.UniqueRule::unique.name to ContentfulValidationDto.UniqueRule::class.java,
            ContentfulValidationDto.SizeRule::size.name to ContentfulValidationDto.SizeRule::class.java,
            ContentfulValidationDto.RangeRule::range.name to ContentfulValidationDto.RangeRule::class.java,
            ContentfulValidationDto.DateRangeRule::dateRange.name to ContentfulValidationDto.DateRangeRule::class.java,
            ContentfulValidationDto.RegexRule::regexp.name to ContentfulValidationDto.RegexRule::class.java,
            ContentfulValidationDto.ProhibitRegexRule::prohibitRegexp.name to ContentfulValidationDto.ProhibitRegexRule::class.java,
            ContentfulValidationDto.LinkContentTypeRule::linkContentType.name to ContentfulValidationDto.LinkContentTypeRule::class.java,
            ContentfulValidationDto.LinkMimetypeGroupRule::linkMimetypeGroup.name to ContentfulValidationDto.LinkMimetypeGroupRule::class.java,
            ContentfulValidationDto.AssetFileSizeRule::assetFileSize.name to ContentfulValidationDto.AssetFileSizeRule::class.java,
            ContentfulValidationDto.AssetImageDimensionsRule::assetImageDimensions.name to ContentfulValidationDto.AssetImageDimensionsRule::class.java,
            ContentfulValidationDto.EnabledMarksRule::enabledMarks.name to ContentfulValidationDto.EnabledMarksRule::class.java,
            ContentfulValidationDto.EnabledNodeTypesRule::enabledNodeTypes.name to ContentfulValidationDto.EnabledNodeTypesRule::class.java,
            ContentfulValidationDto.NodesRule::nodes.name to ContentfulValidationDto.NodesRule::class.java,
        )
    }
}