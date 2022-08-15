package com.woowahan.banchan.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.woowahan.banchan.CartModelDiffUtilCallback
import com.woowahan.banchan.databinding.ItemCartContentBinding
import com.woowahan.banchan.databinding.ItemCartFooterBinding
import com.woowahan.banchan.databinding.ItemCartHeaderBinding
import com.woowahan.banchan.extension.toCashString
import com.woowahan.domain.extension.priceStrToLong
import com.woowahan.domain.model.CartModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DefaultCartAdapter(
    private val selectAll: (Boolean) -> Unit,
    private val deleteAllSelected: (List<CartModel>) -> Unit,
    private val selectItem: (CartModel, Boolean) -> Unit,
    private val deleteItem: (CartModel) -> Unit,
    private val minusClicked: (CartModel) -> Unit,
    private val plusClicked: (CartModel) -> Unit,
    private val orderClicked: (List<CartModel>) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var cartList = listOf<CartModel>()
    private val selectedMenu = ArrayList<CartModel>()
    private val cartStateChangePayload: String = "changePayload"

    fun updateList(newList: List<CartModel>) {
        CoroutineScope(Dispatchers.Default).launch {
            val diffCallback =
                CartModelDiffUtilCallback(cartList, newList, cartStateChangePayload)
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
        val deleteAllSelected: (List<CartModel>) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(
                parent: ViewGroup,
                selectAll: (Boolean) -> Unit,
                deleteAllSelected: (List<CartModel>) -> Unit
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

        fun bind(selectedMenu: List<CartModel>) {
            binding.cbxSelectAll.setOnClickListener {
                selectAll((it as CheckBox).isChecked)
            }
            binding.tvRemoveAll.setOnClickListener { deleteAllSelected(selectedMenu) }
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
        }
    }

    class CartFooterViewHolder(
        private val binding: ItemCartFooterBinding,
        val menusPrice: String,
        val deliveryFee: String,
        val orderClicked: (List<CartModel>) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        val totalPrice = (menusPrice.priceStrToLong() + deliveryFee.priceStrToLong()).toCashString()
        val lessThanMinPrice = (totalPrice.priceStrToLong() < 100000)

        companion object {
            fun from(
                parent: ViewGroup,
                menusPrice: String,
                deliveryFee: String,
                orderClicked: (List<CartModel>) -> Unit
            ): CartFooterViewHolder =
                CartFooterViewHolder(
                    binding = ItemCartFooterBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    menusPrice, deliveryFee, orderClicked
                )
        }

        fun bind() {
            binding.holder = this
            binding.freeDelivery = (40000L - totalPrice.priceStrToLong()).toCashString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CartModel.ViewType.Header.value -> CartHeaderViewHolder.from(
                parent,
                selectAll,
                deleteAllSelected
            )
            CartModel.ViewType.Items.value -> CartItemViewHolder.from(
                parent,
                selectItem,
                deleteItem,
                minusClicked,
                plusClicked
            )
            else -> CartFooterViewHolder.from(
                parent,
                menusPrice = cartList.sumOf { it.price }.toCashString(),
                "2,500",
                orderClicked
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return cartList[position].viewType.value
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CartHeaderViewHolder -> holder.bind(selectedMenu)
            is CartItemViewHolder -> holder.bind(cartList[position])
            is CartFooterViewHolder -> holder.bind()
        }
    }

    override fun getItemCount(): Int = cartList.size
}