package com.woowahan.banchan.ui.detail

import android.content.Context
import android.content.Intent
import androidx.activity.viewModels
import com.woowahan.banchan.R
import com.woowahan.banchan.databinding.ActivityCartBinding
import com.woowahan.banchan.ui.base.BaseActivity
import com.woowahan.banchan.ui.viewmodel.DetailViewModel

class MenuDetailActivity : BaseActivity<ActivityCartBinding>() {
    private val viewModel: DetailViewModel by viewModels()

    companion object {
        private const val HASH = "hash"
        private const val TITLE = "title"

        fun get(context: Context, hash: String, title: String): Intent {
            return Intent(context, MenuDetailActivity::class.java).apply {
                this.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                putExtra(HASH, hash)
                putExtra(TITLE, title)
            }
        }
    }

    override val layoutResId: Int
        get() = R.layout.activity_menu_detail
}