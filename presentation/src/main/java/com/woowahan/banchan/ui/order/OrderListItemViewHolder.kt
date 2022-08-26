package com.woowahan.banchan.ui.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.woowahan.banchan.databinding.ItemOrderListBinding
import com.woowahan.domain.model.OrderModel

class OrderListItemViewHolder(
    private val binding: ItemOrderListBinding,
    val itemClickListener: (OrderModel) -> Unit
): RecyclerView.ViewHolder(binding.root){
    companion object {
        fun from(
            parent: ViewGroup,
            itemClickListener: (OrderModel) -> Unit
        ): OrderListItemViewHolder = OrderListItemViewHolder(
            ItemOrderListBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            itemClickListener
        )
    }

    fun bind(item: OrderModel){
        item.items.firstOrNull()?.let {
            binding.imageUrl = it.imageUrl
            binding.title = it.title
        }
        binding.price = item.totalPrice + item.deliveryFee
        bindDeliveryState(item.deliveryState)
        binding.itemSize = item.items.size
        binding.order = item
        binding.holder = this
    }

    fun bindDeliveryState(deliveryState: Boolean){
        binding.deliveryItemState = deliveryState
    }
}