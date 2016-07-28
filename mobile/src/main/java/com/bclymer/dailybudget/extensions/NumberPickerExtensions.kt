package com.bclymer.dailybudget.extensions

import android.widget.NumberPicker
import rx.Observable

/**
 * Created by brianclymer on 7/26/16.
 * Copyright Travefy, Inc.
 */

internal fun NumberPicker.valueChanges(): Observable<Int> {
    return Observable.create { sub ->
        sub.onStart()
        this.setOnValueChangedListener { numberPicker, oldVal, newVal ->
            sub.onNext(newVal)
        }
    }
}