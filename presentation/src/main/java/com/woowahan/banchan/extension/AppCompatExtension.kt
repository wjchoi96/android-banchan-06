package com.woowahan.banchan.extension

import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

fun AppCompatActivity.showToast(message: String){
    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
}

fun AppCompatActivity.showSnackBar(message: String, view: View){
    Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
}

fun AppCompatActivity.showTopSnackBar(message: String, view: View){
    Snackbar.make(view, message, Snackbar.LENGTH_SHORT).let {
        val baseView = it.view
        baseView.layoutParams = (baseView.layoutParams as FrameLayout.LayoutParams).apply {
            gravity = Gravity.TOP
        }
        it.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
        it.show()
    }
}