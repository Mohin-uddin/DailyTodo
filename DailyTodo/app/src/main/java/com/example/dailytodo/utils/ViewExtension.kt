package com.example.dailytodo.utils

import android.view.View

fun View.show() {
    this.visibility = View.VISIBLE
}
fun View.gone() {
    this.visibility = View.GONE
}
fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.changeVisibilityStatus(){
    this.visibility = if(this.visibility == View.VISIBLE) View.GONE else View.VISIBLE
}