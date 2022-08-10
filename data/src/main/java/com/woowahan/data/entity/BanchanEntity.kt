package com.woowahan.data.entity

import com.google.gson.annotations.SerializedName
import com.woowahan.domain.model.BanchanModel

data class BanchanEntity(
    @SerializedName("alt")
    val alt: String,
    @SerializedName("badge")
    val badge: List<String>,
    @SerializedName("delivery_type")
    val deliveryType: List<String>,
    @SerializedName("description")
    val description: String,
    @SerializedName("detail_hash")
    val detailHash: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("n_price")
    val nPrice: String?,
    @SerializedName("s_price")
    val sPrice: String,
    @SerializedName("title")
    val title: String
){
    fun toDomain(): BanchanModel = BanchanModel(
        hash = this.detailHash,
        title = this.title,
        description = this.description,
        imageUrl = this.image,
        price = if(!nPrice.isNullOrBlank()) nPrice else sPrice,
        salePrice = if(!nPrice.isNullOrBlank()) sPrice else null,
    )
}