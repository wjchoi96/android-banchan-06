package com.woowahan.data.entity


import com.google.gson.annotations.SerializedName

data class MainDishBanchanEntity(
    @SerializedName("body")
    val body: List<BanchanEntity>,
    @SerializedName("statusCode")
    val statusCode: Int
)