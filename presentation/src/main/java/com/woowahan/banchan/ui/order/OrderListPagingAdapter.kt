package com.woowahan.banchan.ui.order

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.woowahan.domain.model.OrderModel
import timber.log.Timber

class OrderListPagingAdapter(
    private val itemClickListener: (OrderModel) -> Unit,
    private val deliveryStatePayload: String = "delivery_state_payload"
): PagingDataAdapter<OrderModel,OrderListItemViewHolder>(
    OrderListDiffUtilCallback(deliveryStatePayload)
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderListItemViewHolder {
        return OrderListItemViewHolder.from(parent, itemClickListener)
    }

    override fun onBindViewHolder(holder: OrderListItemViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
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
                    getItem(position)?.let { order ->
                        holder.bindDeliveryState(order.deliveryState)
                    }
                }
                else -> super.onBindViewHolder(holder, position, payloads)
            }
        }
    }

    class OrderListDiffUtilCallback(
        private val deliveryStatePayload: Any
    ): DiffUtil.ItemCallback<OrderModel>() {
        override fun areItemsTheSame(oldItem: OrderModel, newItem: OrderModel): Boolean {
            return oldItem.orderId == newItem.orderId
        }

        override fun areContentsTheSame(oldItem: OrderModel, newItem: OrderModel): Boolean {
            return oldItem ==newItem
        }

        override fun getChangePayload(oldItem: OrderModel, newItem: OrderModel): Any? {
            return when {
                oldItem.deliveryState != newItem.deliveryState -> deliveryStatePayload
                else -> super.getChangePayload(oldItem, newItem)
            }
        }

    }
}