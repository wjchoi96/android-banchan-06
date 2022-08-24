package com.woowahan.data.entity


import com.google.gson.annotations.SerializedName
import com.woowahan.domain.model.BanchanDetailModel

data class BanchanDetailEntity(
    @SerializedName("data")
    val data: Data,
    @SerializedName("hash")
    val hash: String
) {
    data class Data(
        @SerializedName("delivery_fee")
        val deliveryFee: String,
        @SerializedName("delivery_info")
        val deliveryInfo: String,
        @SerializedName("detail_section")
        val detailSection: List<String>,
        @SerializedName("point")
        val point: String,
        @SerializedName("prices")
        val prices: List<String>,
        @SerializedName("product_description")
        val productDescription: String,
        @SerializedName("thumb_images")
        val thumbImages: List<String>,
        @SerializedName("top_image")
        val topImage: String
    )

    fun toDomain(
        title: String,
        deliveryFee: Long,
        freeDeliveryFeePrice: Long
    ): BanchanDetailModel = BanchanDetailModel(
        hash = hash,
        title = title,
        imageUrl = data.thumbImages.first(),
        price = priceStrToLong(data.prices.first()),
        deliveryFee = deliveryFee,
        freeDeliveryFeePrice = freeDeliveryFeePrice,
        description = data.productDescription,
        point = priceStrToLong(data.point),
        salePrice = if (data.prices.size > 1) {
            priceStrToLong(data.prices.last())
        } else {
            0L
        },
        deliveryInfo = data.deliveryInfo,
        deliveryFeeInfo = data.deliveryFee,
        detailImages = data.detailSection,
        thumbImages = data.thumbImages
    )

    private fun priceStrToLong(priceStr: String): Long {
        val temp = priceStr.filter { it.isDigit() }
        return if (temp.isBlank()) 0L else temp.toLong()
    }
}