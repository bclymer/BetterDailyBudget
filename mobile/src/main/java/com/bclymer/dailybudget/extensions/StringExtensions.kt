package com.bclymer.dailybudget.extensions

/**
 * Created by brianclymer on 7/25/16.
 * Copyright Travefy, Inc.
 */

internal fun String.toDoubleOrZero(): Double {
    try {
        return this.toDouble()
    } catch (e: NumberFormatException) {
        return 0.0
    }
}