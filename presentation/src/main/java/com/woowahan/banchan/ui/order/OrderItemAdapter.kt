package com.woowahan.banchan.ui.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.woowahan.banchan.databinding.ItemOrderHorizontalBinding
import com.woowahan.banchan.databinding.ItemOrderStateFooterBinding
import com.woowahan.banchan.databinding.ItemOrderStateHeaderBinding
import com.woowahan.domain.constant.DeliveryConstant
import com.woowahan.domain.model.OrderItemModel
import com.woowahan.domain.model.OrderItemTypeModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*

class OrderItemAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val headerTimePayload = "header_time_payload"
    private val headerStatePayload = "header_state_payload"

    private var orderItems: List<OrderItemTypeModel> = emptyList()
    fun updateList(newList: List<OrderItemTypeModel>){
        CoroutineScope(Dispatchers.Default).launch {
            val diffCallback = OrderItemDiffUtilCallback(orderItems, newList, headerTimePayload, headerStatePayload)
            val diffRes = DiffUtil.calculateDiff(diffCallback)
            withContext(Dispatchers.Main){
                orderItems = newList.toList()
                diffRes.dispatchUpdatesTo(this@OrderItemAdapter)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            OrderItemTypeModel.ViewType.Header.value -> OrderStateHeaderViewHolder.from(parent)
            OrderItemTypeModel.ViewType.Footer.value -> OrderStateFooterViewHolder.from(parent)
            else -> {
                OrderItemViewHolder.from(parent)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is OrderStateHeaderViewHolder -> holder.bind(orderItems[position] as OrderItemTypeModel.Header)
            is OrderItemViewHolder -> holder.bind((orderItems[position] as OrderItemTypeModel.Order).orderItem)
            is OrderStateFooterViewHolder -> holder.bind(orderItems[position] as OrderItemTypeModel.Footer)
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
                headerTimePayload -> {
                    when (holder) {
                        is OrderStateHeaderViewHolder -> {
                            (orderItems[position] as OrderItemTypeModel.Header).let { item ->
                                holder.bindDeliveryRemainingTime(item.deliveryStartDate, item.deliveryTimeMinute)
                            }
                        }
                    }
                }
                headerStatePayload -> {
                    when (holder) {
                        is OrderStateHeaderViewHolder -> {
                            holder.bindDeliveryState((orderItems[position] as OrderItemTypeModel.Header).deliveryState)
                        }
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(orderItems[position]){
            is OrderItemTypeModel.Header -> OrderItemTypeModel.ViewType.Header.value
            is OrderItemTypeModel.Order -> OrderItemTypeModel.ViewType.Order.value
            is OrderItemTypeModel.Footer -> OrderItemTypeModel.ViewType.Footer.value
            else -> super.getItemViewType(position)
        }
    }

    override fun getItemCount(): Int {
        return orderItems.size
    }

    class OrderItemViewHolder(
        private val binding: ItemOrderHorizontalBinding
    ): RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): OrderItemViewHolder = OrderItemViewHolder(
                ItemOrderHorizontalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }

        fun bind(item: OrderItemModel){
            binding.imageUrl = item.imageUrl
            binding.count = item.count
            binding.title = item.title
            binding.price = item.price
        }
    }

    class OrderStateHeaderViewHolder(
        private val binding: ItemOrderStateHeaderBinding
    ): RecyclerView.ViewHolder(binding.root){
        companion object {
            fun from(parent: ViewGroup): OrderStateHeaderViewHolder = OrderStateHeaderViewHolder(
                ItemOrderStateHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }

        fun bind(item: OrderItemTypeModel.Header){
            bindDeliveryRemainingTime(item.deliveryStartDate, item.deliveryTimeMinute)
            bindDeliveryState(item.deliveryState)
            binding.deliveryCount = item.deliveryCount
        }

        fun bindDeliveryRemainingTime(deliveryStartTime: Date?, deliveryMinute: Int){
            if(deliveryStartTime == null) return
            val current = Calendar.getInstance().time.time
            val start = deliveryStartTime.time
            val lastTimeSinceStartMinute = (((current-start)/1000)/60).toInt() // 배송 시작하고 지난 분
            Timber.d("lastTimeSinceStartMinute => $lastTimeSinceStartMinute => ${(current-start)/1000}")
            val remainingMinute = if(lastTimeSinceStartMinute >= deliveryMinute)
                0
            else
                DeliveryConstant.DeliveryMinute - lastTimeSinceStartMinute
            binding.deliveryTime = remainingMinute
        }

        fun bindDeliveryState(deliveryState: Boolean){
            binding.deliveryItem = deliveryState
        }
    }

    class OrderStateFooterViewHolder(
        private val binding: ItemOrderStateFooterBinding
    ): RecyclerView.ViewHolder(binding.root){
        companion object {
            fun from(parent: ViewGroup): OrderStateFooterViewHolder = OrderStateFooterViewHolder(
                ItemOrderStateFooterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }

        fun bind(item: OrderItemTypeModel.Footer){
            binding.menuPrice = item.price
            binding.totalPrice = item.totalPrice
            binding.deliveryFee = item.deliveryFee
        }
    }

    class OrderItemDiffUtilCallback(
        private val oldList: List<OrderItemTypeModel>,
        private val newList: List<OrderItemTypeModel>,
        private val headerTimePayload: String,
        private val headerStatePayload: String
    ): DiffUtil.Callback(){

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] isSameId newList[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] isSameContent newList[newItemPosition]
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            when {
                oldList[oldItemPosition] is OrderItemTypeModel.Header && newList[newItemPosition] is OrderItemTypeModel.Header -> {
                    return if((oldList[oldItemPosition] as OrderItemTypeModel.Header).deliveryState != (newList[newItemPosition] as OrderItemTypeModel.Header).deliveryState){
                        headerStatePayload
                    }else{
                        headerTimePayload
                    }
                }
            }
            return super.getChangePayload(oldItemPosition, newItemPosition)
        }
    }
}