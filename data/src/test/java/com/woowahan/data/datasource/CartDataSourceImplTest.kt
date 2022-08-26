package com.woowahan.data.datasource

import com.woowahan.data.dao.SpyBanchanDaoImpl
import com.woowahan.data.dao.SpyCartDaoImpl
import com.woowahan.data.entity.dto.CartDto
import com.woowahan.data.entity.table.BanchanItemTableEntity
import com.woowahan.data.entity.table.CartTableEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class CartDataSourceImplTest {

    private lateinit var spyCartDao: SpyCartDaoImpl
    private lateinit var spyBanchanDao: SpyBanchanDaoImpl
    private lateinit var cartDataSourceImpl: CartDataSource
    private val removeConstraintThrowableHash: String = "throw_hash"
    private val removeConstraintThrowableCode: Int = 1811
    private val removeConstraintThrowable = android.database.sqlite.SQLiteConstraintException(removeConstraintThrowableCode.toString())

    private val cartEntities = listOf(
        CartTableEntity("hash_1", 1)
    )
    private val banchanEntities = listOf(
        BanchanItemTableEntity("hash_1", "title_1")
    )

    @Before
    fun setUpCartDataSourceImpl(){
        spyCartDao = SpyCartDaoImpl(cartEntities.toMutableList(), banchanEntities.toMutableList())
        spyBanchanDao = SpyBanchanDaoImpl(
            banchanEntities.toMutableList(),
            removeConstraintThrowableHash,
            removeConstraintThrowable
        )
        cartDataSourceImpl = CartDataSourceImpl(
            spyBanchanDao, spyCartDao
        )
    }

    @Test
    fun getCartSizeFlow_correct_size() = runTest{
        //Given -> Before

        //Then
        val actualResult = cartDataSourceImpl.getCartSizeFlow().first()

        //When
        val expected = cartEntities.size
        assertThat(actualResult).isEqualTo(expected)
    }

    @Test
    fun insertCartItem_data_success() = runTest {
        //Given -> Before

        //When
        cartDataSourceImpl.insertCartItem("test_title", "test_title", 1)
        val actualCartDaoInsertCallCount = spyCartDao.insertCalledCount
        val actualBanchanDaoInsertCallCount = spyBanchanDao.insertCalledCount

        //Then
        val expectedInsertCallCount = 1
        assertThat(actualCartDaoInsertCallCount)
            .isEqualTo(actualBanchanDaoInsertCallCount)
            .isEqualTo(expectedInsertCallCount)
    }

    @Test
    fun removeCartItem_data_success() = runTest {
        //Given -> Before

        //When
        val actualResult = cartDataSourceImpl.removeCartItem(cartEntities.first().hash).first()

        //Then
        val expect = 1
        assertThat(actualResult)
            .isEqualTo(expect)
    }

    @Test
    fun removeCartItem_throw_constraintThrowable() {
        //Given -> Before

        //When
        val actualResult = Assertions.catchThrowable {
            runTest {
                cartDataSourceImpl.removeCartItem(removeConstraintThrowableHash).first()
            }
        }

        //Then
        val expect = removeConstraintThrowable
        assertThat(actualResult)
            .isInstanceOf(Throwable::class.java)
            .isEqualTo(expect)
    }

    @Test
    fun updateCartItemCount_data_success() = runTest{
        //Given -> Before

        //When
        val actualResult = cartDataSourceImpl.updateCartItemCount(
            cartEntities.first().hash,
            cartEntities.first().count + 1
        ).first()

        //Then
        val expect = spyCartDao.updateCountCalledCount
        assertThat(actualResult)
            .isEqualTo(expect)
    }

    @Test
    fun updateCartItemSelect_data_success() = runTest{
        //Given -> Before

        //When
        val actualResult = cartDataSourceImpl.updateCartItemSelect(
            true,
            cartEntities.first().hash
        ).first()

        //Then
        val expect = spyCartDao.updateSelectCalledCount
        assertThat(actualResult)
            .isEqualTo(expect)
    }

    @Test
    fun fetchCartItems_data_equals() = runTest {
        //Given -> Before

        //When
        val actualResult = cartDataSourceImpl.fetchCartItems().first()

        //Then
        val expect = listOf(
            CartDto(
                cartEntities.first(),
                banchanEntities.first()
            ).toEntity()
        )
        assertThat(actualResult)
            .isEqualTo(expect)
    }

}