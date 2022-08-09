package com.woowahan.banchan.util

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

fun Fragment.showToast(context: Context?, message: String){
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun Fragment.showSnackBar(message: String, view: View){
    Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
}