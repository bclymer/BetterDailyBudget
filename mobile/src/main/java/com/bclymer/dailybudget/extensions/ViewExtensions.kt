package com.bclymer.dailybudget.extensions

import android.support.design.widget.Snackbar
import android.view.View

/**
 * Created by brianclymer on 7/29/16.
 * Copyright Travefy, Inc.
 */

fun View.snack(message: String, length: Int = Snackbar.LENGTH_SHORT, f: (Snackbar.() -> Unit)? = null) {
    val snack = Snackbar.make(this, message, length)
    if (f != null) {
        snack.f()
    }
    snack.show()
}

fun View.toVisible() {
    this.visibility = View.VISIBLE
}

fun View.toVisibleOrGone(condition: () -> Boolean) {
    if (condition()) toVisible() else toGone()
}

fun View.toVisibleOrInvisible(condition: () -> Boolean) {
    if (condition()) toVisible() else toInvisible()
}

fun View.toGone() {
    this.visibility = View.GONE
}

fun View.toInvisible() {
    this.visibility = View.INVISIBLE
}