package com.woowahan.banchan.ui.adapter

import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.woowahan.banchan.databinding.ItemCartContentBinding
import com.woowahan.banchan.databinding.ItemCartEmptyBinding
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
    private val updateItem: (CartModel, Int) -> Unit,
    private val orderClicked: () -> Unit,
    private val recentViewedAllClicked: () -> Unit,
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
        val updateItem: (CartModel, Int) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        private val onQuantityChange: (CartModel, Boolean) -> Unit = { item, needToChange ->
            if (needToChange) {
                if (binding.edtQuantity.text.isEmpty()) {
                    binding.edtQuantity.setText("1")
                }
                updateItem(item, binding.edtQuantity.text.toString().toInt())
            }
        }

        companion object {
            fun from(
                parent: ViewGroup,
                selectItem: (CartModel, Boolean) -> Unit,
                deleteItem: (CartModel) -> Unit,
                updateItem: (CartModel, Int) -> Unit,
            ): CartItemViewHolder =
                CartItemViewHolder(
                    binding = ItemCartContentBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    selectItem, deleteItem, updateItem
                )
        }

        fun bind(item: CartModel) {
            binding.cartItem = item
            binding.holder = this
            binding.isSelected = item.isSelected
            binding.itemCount = item.count
            binding.totalPrice = (item.price * item.count)
            binding.isSelected = item.isSelected

            binding.edtQuantity.setOnFocusChangeListener { v, hasFocus ->
                onQuantityChange(item, !hasFocus)
            }

            binding.edtQuantity.setOnKeyListener { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER){
                    onQuantityChange(item, true)
                    true
                }
                false
            }
        }

        fun bindQuantityPayload(item: CartListItemModel.Content) {
            binding.cartItem = item.cart
            binding.itemCount = item.cart.count
            binding.totalPrice = (item.cart.price * item.cart.count)

            binding.edtQuantity.setOnFocusChangeListener { v, hasFocus ->
                onQuantityChange(item.cart, hasFocus)
            }
        }

        fun bindSelectPayload(item: CartListItemModel.Content) {
            binding.isSelected = item.cart.isSelected
            binding.cartItem = item.cart

            binding.edtQuantity.setOnFocusChangeListener { v, hasFocus ->
                onQuantityChange(item.cart, hasFocus)
            }
        }
    }

    class CartFooterViewHolder(
        private val binding: ItemCartFooterBinding,
        val moveToRecentViewedActivity: () -> Unit,
        val orderClicked: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        private val recentViewedAdapter: RecentViewedHorizontalAdapter by lazy {
            RecentViewedHorizontalAdapter(onItemCLick = {})
        }

        companion object {
            fun from(
                parent: ViewGroup,
                moveToRecentViewedActivity: () -> Unit,
                orderClicked: () -> Unit
            ): CartFooterViewHolder =
                CartFooterViewHolder(
                    binding = ItemCartFooterBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    moveToRecentViewedActivity,
                    orderClicked
                )
        }

        fun bind(item: CartListItemModel.Footer) {
            binding.holder = this
            val freeDelivery = (40000L - item.price)
            binding.freeDelivery = freeDelivery.toCashString()
            binding.isFreeDelivery = (freeDelivery <= 0)
            if (freeDelivery <= 0) {
                binding.footerItem = item.copy(deliveryFee = 0L)
            } else {
                binding.footerItem = item
            }
            binding.btnEnabled = (10000L <= item.price)
            binding.btnText = if (10000L <= item.price) {
                "${item.totalPrice.toCashString()}원 주문하기"
            } else {
                "최소주문금액을 확인해주세요"
            }
            binding.menuPrice = item.price
            binding.totalPrice = item.totalPrice
            binding.recentViewedAdapter = recentViewedAdapter
            binding.isViewedItemEmpty = item.recentViewedItems.isEmpty()
            recentViewedAdapter.updateList(item.recentViewedItems)
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

            val freeDelivery = (40000L - item.price)
            binding.freeDelivery = freeDelivery.toCashString()
            binding.isFreeDelivery = (freeDelivery <= 0)
            if (freeDelivery <= 0) {
                binding.footerItem = item.copy(deliveryFee = 0L)
            } else {
                binding.footerItem = item
            }
        }

        fun bindRecentViewedItems(item: CartListItemModel.Footer) {
            recentViewedAdapter.updateList(item.recentViewedItems)
        }
    }

    class CartItemEmptyViewHolder(
        private val binding: ItemCartEmptyBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): CartItemEmptyViewHolder =
                CartItemEmptyViewHolder(
                    ItemCartEmptyBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
        }

        fun bind() {

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
                updateItem = updateItem
            )
            CartModel.ViewType.Empty.value -> CartItemEmptyViewHolder.from(parent)
            else -> CartFooterViewHolder.from(
                parent,
                orderClicked = orderClicked,
                moveToRecentViewedActivity = recentViewedAllClicked
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (cartList[position]) {
            is CartListItemModel.Header -> CartModel.ViewType.Header.value
            is CartListItemModel.Content -> {
                if ((cartList[position] as CartListItemModel.Content).cart.isEmpty()) {
                    CartModel.ViewType.Empty.value
                } else {
                    CartModel.ViewType.Content.value
                }
            }
            is CartListItemModel.Footer -> CartModel.ViewType.Footer.value
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CartHeaderViewHolder -> holder.bind((cartList[position] as CartListItemModel.Header).isAllSelected)
            is CartItemViewHolder -> holder.bind((cartList[position] as CartListItemModel.Content).cart)
            is CartItemEmptyViewHolder -> holder.bind()
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

                Payload.updateRecentViewed -> {
                    if (holder is CartFooterViewHolder) {
                        holder.bindRecentViewedItems(cartList[position] as CartListItemModel.Footer)
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
        object updateRecentViewed : Payload()
    }

    class CartListModelDiffUtilCallback(
        private val oldList: List<CartListItemModel>,
        private val newList: List<CartListItemModel>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] isSameIdWith newList[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] isSameContentWith newList[newItemPosition]
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            when {
                oldItem is CartListItemModel.Header && newItem is CartListItemModel.Header -> {
                    if (!(oldItem isSameContentWith newItem)) {
                        return Payload.SelectAllChanged
                    }
                }
                oldItem is CartListItemModel.Content && newItem is CartListItemModel.Content -> {
                    if (oldItem.cart.isSelected != newItem.cart.isSelected) {
                        return Payload.SelectOneChanged
                    }
                    if (oldItem.cart.count != newItem.cart.count) {
                        return Payload.quantityChanged
                    }
                }
                oldItem is CartListItemModel.Footer && newItem is CartListItemModel.Footer -> {
                    if (oldItem.recentViewedItems != newItem.recentViewedItems) {
                        return Payload.updateRecentViewed
                    } else if (!(oldItem isSameContentWith newItem)) {
                        return Payload.totalPriceChanged
                    }
                }
            }

            return super.getChangePayload(oldItemPosition, newItemPosition)
        }
    }
}