package com.lmalecic.iis.backend.contenttype.contentful

import com.lmalecic.iis.backend.contenttype.Mark
import com.lmalecic.iis.backend.contenttype.Mimetype
import com.lmalecic.iis.backend.contenttype.NodeType
import com.lmalecic.iis.backend.contenttype.contentful.ContentfulValidationDto as Rule
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.JsonTest
import org.springframework.context.annotation.Import
import tools.jackson.databind.json.JsonMapper

@JsonTest
@Import(ContentfulJacksonConfiguration::class)
class ContentfulDtosTest @Autowired constructor(
    private val jsonMapper: JsonMapper,
) {
    @Test
    fun `deserializes Contentful content types response`() {
        val actual = requireNotNull(
            javaClass.getResourceAsStream("/contentful/content-types.json"),
        ) { "Missing Contentful response fixture" }.use { input ->
            jsonMapper.readValue(input, ContentfulContentTypesResponse::class.java)
        }

        val expected = ContentfulContentTypesResponse(
            total = 1,
            skip = 0,
            limit = 100,
            items = listOf(
                ContentfulContentTypeDto(
                    sys = ContentfulContentTypeSysDto("fieldTypeSandbox"),
                    name = "Field Type Sandbox",
                    description = "",
                    displayField = "shortLabel",
                    fields = listOf(
                        expectedRichBody(),
                        SymbolFieldDto(
                            id = "shortLabel",
                            name = "Short label",
                            validations = listOf(Rule.UniqueRule(unique = true)),
                        ),
                        TextFieldDto(id = "longNotes", name = "Long notes"),
                        SymbolArrayFieldDto(
                            id = "shortTags",
                            name = "Short tags",
                            localized = true,
                            validations = listOf(Rule.SizeRule(Rule.Bounds(min = 1L, max = 4L))),
                            items = SymbolItemsDto(
                                validations = listOf(Rule.SizeRule(Rule.Bounds(min = 2L, max = 12L))),
                            ),
                        ),
                        IntegerFieldDto(
                            id = "wholeScore",
                            name = "Whole score",
                            validations = listOf(
                                Rule.RangeRule(
                                    Rule.Bounds(min = 0.toBigDecimal()),
                                    message = "Whole score must be greater or equal than minimum",
                                ),
                            ),
                        ),
                        DecimalFieldDto(
                            id = "decimalScore",
                            name = "Decimal score",
                            validations = listOf(
                                Rule.RangeRule(
                                    Rule.Bounds(min = "0.25".toBigDecimal(), max = "9.75".toBigDecimal()),
                                ),
                                Rule.DecimalAllowedValuesRule(
                                    listOf(1.toBigDecimal(), 2.toBigDecimal(), 3.toBigDecimal(), (-3).toBigDecimal()),
                                ),
                            ),
                        ),
                        DateFieldDto(
                            id = "sampleDate",
                            name = "Sample date",
                            validations = listOf(
                                Rule.DateRangeRule(
                                    Rule.DateRangeRule.Bounds(min = "2026-11-01T00:30:12+00:00"),
                                ),
                            ),
                        ),
                        LocationFieldDto(id = "sampleLocation", name = "Sample location"),
                        AssetFieldDto(
                            id = "coverAsset",
                            name = "Cover asset",
                            validations = listOf(
                                Rule.LinkMimetypeGroupRule(
                                    linkMimetypeGroup = listOf(
                                        Mimetype.ATTACHMENT, Mimetype.PLAIN_TEXT, Mimetype.IMAGE,
                                        Mimetype.AUDIO, Mimetype.VIDEO, Mimetype.RICH_TEXT,
                                        Mimetype.PRESENTATION, Mimetype.SPREADSHEET, Mimetype.PDF_DOCUMENT,
                                        Mimetype.ARCHIVE, Mimetype.CODE, Mimetype.MARKUP, Mimetype.MESSAGE,
                                    ),
                                    message = "MIMETYPE NOT ALLOWED!",
                                ),
                            ),
                        ),
                        AssetArrayFieldDto(
                            id = "galleryAssets",
                            name = "Gallery assets",
                            validations = listOf(Rule.SizeRule(Rule.Bounds(min = 1L, max = 4L))),
                            items = AssetItemsDto(
                                validations = listOf(Rule.LinkMimetypeGroupRule(listOf(Mimetype.IMAGE))),
                            ),
                        ),
                        BooleanFieldDto(
                            id = "featuredFlag",
                            name = "Featured flag",
                            defaultValue = mapOf("en-US" to false),
                        ),
                        ObjectFieldDto(id = "extraJson", name = "Extra JSON"),
                        EntryFieldDto(
                            id = "relatedGame",
                            name = "Related game",
                            validations = listOf(Rule.LinkContentTypeRule(listOf("game"))),
                        ),
                        EntryArrayFieldDto(
                            id = "relatedGames",
                            name = "Related games",
                            validations = listOf(Rule.SizeRule(Rule.Bounds(min = 1L, max = 3L))),
                            items = EntryItemsDto(
                                validations = listOf(Rule.LinkContentTypeRule(listOf("game"))),
                            ),
                        ),
                        SymbolFieldDto(id = "test", name = "test"),
                        DateFieldDto(
                            id = "dateTimeTest",
                            name = "DateTimeTest",
                            validations = listOf(
                                Rule.DateRangeRule(Rule.DateRangeRule.Bounds(min = "2026-11-01")),
                            ),
                        ),
                    ),
                ),
            ),
        )

        assertThat(actual)
            .usingRecursiveComparison()
            .withStrictTypeChecking()
            .isEqualTo(expected)
    }

    private fun expectedRichBody() = RichTextFieldDto(
        id = "richBody",
        name = "Rich body",
        required = true,
        validations = listOf(
            Rule.SizeRule(Rule.Bounds(min = 1L, max = 10L)),
            Rule.EnabledMarksRule(
                enabledMarks = listOf(
                    Mark.BOLD, Mark.ITALIC, Mark.UNDERLINE, Mark.CODE,
                    Mark.SUPERSCRIPT, Mark.SUBSCRIPT, Mark.STRIKETHROUGH,
                ),
                message = "Only bold, italic, underline, code, superscript, " +
                    "subscript, and strikethrough marks are allowed",
            ),
            Rule.EnabledNodeTypesRule(
                enabledNodeTypes = listOf(
                    NodeType.HEADING_1, NodeType.HEADING_2, NodeType.HEADING_3,
                    NodeType.HEADING_4, NodeType.HEADING_5, NodeType.HEADING_6,
                    NodeType.ORDERED_LIST, NodeType.UNORDERED_LIST, NodeType.HR,
                    NodeType.BLOCKQUOTE, NodeType.EMBEDDED_ENTRY_BLOCK,
                    NodeType.EMBEDDED_ASSET_BLOCK, NodeType.TABLE,
                    NodeType.ASSET_HYPERLINK, NodeType.EMBEDDED_ENTRY_INLINE,
                    NodeType.ENTRY_HYPERLINK, NodeType.HYPERLINK,
                ),
                message = "Only heading 1, heading 2, heading 3, heading 4, " +
                    "heading 5, heading 6, ordered list, unordered list, horizontal rule, " +
                    "quote, block entry, asset, table, link to asset, inline entry, " +
                    "link to entry, and link to Url nodes are allowed",
            ),
            Rule.NodesRule(
                Rule.NodesRule.Restrictions(
                    embeddedEntryBlock = listOf(
                        Rule.LinkContentTypeRule(listOf("game", "review"), message = "Embed a Game entry."),
                        Rule.SizeRule(Rule.Bounds(min = 1L, max = 100L)),
                    ),
                    embeddedEntryInline = listOf(Rule.SizeRule(Rule.Bounds(min = 0L, max = 2L))),
                    entryHyperlink = listOf(
                        Rule.LinkContentTypeRule(listOf("fieldTypeSandbox", "game")),
                        Rule.SizeRule(Rule.Bounds(min = 1L, max = 100L)),
                    ),
                    embeddedAssetBlock = listOf(Rule.SizeRule(Rule.Bounds(min = 0L, max = 3L))),
                    assetHyperlink = listOf(Rule.SizeRule(Rule.Bounds(min = 1L, max = 100L))),
                ),
            ),
        ),
    )
}
