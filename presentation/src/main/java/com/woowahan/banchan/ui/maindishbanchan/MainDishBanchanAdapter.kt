package com.woowahan.banchan.ui.maindishbanchan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.woowahan.banchan.BanchanModelDiffUtilCallback
import com.woowahan.banchan.databinding.ItemBachanListBannerBinding
import com.woowahan.banchan.databinding.ItemMenuVerticalBinding
import com.woowahan.banchan.databinding.ItemViewModeToggleHeaderBinding
import com.woowahan.domain.model.BanchanModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainDishBanchanAdapter(
    private val bannerTitle: String,
    private val filterTypeList: List<String>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val banchanList = mutableListOf<BanchanModel>()

    fun updateList(newList: List<BanchanModel>){
        CoroutineScope(Dispatchers.Default).launch {
            val diffCallback = BanchanModelDiffUtilCallback(banchanList, newList)
            val diffRes = DiffUtil.calculateDiff(diffCallback)
            withContext(Dispatchers.Main) {
                banchanList.clear()
                banchanList.addAll(newList)
                diffRes.dispatchUpdatesTo(this@MainDishBanchanAdapter)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            BanchanModel.ViewType.Banner.value -> BanchanListBannerViewHolder.from(parent)
            BanchanModel.ViewType.Header.value -> ViewModelToggleHeaderViewHolder.from(parent)
            else -> MainDishBanchanViewHolder.from(parent)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return banchanList[position].viewType.value
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is BanchanListBannerViewHolder -> holder.bind(bannerTitle)
            is ViewModelToggleHeaderViewHolder -> holder.bind(filterTypeList)
            is MainDishBanchanViewHolder -> holder.bind(banchanList[position])
        }
    }

    override fun getItemCount(): Int = banchanList.size

    class BanchanListBannerViewHolder(
        private val binding: ItemBachanListBannerBinding
    ): RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): BanchanListBannerViewHolder = BanchanListBannerViewHolder(
                ItemBachanListBannerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }
        fun bind(title: String){
            binding.bannerTitle = title
            binding.showBestLabel = false
        }
    }

    class ViewModelToggleHeaderViewHolder(
        private val binding: ItemViewModeToggleHeaderBinding
    ): RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): ViewModelToggleHeaderViewHolder = ViewModelToggleHeaderViewHolder(
                ItemViewModeToggleHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }

        fun bind(filterTypeList: List<String>){

        }
    }

    class MainDishBanchanViewHolder(
        private val binding: ItemMenuVerticalBinding
    ): RecyclerView.ViewHolder(binding.root){
        companion object {
            fun from(parent: ViewGroup): MainDishBanchanViewHolder = MainDishBanchanViewHolder(
                ItemMenuVerticalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }

        fun bind(item: BanchanModel){
            binding.banchan = item
        }
    }

}