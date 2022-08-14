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

    lateinit var vd : DialogMainBinding

    private fun init(){
        vd = DialogMainBinding.inflate(LayoutInflater.from(context), null, false)
        setContentView(vd.root)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        Timber.d("init dialog")
        setDefaultListener()
        setDefaultVisible()
    }

    private fun setDefaultVisible(){
        vd.btnColor.visibility = View.GONE
        vd.btnBlack.visibility = View.GONE
    }
    private fun setDefaultListener(){
        vd.btnColor.setOnClickListener { this.dismiss() }
        vd.btnBlack.setOnClickListener { this.dismiss() }
    }

    fun setPositiveBtn(btnText : String? = null, positiveListener : ((View) -> (Unit))? = null){
        vd.btnColor.visibility = View.VISIBLE
        if(!btnText.isNullOrBlank())
            vd.btnColor.text = btnText
        vd.btnColor.setOnClickListener {
            positiveListener?.invoke(it)
            this.dismiss()
        }
    }
    fun setNegativeBtn(btnText : String? = null, negativeListener : ((View) -> (Unit))? = null){
        vd.btnBlack.visibility = View.VISIBLE
        if(!btnText.isNullOrBlank())
            vd.btnBlack.text = btnText
        vd.btnBlack.setOnClickListener {
            negativeListener?.invoke(it)
            this.dismiss()
        }
    }

    fun setMessage(message: CharSequence?) {
        vd.tvTitle.text = message
    }
}