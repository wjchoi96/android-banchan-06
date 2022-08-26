package com.woowahan.banchan.extension

import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

fun Date.getTimeString(): String {
    val now = Calendar.getInstance()
    val lastDateMill = now.timeInMillis - this.time
    val day = TimeUnit.MILLISECONDS.toDays(lastDateMill)
    Timber.tag("processLastUpdateDate").d("raw day => $day")
    val year = day/365
    return when {
        day <1 -> {
            val hour = TimeUnit.MILLISECONDS.toHours(lastDateMill)
            if(hour>0)
                "${hour}시간전"
            else{
                val min = TimeUnit.MILLISECONDS.toMinutes(lastDateMill)
                if (min == 0L){
                    "방금전"
                }else {
                    "${min}분전"
                }
            }
        }
        year>0 -> {
            Timber.tag("processLastUpdateDate").d("${year}년전")
            "${year}년전"
        }
        else -> {
            val month = (day / 30.417).roundToInt()
            when {
                month>0 -> {
                    Timber.tag("processLastUpdateDate").d("${month}달전")
                    "${month}달전"
                }
                else -> {
                    Timber.tag("processLastUpdateDate").d("${day}일전")
                    "${day}일전"
                }
            }
        }
    }
}