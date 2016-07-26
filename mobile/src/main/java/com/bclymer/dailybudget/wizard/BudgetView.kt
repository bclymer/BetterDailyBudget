package com.bclymer.dailybudget.wizard

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.bclymer.dailybudget.R
import com.bclymer.dailybudget.extensions.toDoubleOrZero
import com.jakewharton.rxbinding.widget.itemSelections
import com.jakewharton.rxbinding.widget.textChanges
import kotlinx.android.synthetic.main.wizard_view_budget.view.*
import rx.Observable
import rx.android.schedulers.AndroidSchedulers

/**
 * Created by brianclymer on 7/24/16.
 * Copyright Travefy, Inc.
 */
internal class BudgetView : LinearLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr)

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attributeSet, defStyleAttr, defStyleRes)

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (isInEditMode) return

        val values = context.resources.getStringArray(R.array.pay_period_values).map { it.toInt() }

        Observable
                .combineLatest(edittext_amount.textChanges(), spinner_period.itemSelections(), { amountStr, period ->
                    val amount = amountStr.toString().toDoubleOrZero()
                    (amount * values[period]) / 12
                })
                .map {
                    textview_dailyallocation.text = it.toString()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

}