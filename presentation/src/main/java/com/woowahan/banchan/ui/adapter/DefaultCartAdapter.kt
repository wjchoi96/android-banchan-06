package com.woowahan.banchan.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.woowahan.banchan.CartListModelDiffUtilCallback
import com.woowahan.banchan.databinding.ItemCartContentBinding
import com.woowahan.banchan.databinding.ItemCartFooterBinding
import com.woowahan.banchan.databinding.ItemCartHeaderBinding
import com.woowahan.banchan.extension.toCashString
import com.woowahan.domain.model.CartListItemModel
import com.woowahan.domain.model.CartModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class DefaultCartAdapter(
    private val selectAll: (Boolean) -> Unit,
    private val deleteAllSelected: () -> Unit,
    private val selectItem: (CartModel, Boolean) -> Unit,
    private val deleteItem: (CartModel) -> Unit,
    private val minusClicked: (CartModel) -> Unit,
    private val plusClicked: (CartModel) -> Unit,
    private val orderClicked: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var cartList = listOf<CartListItemModel>()
    fun updateList(newList: List<CartListItemModel>) {
        CoroutineScope(Dispatchers.Default).launch {
            val diffCallback =
                CartListModelDiffUtilCallback(cartList, newList)
            val diffRes = DiffUtil.calculateDiff(diffCallback)
            withContext(Dispatchers.Main) {
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

        fun bind(defaultSelected: Boolean) {
            var isAllSelected = defaultSelected

            binding.holder = this
            binding.isAllSelected = isAllSelected
        }

        fun bindSelectAllPayload(header: CartListItemModel.Header) {
            binding.isAllSelected = header.isAllSelected
        }
    }

    class CartItemViewHolder(
        private val binding: ItemCartContentBinding,
        val selectItem: (CartModel, Boolean) -> Unit,
        val deleteItem: (CartModel) -> Unit,
        val minusClicked: (CartModel) -> Unit,
        val plusClicked: (CartModel) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(
                parent: ViewGroup,
                selectItem: (CartModel, Boolean) -> Unit,
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
            binding.itemCount = item.count
            binding.totalPrice = (item.price * item.count)
            binding.isSelected = item.isSelected
        }

        fun bindQuantityPayload(item: CartListItemModel.Content) {
            binding.itemCount = item.cart.count
            binding.totalPrice = (item.cart.price * item.cart.count)
        }

        fun bindSelectPayload(item: CartListItemModel.Content) {
            binding.isSelected = item.cart.isSelected
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

        fun bind(item: CartListItemModel.Footer) {
            binding.holder = this
            binding.footerItem = item
            binding.freeDelivery = (40000L - item.price).toCashString()
            binding.isFreeDelivery = (40000L <= item.price)
            binding.btnEnabled = (10000L <= item.price)
            binding.btnText = if (10000L <= item.price) {
                "${item.totalPrice.toCashString()}원 주문하기"
            } else {
                "최소주문금액을 확인해주세요"
            }
            binding.menuPrice = item.price
            binding.totalPrice = item.totalPrice
            binding.isViewedItemEmpty = false
        }

        fun bindTotalPrice(item: CartListItemModel.Footer) {
            binding.menuPrice = item.price
            binding.totalPrice = item.totalPrice
            binding.btnEnabled = (10000L <= item.price)
            binding.btnText = if (10000L <= item.price) {
                "${item.totalPrice.toCashString()}원 주문하기"
            } else {
                "최소주문금액을 확인해주세요"
            }
            binding.isViewedItemEmpty = false
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CartModel.ViewType.Header.value -> CartHeaderViewHolder.from(
                parent,
                selectAll = selectAll,
                deleteAllSelected = deleteAllSelected
            )
            CartModel.ViewType.Content.value -> CartItemViewHolder.from(
                parent,
                selectItem = selectItem,
                deleteItem = deleteItem,
                minusClicked,
                plusClicked
            )
            else -> CartFooterViewHolder.from(
                parent,
                orderClicked = orderClicked
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (cartList[position]) {
            is CartListItemModel.Header -> CartModel.ViewType.Header.value
            is CartListItemModel.Content -> CartModel.ViewType.Content.value
            is CartListItemModel.Footer -> CartModel.ViewType.Footer.value
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CartHeaderViewHolder -> holder.bind((cartList[position] as CartListItemModel.Header).isAllSelected)
            is CartItemViewHolder -> holder.bind((cartList[position] as CartListItemModel.Content).cart)
            is CartFooterViewHolder -> holder.bind(cartList[position] as CartListItemModel.Footer)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
            return
        }
        payloads.firstOrNull()?.let {
            Timber.d("onBindViewHolder payloads[$position][$it]")
            when (it) {
                Payload.SelectAllChanged -> {
                    if (holder is CartHeaderViewHolder) {
                        holder.bindSelectAllPayload(cartList[position] as CartListItemModel.Header)
                    }
                }

                Payload.quantityChanged -> {
                    if (holder is CartItemViewHolder) {
                        holder.bindQuantityPayload(cartList[position] as CartListItemModel.Content)
                    }
                }

                Payload.SelectOneChanged -> {
                    if (holder is CartItemViewHolder) {
                        holder.bindSelectPayload(cartList[position] as CartListItemModel.Content)
                    }
                }

                Payload.totalPriceChanged -> {
                    if (holder is CartFooterViewHolder) {
                        holder.bindTotalPrice(cartList[position] as CartListItemModel.Footer)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = cartList.size

    sealed class Payload {
        object SelectAllChanged : Payload()
        object SelectOneChanged : Payload()
        object quantityChanged : Payload()
        object totalPriceChanged : Payload()
    }
}