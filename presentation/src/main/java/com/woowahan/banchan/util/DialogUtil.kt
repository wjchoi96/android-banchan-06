package com.woowahan.banchan.util

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.woowahan.banchan.ui.dialog.MainDialog

object DialogUtil {
    class DialogCustomBuilder(
        val content: String,
        val positive: Pair<String, ()->Unit>,
        val negative: Pair<String, ()->Unit>? = null
    )

    fun show(
        context: Context,
        customBuilder: DialogCustomBuilder
    ){
        when (customBuilder.negative) {
            null -> {
                show(
                    context,
                    customBuilder.content,
                    customBuilder.positive.first,
                    customBuilder.positive.second
                )
            }
            else -> {
                show(
                    context,
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
        context: Context,
        content: String,
        positive: String,
        positiveListener: ()->Unit,
        negative: String,
        negativeListener: ()->Unit
    ){
        MainDialog(context).apply {
            setMessage(content)
            setNegativeBtn(negative){
                negativeListener()
            }
            setPositiveBtn (positive){
                positiveListener()
            }
        }.show()
    }

    fun show(
        context: Context,
        content: String,
        positive: String,
        positiveListener: ()->Unit
    ){
        MainDialog(context).apply {
            setMessage(content)
            setPositiveBtn (positive){
                positiveListener()
            }
        }.show()
    }

}