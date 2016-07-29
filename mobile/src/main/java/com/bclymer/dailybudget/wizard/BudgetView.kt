package com.bclymer.dailybudget.wizard

import android.content.Context
import android.util.AttributeSet
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import com.bclymer.dailybudget.R
import com.bclymer.dailybudget.core.database.BudgetRepository
import com.bclymer.dailybudget.core.database.CategoryRepository
import com.bclymer.dailybudget.extensions.observeOnMainThread
import com.bclymer.dailybudget.extensions.safeSubscribe
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

    private var budgetId: Long? = null
    private val values by lazy {
        context.resources.getStringArray(R.array.pay_period_values).map { it.toInt() }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (isInEditMode) return

        CategoryRepository.monitorAll()
                .observeOnMainThread()
                .safeSubscribe(onNext = { categories ->
                    edittext_category.setAdapter(ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, categories.map { it.name }))
                })

        edittext_category.setOnFocusChangeListener { view, focused ->
            if (focused) {
                edittext_category.showDropDown()
            } else {
                edittext_category.dismissDropDown()
            }
        }

        edittextName.textChanges()
                .filter { budgetId != null }
                .flatMap { BudgetRepository.updateBudget(budgetId!!, name = it.toString()) }
                .observeOnMainThread()
                .safeSubscribe()

        // TODO this is getting triggered from setBudget and I hate it. It's the spinner's listener.
        Observable
                .combineLatest(edittext_amount.textChanges(), spinner_period.itemSelections(), { amountStr, period ->
                    amountStr.toString().toDoubleOrZero() to values[period]
                })
                .filter { budgetId != null }
                .delay { BudgetRepository.updateBudget(budgetId!!, amount = it.first, intervalInDays = it.second) }
                .map { (it.first * it.second) / 365 }
                .map { NumberFormat.getCurrencyInstance().format(it) }
                .map { "Daily Budget: $it" }
                .observeOn(AndroidSchedulers.mainThread())
                .safeSubscribe(onNext = { textview_dailyallocation.text = it })
    }

    internal fun setBudget(budget: Budget) {
        this.budgetId = null
        edittextName.setText(budget.name)
        edittext_amount.setText(budget.allocation.toString())
        val index = values.indexOf(budget.intervalInDays.toInt())
        if (index != -1) {
            spinner_period.setSelection(index)
        }
        this.budgetId = budget.id
    }

}