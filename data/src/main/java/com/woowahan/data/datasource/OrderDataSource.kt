package com.woowahan.data.datasource

import com.woowahan.data.entity.dto.OrderEntity
import kotlinx.coroutines.flow.Flow

interface OrderDataSource {

    suspend fun insertOrder(
        time: String,
        hash: String,
        imageUrl: String,
        title: String,
        count: Int,
        price: Long,
    ): Flow<Long>

    suspend fun updateOrder(orderId: Long, deliveryState: Boolean): Flow<Boolean>

    suspend fun fetchOrder(orderId: Long): Flow<OrderEntity>

    fun fetchOrders(): Flow<List<OrderEntity>>

    fun getDeliveryOrderCount(): Flow<Int>

}