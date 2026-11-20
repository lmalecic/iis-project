package com.lmalecic.iis.backend.contenttype.contentful

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tools.jackson.databind.module.SimpleModule

@Configuration(proxyBeanMethods = false)
class ContentfulJacksonConfiguration {
    @Bean
    fun contentfulValidationModule(): SimpleModule = SimpleModule("ContentfulValidation")
        .apply {
            addDeserializer(ContentfulFieldDto::class.java, ContentfulFieldDtoDeserializer())

            registerRuleType(ContentfulValidationDto.Symbol::class.java)
            registerRuleType(ContentfulValidationDto.Text::class.java)
            registerRuleType(ContentfulValidationDto.Integer::class.java)
            registerRuleType(ContentfulValidationDto.Decimal::class.java)
            registerRuleType(ContentfulValidationDto.Date::class.java)
            registerRuleType(ContentfulValidationDto.Size::class.java)
            registerRuleType(ContentfulValidationDto.RichText::class.java)
            registerRuleType(ContentfulValidationDto.Asset::class.java)
            registerRuleType(ContentfulValidationDto.Entry::class.java)
            registerRuleType(ContentfulValidationDto.RichTextEntry::class.java)
            registerRuleType(ContentfulValidationDto.None::class.java)
        }

    private fun <R : ContentfulValidationDto> SimpleModule.registerRuleType(type: Class<R>) {
        addDeserializer(type, ContentfulValidationDtoDeserializer(type))
    }
}