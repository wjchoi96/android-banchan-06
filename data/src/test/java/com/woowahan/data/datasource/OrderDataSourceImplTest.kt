package com.woowahan.data.datasource

import com.woowahan.data.dao.OrderDao
import com.woowahan.data.entity.table.OrderItemTableEntity
import com.woowahan.data.entity.table.OrderTableEntity
import com.woowahan.domain.model.OrderItemModel
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OrderDataSourceImplTest {

    private lateinit var orderDao: OrderDao
    private lateinit var orderDataSourceImpl: OrderDataSource

    @Before
    fun setUpOrderDataSource() {
        orderDao = mockk(relaxed = true)
        orderDataSourceImpl = OrderDataSourceImpl(orderDao)
    }

    @Test
    fun insertOrder_data_callDaoInsert() = runTest {
        //Given
        val time = "time"
        val list = listOf<OrderItemModel>()

        //When
        orderDataSourceImpl.insertOrder(time, list).first()

        //Then
        verify {
            orderDao.insertOrder(
                OrderTableEntity(time),
                list.map { OrderItemTableEntity(
                    0L,
                    it.hash,
                    it.imageUrl,
                    it.title,
                    it.count,
                    it.price
                ) }
            )
        }
    }

}