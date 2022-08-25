package com.woowahan.domain.repository

import androidx.paging.PagingData
import com.woowahan.domain.model.OrderItemModel
import com.woowahan.domain.model.OrderModel
import kotlinx.coroutines.flow.Flow

interface OrderRepository {

    suspend fun insertOrder(
        time: String,
        items: List<OrderItemModel>
    ): Flow<Long>

    suspend fun updateOrder(orderId: Long, deliveryState: Boolean): Flow<Boolean>

    suspend fun fetchOrder(orderId: Long): Flow<OrderModel>

    suspend fun fetchOrders(): Flow<List<OrderModel>>

    fun fetchOrdersPaging(): Flow<PagingData<OrderModel>>

    suspend fun getDeliveryOrderCount(): Flow<Int>
}