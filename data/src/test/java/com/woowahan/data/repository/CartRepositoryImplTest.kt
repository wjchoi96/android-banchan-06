package com.woowahan.data.repository

import com.google.gson.Gson
import com.woowahan.data.datasource.SpyBanchanDetailDataSourceImpl
import com.woowahan.data.datasource.SpyCartDataSourceImpl
import com.woowahan.data.entity.BanchanDetailEntity
import com.woowahan.data.entity.dto.CartEntity
import com.woowahan.domain.model.BanchanModel
import com.woowahan.domain.repository.CartRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class CartRepositoryImplTest {

    private lateinit var spyCartDataSource: SpyCartDataSourceImpl
    private lateinit var spyBanchanDetailDataSource: SpyBanchanDetailDataSourceImpl
    private lateinit var cartRepo: CartRepository

    private val testDispatcher = StandardTestDispatcher(TestCoroutineScheduler())

    private lateinit var banchanDetailEntity: BanchanDetailEntity
    private val cartEntities = listOf(
        CartEntity("HBDEF", 1, true, "title")
    )

    private fun readResponse(fileName: String): String{
        return File("src/test/java/com/resources/$fileName").readText()
    }

    @Before
    fun setUpCartRepo(){
        banchanDetailEntity =  Gson().fromJson(readResponse("banchan_detail_success.json"), BanchanDetailEntity::class.java)
        spyCartDataSource = SpyCartDataSourceImpl(cartEntities.toMutableList())
        spyBanchanDetailDataSource = SpyBanchanDetailDataSourceImpl(banchanDetailEntity)
        cartRepo = CartRepositoryImpl(
            spyCartDataSource,
            spyBanchanDetailDataSource,
            testDispatcher //Dispatchers.Default
        )
    }

    @Test
    fun getCartSizeFlow_return_one() = runTest(testDispatcher) {
        //Given -> Before

        //When
        val actualResult = cartRepo.getCartSizeFlow().first()

        //Then
        val expect = cartEntities.size
        assertThat(actualResult)
            .isEqualTo(expect)
    }

    @Test
    fun insertCartItem_called_insertCartDaoMethod() = runTest(testDispatcher) {
        //Given
        val insertItem = BanchanModel.empty()

        //When
        cartRepo.insertCartItem(insertItem, 1).first()
        val actualResult = spyCartDataSource.insertCartItemCallCount

        //Then
        val expectCallTime = 1
        assertThat(actualResult)
            .isEqualTo(expectCallTime)
    }

    @Test
    fun removeCartItem_return_true() = runTest(testDispatcher) {
        //Given -> Before

        //When
        val actualResult = cartRepo.removeCartItem("test").first()

        //Then
        assertThat(actualResult)
            .isTrue()
    }

    @Test
    fun updateCartItemCount_return_true() = runTest(testDispatcher) {
        //Given -> Before

        //When
        val actualResult = cartRepo.updateCartItemCount("test", 1).first()

        //Then
        assertThat(actualResult)
            .isTrue()
    }

    @Test
    fun updateCartItemSelect_return_true() = runTest(testDispatcher) {
        //Given -> Before

        //When
        val actualResult = cartRepo.updateCartItemSelect(true, "test").first()

        //Then
        assertThat(actualResult)
            .isTrue()
    }

    @Test
    fun fetchCartItemsKey_contain_key() = runTest(testDispatcher) {
        //Given -> Before

        //When
        val actualResult = cartRepo.fetchCartItemsKey().first()

        //Then
        val expect = cartEntities.first().hash
        assertThat(actualResult)
            .contains(expect)
    }

    @Test
    fun fetchCartItems_request_dataSource() = runTest(testDispatcher) {
        //Given -> Before

        //When
        cartRepo.fetchCartItems().first()
        val actualResult = spyBanchanDetailDataSource.fetchMethodCallCount

        //Then
        val expect = 1
        assertThat(actualResult)
            .isEqualTo(expect)
    }

    @Test
    // 두번 호출했지만, 한번은 dataSource, 한번은 cache 에서 데이터를 가져오기때문에 fetchMethodCallCount 가 1이다
    // 를 어떻게 네이밍하지...
    fun fetchCartItems_request_cache() = runTest(testDispatcher) {
        //Given -> Before

        //When
        repeat(2){ cartRepo.fetchCartItems().first() }
        val actualResult = spyBanchanDetailDataSource.fetchMethodCallCount

        //Then
        val expect = 1
        assertThat(actualResult)
            .isEqualTo(expect)
    }

}
