package com.woowahan.banchan.ui.adapter.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.woowahan.banchan.databinding.ItemBachanListBannerBinding

class BanchanListBannerViewHolder(
    private val binding: ItemBachanListBannerBinding
) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun from(parent: ViewGroup): BanchanListBannerViewHolder = BanchanListBannerViewHolder(
            ItemBachanListBannerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    fun bind(title: String) {
        binding.bannerTitle = title
        binding.showBestLabel = false
    }
}