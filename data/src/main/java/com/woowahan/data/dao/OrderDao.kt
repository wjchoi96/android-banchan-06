package com.woowahan.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.woowahan.data.entity.dto.OrderDto
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
            "where ${OrderTableEntity.COLUMN_ID} in (:orderId)")
    fun update(vararg orderId: Long, deliveryState: Boolean): Int

    @Transaction
    @Query("select * from `${OrderTableEntity.TABLE_NAME}` where ${OrderTableEntity.COLUMN_ID} = :orderId")
    fun fetchOrder(orderId: Long): Flow<OrderDto>

    @Transaction
    @Query("SELECT * FROM `${OrderTableEntity.TABLE_NAME}` ORDER BY datetime(${OrderTableEntity.COLUMN_TIME}) DESC")
    fun fetchOrdersPaging(): PagingSource<Int, OrderDto>

    // true => 1
    @Query("select COUNT(*) FROM `${OrderTableEntity.TABLE_NAME}` where ${OrderTableEntity.COLUMN_STATE} = 1")
    fun fetchDeliveryOrderCount(): Flow<Int>
}