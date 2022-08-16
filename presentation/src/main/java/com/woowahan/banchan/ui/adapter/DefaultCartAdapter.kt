package com.woowahan.banchan.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ObservableField
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.woowahan.banchan.CartListModelDiffUtilCallback
import com.woowahan.banchan.databinding.ItemCartContentBinding
import com.woowahan.banchan.databinding.ItemCartFooterBinding
import com.woowahan.banchan.databinding.ItemCartHeaderBinding
import com.woowahan.banchan.extension.toCashString
import com.woowahan.domain.extension.priceStrToLong
import com.woowahan.domain.model.CartListModel
import com.woowahan.domain.model.CartModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DefaultCartAdapter(
    private val selectAll: (Boolean) -> Unit,
    private val deleteAllSelected: () -> Unit,
    private val selectItem: (CartModel) -> Unit,
    private val deleteItem: (CartModel) -> Unit,
    private val minusClicked: (CartModel) -> Unit,
    private val plusClicked: (CartModel) -> Unit,
    private val orderClicked: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var cartList = listOf<CartListModel>()
    private val selectedItemSet = mutableSetOf<String>()
    private val cartStateChangePayload: String = "changePayload"

    fun updateList(newList: List<CartListModel>) {
        CoroutineScope(Dispatchers.Default).launch {
            val diffCallback =
                CartListModelDiffUtilCallback(cartList, newList, cartStateChangePayload)
            val diffRes = DiffUtil.calculateDiff(diffCallback)
            withContext(Dispatchers.Main) {
                // 기존에 선택한 아이템 불러오기
                newList.map {
                    if (it is CartListModel.Content) {
                        CartListModel.Content(it.cart.copy(isSelected = selectedItemSet.contains(it.cart.hash)))
                    } else {
                        it
                    }
                }
                cartList = newList.toList()
                diffRes.dispatchUpdatesTo(this@DefaultCartAdapter)
            }
        }
    }

    class CartHeaderViewHolder(
        private val binding: ItemCartHeaderBinding,
        val selectAll: (Boolean) -> Unit,
        val deleteAllSelected: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        var isSelectedAll = ObservableField(false)

        companion object {
            fun from(
                parent: ViewGroup,
                selectAll: (Boolean) -> Unit,
                deleteAllSelected: () -> Unit
            ): CartHeaderViewHolder =
                CartHeaderViewHolder(
                    binding = ItemCartHeaderBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    selectAll = selectAll,
                    deleteAllSelected = deleteAllSelected
                )
        }

        fun bind() {
            binding.holder = this
        }
    }

    class CartItemViewHolder(
        private val binding: ItemCartContentBinding,
        val selectItem: (CartModel) -> Unit,
        val deleteItem: (CartModel) -> Unit,
        val minusClicked: (CartModel) -> Unit,
        val plusClicked: (CartModel) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(
                parent: ViewGroup,
                selectItem: (CartModel) -> Unit,
                deleteItem: (CartModel) -> Unit,
                minusClicked: (CartModel) -> Unit,
                plusClicked: (CartModel) -> Unit,
            ): CartItemViewHolder =
                CartItemViewHolder(
                    binding = ItemCartContentBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    selectItem, deleteItem, minusClicked, plusClicked
                )
        }

        fun bind(item: CartModel) {
            binding.cartItem = item
            binding.holder = this
        }
    }

    class CartFooterViewHolder(
        private val binding: ItemCartFooterBinding,
        val orderClicked: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(
                parent: ViewGroup,
                orderClicked: () -> Unit
            ): CartFooterViewHolder =
                CartFooterViewHolder(
                    binding = ItemCartFooterBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    orderClicked
                )
        }

        fun bind(item: CartListModel.Footer) {
            binding.footerItem = item
            binding.freeDelivery = (40000L - item.price).toCashString()
            binding.isFreeDelivery = (40000L <= item.price)
            binding.btnEnabled = (10000L <= item.price)
            binding.btnText = if(10000L <= item.price){
                "${item.totalPrice.toCashString()}원 주문하기"
            }else{
                "최소주문금액을 확인해주세요"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CartModel.ViewType.Header.value -> CartHeaderViewHolder.from(
                parent,
                selectAll = { isSelected ->
                    // Room 연동
                    selectAll(isSelected)

                    // 로컬 연동
                    val test = cartList.map {
                        if (it is CartListModel.Content) {
                            CartListModel.Content(it.cart.copy(isSelected = isSelected))
                        } else {
                            it
                        }
                    }
                    updateList(test)
                    if (isSelected) {
                        selectedItemSet.addAll(
                            cartList.filterIsInstance<CartListModel.Content>().map { it.cart.hash })
                    } else {
                        selectedItemSet.clear()
                    }
                },
                deleteAllSelected = {
                    deleteAllSelected()
                    selectedItemSet.clear()
                }
            )
            CartModel.ViewType.Content.value -> CartItemViewHolder.from(
                parent,
                selectItem = { cartModel ->
                    selectItem(cartModel)
                    if (cartModel.isSelected) {
                        selectedItemSet.add(cartModel.hash)
                    } else {
                        selectedItemSet.remove(cartModel.hash)
                    }
                },
                deleteItem = { cartModel ->
                    deleteItem(cartModel)
                    selectedItemSet.remove(cartModel.hash)
                },
                minusClicked,
                plusClicked
            )
            else -> CartFooterViewHolder.from(
                parent,
                orderClicked = {
                    orderClicked()
                    selectedItemSet.clear()
                }
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (cartList[position]) {
            is CartListModel.Header -> CartModel.ViewType.Header.value
            is CartListModel.Content -> CartModel.ViewType.Content.value
            is CartListModel.Footer -> CartModel.ViewType.Footer.value
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CartHeaderViewHolder -> holder.bind()
            is CartItemViewHolder -> holder.bind((cartList[position] as CartListModel.Content).cart)
            is CartFooterViewHolder -> holder.bind(cartList[position] as CartListModel.Footer)
        }
    }

    override fun getItemCount(): Int = cartList.size
}