package com.woowahan.domain.extension

fun String.priceStrToLong(): Long {
    val temp = this.filter { it.isDigit() }
    return if (temp.isBlank()) 0L else temp.toLong()
}
