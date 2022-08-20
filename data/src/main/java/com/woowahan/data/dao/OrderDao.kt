package com.woowahan.data.dao

import androidx.room.*
import com.woowahan.data.entity.dto.OrderDto
import com.woowahan.data.entity.dto.OrderEntity
import com.woowahan.data.entity.table.CartTableEntity
import com.woowahan.data.entity.table.OrderItemTableEntity
import com.woowahan.data.entity.table.OrderTableEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Insert
    fun insertOrder(order: OrderTableEntity): Long

    @Insert
    fun insertOrderItem(orderItem: OrderItemTableEntity): Long

    @Transaction
    fun insertOrder(order: OrderTableEntity, orderItems: List<OrderItemTableEntity>): Long {
        val id = insertOrder(order)
        orderItems.forEach {
            insertOrderItem(it.copy(orderId = id))
        }
        return id
    }

    @Query("update `${OrderTableEntity.TABLE_NAME}` set " +
            "${OrderTableEntity.COLUMN_STATE} = :deliveryState " +
            "where ${OrderTableEntity.COLUMN_ID} = :orderId")
    fun update(orderId: Long, deliveryState: Boolean): Int

    @Transaction
    @Query("select * from `${OrderTableEntity.TABLE_NAME}` where ${OrderTableEntity.COLUMN_ID} = :orderId")
    fun fetchOrder(orderId: Long): Flow<OrderDto>

    @Transaction
    @Query("SELECT * FROM `${OrderTableEntity.TABLE_NAME}`")
    fun fetchOrders(): Flow<List<OrderDto>>

    // true => 1
    @Query("select COUNT(*) FROM `${OrderTableEntity.TABLE_NAME}` where ${OrderTableEntity.COLUMN_STATE} = 1")
    fun fetchDeliveryOrderCount(): Flow<Int>
}