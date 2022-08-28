package com.woowahan.domain.model

import java.io.Serializable

abstract class BaseBanchan: Serializable {
    abstract val hash: String
    abstract val imageUrl: String
    abstract val title: String
    abstract val price: Long
    abstract val salePrice: Long

    val salePercent: Int
        get() {
            return if (salePrice != 0L) {
                val saleValue = (price - salePrice).toFloat()
                (saleValue / price * 100).toInt()
            } else
                0
        }
}