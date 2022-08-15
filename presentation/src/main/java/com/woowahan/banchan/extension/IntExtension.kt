package com.woowahan.banchan.extension

import android.content.Context
import kotlin.math.roundToInt

fun Int.dp(context: Context?): Int {
    if(context == null) return 0
    val density = context.resources.displayMetrics.density
    return (this.toFloat() * density).roundToInt()
}

