package com.woowahan.banchan.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.woowahan.banchan.CartModelDiffUtilCallback
import com.woowahan.banchan.databinding.ItemCartContentBinding
import com.woowahan.banchan.databinding.ItemCartFooterBinding
import com.woowahan.banchan.databinding.ItemCartHeaderBinding
import com.woowahan.banchan.extension.priceStrToLong
import com.woowahan.banchan.extension.toCashString
import com.woowahan.domain.model.CartModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DefaultCartAdapter(
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var cartList = listOf<CartModel>()
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
        private val checkBoxListener: View.OnClickListener,
        private val deleteListener: View.OnClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(
                parent: ViewGroup,
                checkBoxListener: View.OnClickListener,
                deleteListener: View.OnClickListener
            ): CartHeaderViewHolder =
                CartHeaderViewHolder(
                    binding = ItemCartHeaderBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    checkBoxListener,
                    deleteListener
                )
        }

        fun bind() {
            binding.cbxSelectAll.setOnClickListener(checkBoxListener)
            binding.tvRemoveAll.setOnClickListener(deleteListener)
        }
    }

    class CartItemViewHolder(
        private val binding: ItemCartContentBinding,
        val checkBoxListener: View.OnClickListener,
        val deleteListener: View.OnClickListener,
        val minusListener: View.OnClickListener,
        val plusListener: View.OnClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(
                parent: ViewGroup,
                checkBoxListener: View.OnClickListener,
                deleteListener: View.OnClickListener,
                minusListener: View.OnClickListener,
                plusListener: View.OnClickListener
            ): CartItemViewHolder =
                CartItemViewHolder(
                    binding = ItemCartContentBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    checkBoxListener,
                    deleteListener,
                    minusListener,
                    plusListener
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
        val orderListener: View.OnClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        val totalPrice = (menusPrice.priceStrToLong() + deliveryFee.priceStrToLong()).toCashString()

        companion object {
            fun from(
                parent: ViewGroup,
                menusPrice: String,
                deliveryFee: String,
                orderListener: View.OnClickListener
            ): CartFooterViewHolder =
                CartFooterViewHolder(
                    binding = ItemCartFooterBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    menusPrice, deliveryFee, orderListener
                )
        }

        fun bind() {
            binding.holder = this
            binding.freeDelivery = (40000L - totalPrice.priceStrToLong()).toCashString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}