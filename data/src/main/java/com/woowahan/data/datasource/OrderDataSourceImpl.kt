package com.woowahan.data.datasource

import com.woowahan.data.dao.OrderDao
import com.woowahan.data.entity.dto.OrderEntity
import com.woowahan.data.entity.table.OrderItemTableEntity
import com.woowahan.data.entity.table.OrderTableEntity
import com.woowahan.domain.model.OrderItemModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
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
        items: List<OrderItemModel>
    ): Flow<Long> = flow {
        emit(
            orderDao.insertOrder(
                OrderTableEntity(time),
                items.map { OrderItemTableEntity(
                    0L,
                    it.hash,
                    it.imageUrl,
                    it.title,
                    it.count,
                    it.price
                ) }
            )
        )
    }

    override suspend fun updateOrder(orderId: Long, deliveryState: Boolean): Flow<Boolean> = flow {
        emit(orderDao.update(orderId, deliveryState) != 0)
    }

    override suspend fun fetchOrder(orderId: Long): Flow<OrderEntity> = flow {
        orderDao.fetchOrder(orderId)
            .collect {
                emit(it.toEntity())
            }
    }

    override fun fetchOrders(): Flow<List<OrderEntity>> = flow {
        orderDao.fetchOrders()
            .collect {
                emit(it.map { item -> item.toEntity() } )
            }
    }

    override fun getDeliveryOrderCount(): Flow<Int> = flow {
        orderDao.fetchDeliveryOrderCount()
            .collect {
                emit(it)
            }
    }
}