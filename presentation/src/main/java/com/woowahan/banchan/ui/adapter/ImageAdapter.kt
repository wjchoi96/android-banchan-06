package com.woowahan.banchan.ui.adapter

import android.media.Image
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.woowahan.banchan.databinding.ItemMenuDetailImageBinding
import com.woowahan.banchan.databinding.ItemMenuThumbImageBinding

class ImageAdapter(
    private val imageList: List<String>,
    private val imageType: ImageType
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    sealed class ImageType {
        object THUMB : ImageType()
        object DETAIL : ImageType()
    }

    class DetailImageViewHolder(private val binding: ItemMenuDetailImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(
                parent: ViewGroup,
            ): DetailImageViewHolder =
                DetailImageViewHolder(
                    ItemMenuDetailImageBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
        }

        fun bind(url: String) {
            binding.url = url
        }
    }

    class ViewPagerImageViewHolder(private val binding: ItemMenuThumbImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(
                parent: ViewGroup,
            ): ViewPagerImageViewHolder =
                ViewPagerImageViewHolder(
                    ItemMenuThumbImageBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
        }

        fun bind(url: String) {
            binding.url = url
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (imageType) {
            ImageType.THUMB -> ViewPagerImageViewHolder.from(parent)
            ImageType.DETAIL -> DetailImageViewHolder.from(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DetailImageViewHolder -> holder.bind(imageList[position])
            is ViewPagerImageViewHolder -> holder.bind(imageList[position])
        }
    }

    override fun getItemCount(): Int = imageList.size
}