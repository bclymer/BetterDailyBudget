package com.bclymer.dailybudget.extensions

import android.widget.EditText

/**
 * Created by brianclymer on 7/29/16.
 * Copyright Travefy, Inc.
 */

internal val EditText.textValue: String
    get() = text.toString()