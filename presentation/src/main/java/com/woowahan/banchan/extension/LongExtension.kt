package com.woowahan.banchan.extension

import java.text.DecimalFormat

fun Long.toCashString(): String {
    return DecimalFormat("###,###.####").format(this)
}