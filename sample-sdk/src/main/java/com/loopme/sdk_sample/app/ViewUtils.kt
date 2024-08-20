package com.loopme.sdk_sample.app

import android.view.View
import android.widget.Button

fun View.makeVisible() {
    visibility = View.VISIBLE
}
fun View.makeGone() {
    visibility = View.GONE
}

fun View.makeInvisible() {
    visibility = View.INVISIBLE
}

fun Button.enable() {
    isEnabled = true
}

fun Button.disable() {
    isEnabled = false
}