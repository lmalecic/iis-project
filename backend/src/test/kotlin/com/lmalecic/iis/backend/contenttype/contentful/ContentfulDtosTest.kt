package com.lmalecic.iis.backend.contenttype.contentful

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
            total = 2,
            skip = 0,
            limit = 100,
            items = listOf(
                ContentfulContentTypeDto(
                    sys = ContentfulContentTypeSysDto("review"),
                    name = "Review",
                    description = "",
                    displayField = "text",
                    fields = listOf(
                        EntryFieldDto(
                            id = "game",
                            name = "Game",
                            required = true,
                            validations = listOf(Rule.LinkContentTypeRule(listOf("game"))),
                        ),
                        TextFieldDto(
                            id = "text",
                            name = "Text",
                            validations = listOf(Rule.SizeRule(Rule.Bounds(min = 0L, max = 1000L))),
                        ),
                        IntegerFieldDto(
                            id = "rating",
                            name = "Rating",
                            required = true,
                            validations = listOf(
                                Rule.RangeRule(
                                    Rule.Bounds(min = 1.toBigDecimal(), max = 5.toBigDecimal()),
                                ),
                            ),
                        ),
                        BooleanFieldDto(id = "recommended", name = "Recommended", required = true),
                    ),
                ),
                ContentfulContentTypeDto(
                    sys = ContentfulContentTypeSysDto("game"),
                    name = "Game",
                    description = "",
                    displayField = "title",
                    fields = listOf(
                        SymbolFieldDto(id = "title", name = "Title", required = true),
                        SymbolArrayFieldDto(
                            id = "platforms",
                            name = "Platforms",
                            items = SymbolItemsDto(
                                validations = listOf(
                                    Rule.TextAllowedValuesRule(listOf("PC", "Console", "Mobile")),
                                ),
                            ),
                            required = true,
                        ),
                        SymbolArrayFieldDto(
                            id = "genres",
                            name = "Genres",
                            items = SymbolItemsDto(
                                validations = listOf(
                                    Rule.TextAllowedValuesRule(
                                        listOf(
                                            "Action", "Adventure", "Role-playing (RPG)",
                                            "Strategy", "Simulation", "Puzzle", "Platformer",
                                            "Shooter", "Fighting", "Racing", "Sports", "Horror",
                                        ),
                                    ),
                                ),
                            ),
                        ),
                        DateFieldDto(
                            id = "releaseDate",
                            name = "Release date",
                            validations = listOf(
                                Rule.DateRangeRule(
                                    Rule.DateRangeRule.Bounds(min = "1950-01-01"),
                                ),
                            ),
                        ),
                        BooleanFieldDto(id = "multiplayer", name = "Multiplayer", required = true),
                    ),
                ),
            ),
        )

        assertThat(actual)
            .usingRecursiveComparison()
            .withStrictTypeChecking()
            .isEqualTo(expected)
    }
}
