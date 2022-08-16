package com.woowahan.data.dao

import androidx.room.*
import com.woowahan.data.entity.dto.CartDto
import com.woowahan.data.entity.table.BanchanItemTableEntity
import com.woowahan.data.entity.table.CartTableEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCartItem(cartItem: BanchanItemTableEntity, cart: CartTableEntity)

    @Query("update ${CartTableEntity.TABLE_NAME} set " +
            "${CartTableEntity.COLUMN_COUNT} = :count " +
            "where ${CartTableEntity.COLUMN_HASH} = :hash")
    fun updateCartItemCount(hash: String, count: Int): Int

    @Query("DELETE FROM ${CartTableEntity.TABLE_NAME} WHERE ${CartTableEntity.COLUMN_HASH} in (:hash)")
    fun removeCartItem(vararg hash: String): Int
    @Query("DELETE FROM ${BanchanItemTableEntity.TABLE_NAME} WHERE ${BanchanItemTableEntity.COLUMN_HASH} in (:hash)")
    fun removeCartItemInfo(vararg hash: String): Int

    @Query("SELECT COUNT(*) FROM ${CartTableEntity.TABLE_NAME}")
    fun fetchCartItemsCount(): Flow<Int>

    @Transaction
    @Query("SELECT * FROM ${CartTableEntity.TABLE_NAME}")
    fun fetchCartItems(): List<CartDto>

    @Transaction
    @Query("SELECT * FROM ${CartTableEntity.TABLE_NAME}")
    fun fetchCartItemsFlow(): Flow<List<CartDto>>

}