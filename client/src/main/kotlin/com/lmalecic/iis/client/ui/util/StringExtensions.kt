package com.lmalecic.iis.client.ui.util

fun String.pluralize(count: Int, pluralForm: String? = null): String {
    val isSingular = count % 10 == 1 && count % 100 != 11
    return if (isSingular) {
        this
    } else {
        pluralForm ?: "${this}s"
    }
}

fun String.pluralizeWithCount(count: Int, pluralForm: String? = null): String {
    return "$count ${this.pluralize(count, pluralForm)}"
}