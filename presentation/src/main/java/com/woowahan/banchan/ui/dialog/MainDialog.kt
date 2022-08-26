package com.woowahan.banchan.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import com.woowahan.banchan.databinding.DialogMainBinding
import timber.log.Timber

class MainDialog: Dialog {
    constructor(context: Context): super(context) { init() }
    constructor(context: Context, themeResId: Int): super(context, themeResId) { init() }

    lateinit var binding : DialogMainBinding

    private fun init(){
        binding = DialogMainBinding.inflate(LayoutInflater.from(context), null, false)
        setContentView(binding.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        Timber.d("init dialog")
        setDefaultListener()
        setDefaultVisible()
    }

    private fun setDefaultVisible(){
        binding.btnColor.visibility = View.GONE
        binding.btnBlack.visibility = View.GONE
    }
    private fun setDefaultListener(){
        binding.btnColor.setOnClickListener { this.dismiss() }
        binding.btnBlack.setOnClickListener { this.dismiss() }
    }

    fun setPositiveBtn(btnText : String? = null, positiveListener : ((View) -> (Unit))? = null){
        binding.btnColor.visibility = View.VISIBLE
        if(!btnText.isNullOrBlank())
            binding.btnColor.text = btnText
        binding.btnColor.setOnClickListener {
            positiveListener?.invoke(it)
            this.dismiss()
        }
    }
    fun setNegativeBtn(btnText : String? = null, negativeListener : ((View) -> (Unit))? = null){
        binding.btnBlack.visibility = View.VISIBLE
        if(!btnText.isNullOrBlank())
            binding.btnBlack.text = btnText
        binding.btnBlack.setOnClickListener {
            negativeListener?.invoke(it)
            this.dismiss()
        }
    }

    fun setMessage(message: CharSequence?) {
        binding.tvTitle.text = message
    }
}