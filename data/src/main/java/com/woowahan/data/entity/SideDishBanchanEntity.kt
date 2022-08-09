package com.woowahan.data.entity

import com.google.gson.annotations.SerializedName

data class SideDishBanchanEntity(
    @SerializedName("body")
    val body: List<BanchanEntity>,
    @SerializedName("statusCode")
    val statusCode: Int
)