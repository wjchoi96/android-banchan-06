package com.woowahan.data.datasource

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.woowahan.data.dao.OrderDao
import com.woowahan.data.entity.dto.OrderEntity
import com.woowahan.data.entity.table.OrderItemTableEntity
import com.woowahan.data.entity.table.OrderTableEntity
import com.woowahan.domain.model.OrderItemModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

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

    override suspend fun updateOrder(vararg orderId: Long, deliveryState: Boolean): Flow<Boolean> = flow {
        emit(orderDao.update(orderId = orderId, deliveryState) != 0)
    }

    override suspend fun fetchOrder(orderId: Long): Flow<OrderEntity> = flow {
        orderDao.fetchOrder(orderId)
            .collect {
                emit(it.toEntity())
            }
    }

    override fun fetchOrdersPaging(): Flow<PagingData<OrderEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = true
            ),
            pagingSourceFactory = {
                orderDao.fetchOrdersPaging()
            }
        ).flow.map { pagingData ->
                pagingData.map {
                    it.toEntity()
                }
            }
    }

    override fun getDeliveryOrderCount(): Flow<Int> = flow {
        orderDao.fetchDeliveryOrderCount()
            .collect {
                emit(it)
            }
    }
}