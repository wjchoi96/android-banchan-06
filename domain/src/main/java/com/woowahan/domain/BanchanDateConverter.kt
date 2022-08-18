package com.woowahan.domain

import java.text.SimpleDateFormat
import java.util.*

@Suppress("SimpleDateFormat")
object BanchanDateConverter {
    private const val banchanDateFormatString = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    private val formatter = SimpleDateFormat(banchanDateFormatString).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    fun getSimpleDateFormat(): SimpleDateFormat {
        return formatter
    }

    fun convert(dateStr: String): Date? {
        return try{
            formatter.parse(dateStr)
        }catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun convert(date: Date): String {
        return try {
            formatter.format(date)
        }catch (e: Exception){
            e.printStackTrace()
            ""
        }
    }
}