package com.woowahan.data.datasource

import com.woowahan.data.dao.OrderDao
import com.woowahan.data.entity.dto.OrderEntity
import com.woowahan.data.entity.table.OrderItemTableEntity
import com.woowahan.data.entity.table.OrderTableEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderDataSourceImpl @Inject constructor(
    private val orderDao: OrderDao
): OrderDataSource {

    override suspend fun insertOrder(
        time: String,
        hash: String,
        imageUrl: String,
        title: String,
        count: Int,
        price: Long
    ): Flow<Long> = flow {
        emit(
            orderDao.insertOrder(
                OrderTableEntity(time), OrderItemTableEntity(0L, hash, imageUrl, title, count, price)
            )
        )
    }

    override suspend fun updateOrder(orderId: Long, deliveryState: Boolean): Flow<Boolean> = flow {
        emit(orderDao.update(orderId, deliveryState) != 0)
    }

    override suspend fun fetchOrder(orderId: Long): Flow<OrderEntity> {
        return orderDao.fetchOrder(orderId).map { it.toEntity() }
    }

    override fun fetchOrders(): Flow<List<OrderEntity>> {
        return orderDao.fetchOrders().map { it.map { item -> item.toEntity() } }
    }

    override fun getDeliveryOrderCount(): Flow<Int> {
        return orderDao.fetchDeliveryOrderCount()
    }
}