package com.woowahan.banchan.ui.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.woowahan.banchan.databinding.ItemOrderListBinding
import com.woowahan.domain.model.OrderModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class OrderListAdapter(
    private val itemClickListener: (OrderModel) -> Unit
): RecyclerView.Adapter<OrderListAdapter.OrderListItemViewHolder>() {

    private val deliveryStatePayload = "delivery_state_payload"

    private var orders: List<OrderModel> = emptyList()
    fun updateList(newList: List<OrderModel>) {
        CoroutineScope(Dispatchers.Default).launch {
            val diffCallback = OrderListDiffUtilCallback(orders, newList, deliveryStatePayload)
            val diffRes = DiffUtil.calculateDiff(diffCallback)
            withContext(Dispatchers.Main){
                orders = newList.toList()
                diffRes.dispatchUpdatesTo(this@OrderListAdapter)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderListItemViewHolder {
        return OrderListItemViewHolder.from(parent, itemClickListener)
    }

    override fun onBindViewHolder(holder: OrderListItemViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun onBindViewHolder(
        holder: OrderListItemViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if(payloads.isEmpty()){
            super.onBindViewHolder(holder, position, payloads)
            return
        }
        payloads.firstOrNull()?.let {
            Timber.d("onBindViewHolder payloads[$position][$it]")
            when (it) {
                deliveryStatePayload -> {
                    holder.bindDeliveryState(orders[position].deliveryState)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return orders.size
    }

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
            binding.price = item.items.sumOf { it.price }
            bindDeliveryState(item.deliveryState)
            binding.itemSize = item.items.size
            binding.order = item
            binding.holder = this
        }

        fun bindDeliveryState(deliveryState: Boolean){
            binding.deliveryItemState = deliveryState
        }
    }

    class OrderListDiffUtilCallback(
        private val oldList: List<OrderModel>,
        private val newList: List<OrderModel>,
        private val deliveryStatePayload: Any
    ): DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].orderId == newList[newItemPosition].orderId
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            return when {
                oldList[oldItemPosition].deliveryState != newList[newItemPosition].deliveryState -> deliveryStatePayload
                else -> super.getChangePayload(oldItemPosition, newItemPosition)
            }
        }
    }
}