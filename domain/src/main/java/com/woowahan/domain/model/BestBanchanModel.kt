package com.woowahan.domain.model

data class BestBanchanModel(
    val title: String,
    val banchans: List<BanchanModel>,
    val viewType: ViewType = ViewType.Section
){
    companion object {
        fun empty(): BestBanchanModel = BestBanchanModel("", emptyList())
    }
    enum class ViewType(val value: Int){
        Banner(0),
        Section(1)
    }
}