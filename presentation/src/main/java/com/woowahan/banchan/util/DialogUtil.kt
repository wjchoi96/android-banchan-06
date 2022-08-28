package com.woowahan.banchan.util

import androidx.fragment.app.FragmentManager
import com.woowahan.banchan.ui.dialog.MainDialogFragment

object DialogUtil {
    class DialogCustomBuilder(
        val content: String,
        val positive: Pair<String, ()->Unit>,
        val negative: Pair<String, ()->Unit>? = null
    )

    fun show(
        fragmentManager: FragmentManager,
        customBuilder: DialogCustomBuilder
    ){
        when (customBuilder.negative) {
            null -> {
                show(
                    fragmentManager,
                    customBuilder.content,
                    customBuilder.positive.first,
                    customBuilder.positive.second
                )
            }
            else -> {
                show(
                    fragmentManager,
                    customBuilder.content,
                    customBuilder.positive.first,
                    customBuilder.positive.second,
                    customBuilder.negative.first,
                    customBuilder.negative.second
                )
            }
        }
    }

    private fun show(
        fragmentManager: FragmentManager,
        content: String,
        positive: String,
        positiveListener: ()->Unit,
        negative: String,
        negativeListener: ()->Unit
    ){
        MainDialogFragment.get(
            content,
            positive to positiveListener,
            negative to negativeListener
        ).show(fragmentManager)
    }

    fun show(
        fragmentManager: FragmentManager,
        content: String,
        positive: String,
        positiveListener: ()->Unit
    ){
        MainDialogFragment.get(
            content,
            positive to positiveListener
        ).show(fragmentManager)
    }

}