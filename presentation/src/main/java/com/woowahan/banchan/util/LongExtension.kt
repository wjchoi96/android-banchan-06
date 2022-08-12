package com.woowahan.banchan.util

import java.text.DecimalFormat

fun Long.toCashString(): String {
    return DecimalFormat("###,###.####").format(this)
}