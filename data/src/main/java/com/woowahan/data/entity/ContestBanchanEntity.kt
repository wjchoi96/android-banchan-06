package com.woowahan.data.entity


import com.google.gson.annotations.SerializedName

data class ContestBanchanEntity(
    @SerializedName("body")
    val body: List<BanchanSectionEntity>,
    @SerializedName("statusCode")
    val statusCode: Int
) {
    data class BanchanSectionEntity(
        @SerializedName("category_id")
        val categoryId: String,
        @SerializedName("items")
        val items: List<BanchanEntity>,
        @SerializedName("name")
        val name: String
    ) {
    }
}