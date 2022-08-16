package com.woowahan.data.datasource

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
    private val cartDao: CartDao
) : CartDataSource {
    // key => banchan hash
    // value => BanchanModel[품목], Int[개수]
//    private val cart = mutableMapOf<String, Pair<BanchanModel, Int>>()
//    private val cartItemCountFlow: MutableStateFlow<Int> = MutableStateFlow(0)

    override fun getCartSizeFlow(): Flow<Int> = cartDao.fetchCartItemsCount()

    // 단순 추가 -> 추가된 항목 리턴
    override suspend fun insertCartItem(banchan: BanchanModel, count: Int) {
        cartDao.insertCartItem(
            BanchanItemTableEntity(banchan.hash, banchan.title),
            CartTableEntity(banchan.hash, count)
        )
    }

    // 단순 제거 -> 제거된 Item 리턴
    override suspend fun removeCartItem(hash: String): Int {
        val res = cartDao.removeCartItem(hash)
        cartDao.removeCartItemInfo(hash)
        return res
    }

    // 목록 제거 -> 제거된 Items 리턴
    override suspend fun removeCartItems(hashes: List<String>): Int {
        val res = cartDao.removeCartItem(*hashes.toTypedArray())
        cartDao.removeCartItemInfo(*hashes.toTypedArray())
        return res
    }

    // 항목 개수 업데이트 -> 이때 기존에 없는 항목을 업데이트 시도한다면 null 을 리턴받을것
    override suspend fun updateCartItemCount(hash: String, count: Int): Int {
        return cartDao.updateCartItemCount(hash, count)
    }

    override suspend fun updateCartItemSelect(hash: String, isSelect: Boolean): Int {
        return cartDao.updateCartItemSelect(hash, isSelect)
    }

    override suspend fun fetchCartItems(): List<CartEntity> {
        return cartDao.fetchCartItems().map { it.toEntity() }
    }

    override suspend fun fetchCartItemsFlow(): Flow<List<CartEntity>> {
        return cartDao.fetchCartItemsFlow().map { it.map { dto -> dto.toEntity() } }
    }
}