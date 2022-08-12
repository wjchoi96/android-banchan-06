package com.woowahan.banchan.ui.adapter.decoratin

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.woowahan.banchan.util.dp
import timber.log.Timber

class GridItemDecoration(
    context: Context,
    spanCount: Int
) {
    val decoration = object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)

            val idx = parent.getChildAdapterPosition(view) - 2
            if (idx < 0) return
            val column = idx % spanCount
            val margin = 16.dp(context)
            val spacing = 8.dp(context)
            outRect.left =
                spacing - column * spacing / spanCount // spacing - column * ((1f / spanCount) * spacing)
            outRect.right =
                (column + 1) * spacing / spanCount // (column + 1) * ((1f / spanCount) * spacing)

            when (column) {
                0 -> {
                    outRect.left = margin
                }
                spanCount - 1 -> {
                    outRect.right = margin
                }
            }

            outRect.bottom = 32.dp(context)
            Timber.d("idx[$idx] => left[${outRect.left}], right[${outRect.right}]")
        }
    }
}