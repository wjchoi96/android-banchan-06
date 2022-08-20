package com.woowahan.data.datasource

import com.woowahan.data.dao.BanchanDao
import com.woowahan.data.dao.CartDao
import com.woowahan.data.entity.dto.CartEntity
import com.woowahan.data.entity.table.BanchanItemTableEntity
import com.woowahan.data.entity.table.CartTableEntity
import com.woowahan.domain.model.BanchanModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartDataSourceImpl @Inject constructor(
    private val banchanDao: BanchanDao,
    private val cartDao: CartDao
) : CartDataSource {
    override fun getCartSizeFlow(): Flow<Int> = flow {
        cartDao.fetchCartItemsCount()
            .collect {
                emit(it)
            }
    }

    // 단순 추가 -> 추가된 항목 리턴
    override suspend fun insertCartItem(hash: String, title: String, count: Int){
        cartDao.insertCartItem(
            BanchanItemTableEntity(hash, title),
            CartTableEntity(hash, count)
        )
    }

    override suspend fun removeCartItem(vararg hashes: String): Flow<Int> = flow {
        val res = cartDao.removeCartItem(*hashes)
        cartDao.removeCartItemInfo(*hashes)
        emit(res)
    }

    // 항목 개수 업데이트 -> 이때 기존에 없는 항목을 업데이트 시도한다면 null 을 리턴받을것
    override suspend fun updateCartItemCount(hash: String, count: Int): Flow<Int> = flow {
        emit(cartDao.updateCartItemCount(hash, count))
    }

    override suspend fun updateCartItemSelect(isSelect: Boolean, vararg hashes: String): Flow<Int> = flow {
        emit(cartDao.updateCartItemSelect(isSelect, *hashes))
    }

    override suspend fun fetchCartItems(): Flow<List<CartEntity>> = flow {
        cartDao.fetchCartItems()
            .collect {
                emit(it.map { dto -> dto.toEntity() })
            }
    }
}