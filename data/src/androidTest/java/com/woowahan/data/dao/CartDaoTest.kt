package com.woowahan.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.woowahan.data.database.BanchanRoomDatabase
import com.woowahan.data.entity.table.BanchanItemTableEntity
import com.woowahan.data.entity.table.CartTableEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class CartDaoTest {
    private lateinit var cartDao: CartDao
    private lateinit var banchanDao: BanchanDao
    private lateinit var db: BanchanRoomDatabase

    private val cart = CartTableEntity(
        "test_hash",
        1,
        true
    )
    private val banchan = BanchanItemTableEntity(
        "test_hash",
        "test_title"
    )

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, BanchanRoomDatabase::class.java).build()

        cartDao = db.cartDao()
        banchanDao = db.banchanDao()

        banchanDao.insertBanchanItems(banchan)
        cartDao.insertCartItem(cart)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertCartItem_data_isNotEmpty() = runTest {
        //Given
        val cartItem = CartTableEntity(
            "test_hash_1",
            1,
            true
        )
        val banchanItem = BanchanItemTableEntity(
            "test_hash_1",
            "test_title"
        )

        //When
        banchanDao.insertBanchanItems(banchanItem)
        cartDao.insertCartItem(cartItem)
        val actualResult = cartDao.fetchCartItems().first().map { it.toEntity() }

        //Then
        assertThat(actualResult).filteredOn { it.hash == cartItem.hash }.isNotEmpty
    }

    @Test
    fun updateCartItemCount_data_equals() = runTest {
        //Given
        val hash = cart.hash
        val count = cart.count + 1

        //When
        cartDao.updateCartItemCount(hash, count)
        val actualResult = cartDao.fetchCartItems().first()
            .map { it.toEntity() }.find { it.hash == hash }

        //Then
        assertThat(actualResult?.count).isEqualTo(count)
    }

    @Test
    fun updateCartItemSelect_data_equals() = runTest {
        //Given
        val hash = cart.hash
        val select = !cart.isSelect

        //When
        cartDao.updateCartItemSelect(select, hash)

        //Then
        val actualResult = cartDao.fetchCartItems().first()
            .map { it.toEntity() }
            .find { it.hash == hash }
        assertThat(actualResult?.isSelect).isEqualTo(select)
    }

    @Test
    fun removeCartItem_data_isNull() = runTest {
        //Given
        val hash = cart.hash

        //When
        cartDao.removeCartItem(hash)

        //Then
        val actualResult = cartDao.fetchCartItems().first()
            .map { it.toEntity() }
        assertThat(actualResult)
            .filteredOn { it.hash == hash }
            .isEmpty()
    }

    @Test
    fun fetchItemCount_insertAfter_equal() = runTest {
        //Given
        val size = cartDao.fetchCartItemsCount().first()
        val cartItem = CartTableEntity(
            "test_hash_1",
            1,
            true
        )
        val banchanItem = BanchanItemTableEntity(
            "test_hash_1",
            "test_title"
        )

        //When
        banchanDao.insertBanchanItems(banchanItem)
        cartDao.insertCartItem(cartItem)
        val actualResult = cartDao.fetchCartItemsCount().first()

        //Then
        val expected = size + 1
        assertThat(actualResult).isEqualTo(expected)
    }

    @Test
    fun fetchItemCount_removeAfter_equal() = runTest {
        //Given
        val size = cartDao.fetchCartItemsCount().first()
        val hash = cart.hash

        //When
        cartDao.removeCartItem(hash)
        val actualResult = cartDao.fetchCartItemsCount().first()

        //Then
        val expected = size - 1
        assertThat(actualResult).isEqualTo(expected)
    }

    @Test
    fun fetchItem_insertAfter_contain() = runTest {
        //Given
        val cartItem = CartTableEntity(
            "test_hash_1",
            1,
            true
        )
        val banchanItem = BanchanItemTableEntity(
            "test_hash_1",
            "test_title"
        )

        //When
        banchanDao.insertBanchanItems(banchanItem)
        cartDao.insertCartItem(cartItem)
        val actualResult = cartDao.fetchCartItems().first()
            .map { it.toEntity() }

        //Then
        assertThat(actualResult)
            .filteredOn { it.hash == cartItem.hash }
            .isNotEmpty
    }


}