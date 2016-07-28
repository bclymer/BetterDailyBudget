package com.bclymer.dailybudget.core

import android.os.Looper

/**
 * Created by brianclymer on 7/26/16.
 * Copyright Travefy, Inc.
 */


fun failOnMainThread(msg: String) {
    if (Looper.getMainLooper() == Looper.myLooper()) {
        throw IllegalStateException(msg)
    }
}

fun failOnBackgroundThread(msg: String) {
    if (Looper.getMainLooper() != Looper.myLooper()) {
        throw IllegalStateException(msg)
    }
}