package com.woowahan.data.repository

import com.woowahan.data.datasource.OrderDataSource
import com.woowahan.domain.model.OrderItemModel
import com.woowahan.domain.model.OrderModel
import com.woowahan.domain.repository.OrderRepository
import com.woowahan.domain.util.BanchanDateConvertUtil
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val orderDataSource: OrderDataSource,
    private val coroutineDispatcher: CoroutineDispatcher
): OrderRepository {

    override suspend fun insertOrder(
        time: String,
        items: List<OrderItemModel>
    ): Flow<Long> = flow {
        orderDataSource.insertOrder(
            time, items
        ).collect {
            emit(it)
        }
    }.flowOn(coroutineDispatcher)

    override suspend fun updateOrder(orderId: Long, deliveryState: Boolean): Flow<Boolean> = flow {
        orderDataSource.updateOrder(orderId, deliveryState)
            .collect {
                emit(it)
            }
    }.flowOn(coroutineDispatcher)

    override suspend fun fetchOrder(orderId: Long): Flow<OrderModel> = flow {
        orderDataSource.fetchOrder(orderId)
            .collect {
                emit(
                    OrderModel(
                        orderId = it.orderId,
                        time = BanchanDateConvertUtil.convert(it.time),
                        items = it.items.map { item -> item.toDomain() },
                        it.deliveryState
                    )
                )
            }
    }.flowOn(coroutineDispatcher)

    override suspend fun fetchOrders(): Flow<List<OrderModel>> = flow {
        orderDataSource.fetchOrders()
            .collect {
                emit(
                    it.map { item ->
                        OrderModel(
                            orderId = item.orderId,
                            time = BanchanDateConvertUtil.convert(item.time),
                            items = item.items.map { child -> child.toDomain() },
                            item.deliveryState
                        )
                    }
                )
            }
    }.flowOn(coroutineDispatcher)

    override suspend fun getDeliveryOrderCount(): Flow<Int> = flow {
        orderDataSource.getDeliveryOrderCount()
            .collect {
                emit(it)
            }
    }.flowOn(coroutineDispatcher)
}