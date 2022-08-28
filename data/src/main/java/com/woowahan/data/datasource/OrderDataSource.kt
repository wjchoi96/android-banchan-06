package com.woowahan.data.datasource

import androidx.paging.PagingData
import com.woowahan.data.entity.dto.OrderEntity
import com.woowahan.domain.model.OrderItemModel
import kotlinx.coroutines.flow.Flow

interface OrderDataSource {

    suspend fun insertOrder(
        time: String,
        items: List<OrderItemModel>
    ): Flow<Long>

    suspend fun updateOrder(vararg orderId: Long, deliveryState: Boolean): Flow<Boolean>

    suspend fun fetchOrder(orderId: Long): Flow<OrderEntity>

    fun fetchOrdersPaging(): Flow<PagingData<OrderEntity>>

    suspend fun fetchDeliveryOrder(): Flow<List<OrderEntity>>

    fun getDeliveryOrderCount(): Flow<Int>

}