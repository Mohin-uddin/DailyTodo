package com.example.dailytodo.utils

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.Window
import com.example.dailytodo.R
import kotlinx.android.synthetic.main.item_dialuge_beef.*

object ConstValue {

    fun animation(context: Context, state:Int): Dialog {
        val dialog = Dialog(context, R.style.CommonDialog2)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.item_dialuge_beef)
        val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
            dialog.ivLoader,
            PropertyValuesHolder.ofFloat("scaleX", 1.2f),
            PropertyValuesHolder.ofFloat("scaleY", 1.2f))
        scaleDown.duration = 310

        scaleDown.repeatCount = ObjectAnimator.INFINITE
        scaleDown.repeatMode = ObjectAnimator.REVERSE

        if (state==0) {
            dialog.show()
            scaleDown.start()
        }
        else
        {
            Log.e("asdasdasd", "animation: " )
            dialog.dismiss()
            scaleDown.cancel()
        }

        return dialog
    }
}