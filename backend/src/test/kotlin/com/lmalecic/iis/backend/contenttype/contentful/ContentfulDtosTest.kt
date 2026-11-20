package com.lmalecic.iis.backend.contenttype.contentful

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
        val expected = ContentfulContentTypesResponse(
            total = 2,
            skip = 0,
            limit = 100,
            items = listOf(
                ContentfulContentTypeDto(
                    sys = ContentfulContentTypeSysDto(id = "review"),
                    name = "Review",
                    description = "",
                    displayField = "text",
                    fields = listOf(
                        ContentfulFieldDto(
                            id = "game",
                            name = "Game",
                            type = ContentfulFieldType.LINK,
                            required = true,
                            linkType = "Entry",
                            validations = listOf(
                                ContentfulValidationDto.LinkContentTypeRule(listOf("game")),
                            ),
                        ),
                        ContentfulFieldDto(
                            id = "text",
                            name = "Text",
                            type = ContentfulFieldType.TEXT,
                            required = false,
                            validations = listOf(
                                ContentfulValidationDto.SizeRule(ContentfulValidationDto.Bounds(0, 1000))
                            ),
                        ),
                        ContentfulFieldDto(
                            id = "rating",
                            name = "Rating",
                            type = ContentfulFieldType.INTEGER,
                            required = true,
                            validations = listOf(
                                ContentfulValidationDto.RangeRule(ContentfulValidationDto.Bounds(1.toBigDecimal(), 5.toBigDecimal()))
                            ),
                        ),
                        ContentfulFieldDto(
                            id = "recommended",
                            name = "Recommended",
                            type = ContentfulFieldType.BOOLEAN,
                            required = true,
                        ),
                    ),
                ),
                ContentfulContentTypeDto(
                    sys = ContentfulContentTypeSysDto(id = "game"),
                    name = "Game",
                    description = "",
                    displayField = "title",
                    fields = listOf(
                        ContentfulFieldDto(
                            id = "title",
                            name = "Title",
                            type = ContentfulFieldType.SYMBOL,
                            required = true,
                        ),
                        ContentfulFieldDto(
                            id = "platforms",
                            name = "Platforms",
                            type = ContentfulFieldType.ARRAY,
                            required = true,
                            items = ContentfulArrayItemsDto(
                                type = ContentfulFieldType.SYMBOL,
                                validations = listOf(
                                    // This might be problematic
                                    ContentfulValidationDto.AllowedValuesRule(listOf(jsonMapper.readTree("""["PC","Console","Mobile"]"""))),
                                ),
                            ),
                        ),
                        ContentfulFieldDto(
                            id = "genres",
                            name = "Genres",
                            type = ContentfulFieldType.ARRAY,
                            required = false,
                            items = ContentfulArrayItemsDto(
                                type = ContentfulFieldType.SYMBOL,
                                validations = listOf(
                                        ContentfulValidationDto.AllowedValuesRule(listOf(
                                            jsonMapper.readTree("""
                                                      [
                                                        "Action", "Adventure", "Role-playing (RPG)",
                                                        "Strategy", "Simulation", "Puzzle",
                                                        "Platformer", "Shooter", "Fighting",
                                                        "Racing", "Sports", "Horror"
                                                      ]
                                                    """.trimIndent()
                                            )
                                        )
                                    ),
                                )
                            ),
                        ),
                        ContentfulFieldDto(
                            id = "releaseDate",
                            name = "Release date",
                            type = ContentfulFieldType.DATE,
                            required = false,
                            validations = listOf(
                                ContentfulValidationDto.DateRangeRule(ContentfulValidationDto.DateRangeRule.Bounds("1950-01-01"))
                            ),
                        ),
                        ContentfulFieldDto(
                            id = "multiplayer",
                            name = "Multiplayer",
                            type = ContentfulFieldType.BOOLEAN,
                            required = true,
                        ),
                    ),
                ),
            ),
        )

        val response = requireNotNull(
            javaClass.getResourceAsStream("/contentful/content-types.json")
        ) { "Missing Contentful response fixture" }.use { input ->
            jsonMapper.readValue(input, ContentfulContentTypesResponse::class.java)
        }

        assertThat(response).isEqualTo(expected)
    }
}