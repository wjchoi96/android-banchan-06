package com.woowahan.banchan.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.woowahan.banchan.databinding.ItemCartContentBinding
import com.woowahan.banchan.databinding.ItemCartEmptyBinding
import com.woowahan.banchan.databinding.ItemCartFooterBinding
import com.woowahan.banchan.databinding.ItemCartHeaderBinding
import com.woowahan.banchan.extension.toCashString
import com.woowahan.domain.model.CartListItemModel
import com.woowahan.domain.model.CartModel
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
    private val itemClickListener: (String, String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var cartList = listOf<CartListItemModel>()

    suspend fun updateList(newList: List<CartListItemModel>) {
         withContext(Dispatchers.Default){
            val diffCallback = CartListModelDiffUtilCallback(cartList, newList)
            val diffRes = DiffUtil.calculateDiff(diffCallback)
            withContext(Dispatchers.Main) {
                cartList = newList
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
        val onQuantityChange: (CartModel, Boolean) -> Unit = { item, needToChange ->
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
            binding.totalPrice = (item.price * item.count)
        }

        fun bindQuantityPayload(item: CartListItemModel.Content) {
            binding.cartItem = item.cart
            binding.totalPrice = (item.cart.price * item.cart.count)
        }

        fun bindSelectPayload(item: CartListItemModel.Content) {
            binding.isSelected = item.cart.isSelected
            binding.cartItem = item.cart
        }
    }

    class CartFooterViewHolder(
        private val binding: ItemCartFooterBinding,
        parent: ViewGroup,
        val moveToRecentViewedActivity: () -> Unit,
        val orderClicked: () -> Unit,
        private val itemClickListener: (String, String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        private val recentViewedAdapter: RecentViewedHorizontalAdapter by lazy {
            RecentViewedHorizontalAdapter(itemClickListener = itemClickListener)
        }

        companion object {
            fun from(
                parent: ViewGroup,
                moveToRecentViewedActivity: () -> Unit,
                orderClicked: () -> Unit,
                itemClickListener: (String, String) -> Unit
            ): CartFooterViewHolder =
                CartFooterViewHolder(
                    binding = ItemCartFooterBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    parent,
                    moveToRecentViewedActivity,
                    orderClicked,
                    itemClickListener
                )
        }

        private val parentLifecycleOwner = parent.findViewTreeLifecycleOwner()

        fun bind(item: CartListItemModel.Footer) {
            bindTotalPrice(item)
            binding.holder = this
            binding.recentViewedAdapter = recentViewedAdapter
            binding.isViewedItemEmpty = item.recentViewedItems.isEmpty()
            parentLifecycleOwner?.lifecycleScope?.launch {
                recentViewedAdapter.updateList(item.recentViewedItems)
            }
        }

        fun bindTotalPrice(item: CartListItemModel.Footer) {
            binding.showPrice = item.showPriceInfo
            binding.menuPrice = item.price
            binding.totalPrice = item.totalPrice
            binding.btnEnabled = (item.minimumOrderPrice <= item.price)
            val freeDelivery = (item.freeDeliveryFeePrice - item.price)
            binding.freeDelivery = freeDelivery.toCashString()
            binding.isFreeDelivery = (freeDelivery <= 0)
            if (freeDelivery <= 0) {
                binding.footerItem = item.copy(deliveryFee = 0L)
            } else {
                binding.footerItem = item
            }
            binding.menuPriceStr = item.price.toCashString()
        }

        fun bindRecentViewedItems(item: CartListItemModel.Footer) {
            parentLifecycleOwner?.lifecycleScope?.launch {
                recentViewedAdapter.updateList(item.recentViewedItems)
            }
            binding.rvRecentViewed.post {
                binding.rvRecentViewed.smoothScrollToPosition(0)
            }
        }

        fun bindShowPriceInfo(item: CartListItemModel.Footer) {
            binding.showPrice = item.showPriceInfo
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
                moveToRecentViewedActivity = recentViewedAllClicked,
                itemClickListener = itemClickListener
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
        payloads.firstOrNull()?.let { payloads ->
            Timber.d("onBindViewHolder payloads[$position][$payloads]")
            (payloads as ArrayList<*>).forEach { payload ->
                when (payload) {
                    Payload.SelectAllChanged -> {
                        if (holder is CartHeaderViewHolder) {
                            holder.bindSelectAllPayload(cartList[position] as CartListItemModel.Header)
                        }
                    }

                    Payload.QuantityChanged -> {
                        if (holder is CartItemViewHolder) {
                            holder.bindQuantityPayload(cartList[position] as CartListItemModel.Content)
                        }
                    }

                    Payload.SelectOneChanged -> {
                        if (holder is CartItemViewHolder) {
                            holder.bindSelectPayload(cartList[position] as CartListItemModel.Content)
                        }
                    }

                    Payload.TotalPriceChanged -> {
                        if (holder is CartFooterViewHolder) {
                            holder.bindTotalPrice(cartList[position] as CartListItemModel.Footer)
                        }
                    }

                    Payload.UpdateRecentViewed -> {
                        if (holder is CartFooterViewHolder) {
                            holder.bindRecentViewedItems(cartList[position] as CartListItemModel.Footer)
                        }
                    }

                    Payload.PriceInfoChanged -> {
                        if (holder is CartFooterViewHolder) {
                            holder.bindShowPriceInfo(cartList[position] as CartListItemModel.Footer)
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = cartList.size

    sealed class Payload {
        object SelectAllChanged : Payload()
        object SelectOneChanged : Payload()
        object QuantityChanged : Payload()
        object TotalPriceChanged : Payload()
        object UpdateRecentViewed : Payload()
        object PriceInfoChanged : Payload()
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
            val payloads = ArrayList<Any>()

            when {
                oldItem is CartListItemModel.Header && newItem is CartListItemModel.Header -> {
                    if (!(oldItem isSameContentWith newItem)) {
                        payloads.add(Payload.SelectAllChanged)
                    }
                }
                oldItem is CartListItemModel.Content && newItem is CartListItemModel.Content -> {
                    if (oldItem.cart.isSelected != newItem.cart.isSelected) {
                        payloads.add(Payload.SelectOneChanged)
                    }
                    if (oldItem.cart.count != newItem.cart.count) {
                        payloads.add(Payload.QuantityChanged)
                    }
                }
                oldItem is CartListItemModel.Footer && newItem is CartListItemModel.Footer -> {
                    if (oldItem.recentViewedItems != newItem.recentViewedItems) {
                        payloads.add(Payload.UpdateRecentViewed)
                    }
                    if (oldItem.showPriceInfo != newItem.showPriceInfo) {
                        payloads.add(Payload.PriceInfoChanged)
                    }
                    if (!(oldItem isSameContentWith newItem)) {
                        payloads.add(Payload.TotalPriceChanged)
                    }
                }
            }
            if (payloads.isNotEmpty()) {
                return payloads
            }

            return super.getChangePayload(oldItemPosition, newItemPosition)
        }
    }
}