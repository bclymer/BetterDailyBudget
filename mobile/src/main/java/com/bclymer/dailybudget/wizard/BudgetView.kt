package com.bclymer.dailybudget.wizard

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.bclymer.dailybudget.R
import com.bclymer.dailybudget.core.database.BudgetRepository
import com.bclymer.dailybudget.extensions.observeOnMainThread
import com.bclymer.dailybudget.extensions.toDoubleOrZero
import com.bclymer.dailybudget.models.Budget
import com.jakewharton.rxbinding.widget.itemSelections
import com.jakewharton.rxbinding.widget.textChanges
import kotlinx.android.synthetic.main.wizard_view_budget.view.*
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import java.text.NumberFormat

/**
 * Created by brianclymer on 7/24/16.
 * Copyright Travefy, Inc.
 */
internal class BudgetView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {

    internal fun setBudget(budget: Budget) {
        val values = context.resources.getStringArray(R.array.pay_period_values).map { it.toInt() }

        edittextName.setText(budget.name)
        edittext_amount.setText(budget.amount.toString())
        val index = values.indexOf(budget.intervalInDays)
        if (index != -1) {
            spinner_period.setSelection(index)
        }

        edittextName.textChanges()
                .flatMap { BudgetRepository.updateBudget(budgetId = budget.id, name = it.toString()) }
                .observeOnMainThread()
                .subscribe()

        Observable
                .combineLatest(edittext_amount.textChanges(), spinner_period.itemSelections(), { amountStr, period ->
                    val amount = amountStr.toString().toDoubleOrZero()
                    // TODO make the observable chain call subscribe on this.
                    BudgetRepository.updateBudget(
                            budgetId = budget.id,
                            amount = amount,
                            intervalInDays = values[period]
                    ).subscribe()

                    (amount * values[period]) / 365
                })
                .map { NumberFormat.getCurrencyInstance().format(it) }
                .map { "Daily Budget: $it" }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { textview_dailyallocation.text = it }
    }

}