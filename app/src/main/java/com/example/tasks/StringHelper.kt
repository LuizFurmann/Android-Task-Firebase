package com.example.tasks

import java.text.Normalizer


object StringHelper {
    fun getString(value: String?): String {
        return value ?: ""
    }

    fun truncateTwoDecimalPlaces(value: String): String {
        val integer = value.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        val decimal = if (value.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray().size > 1) value.split("\\.".toRegex())
            .dropLastWhile { it.isEmpty() }
            .toTypedArray()[1] else ""
        return integer + "." + if (decimal.length > 2) decimal.substring(0, 2) else decimal
    }

    fun removeDiacriticalMarks(string: String?): String {
        return Normalizer.normalize(string, Normalizer.Form.NFD)
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
    }

    fun getFilterText(text: String?): String {
        return if (text == null) "" else removeDiacriticalMarks(text.lowercase())
    }
}
