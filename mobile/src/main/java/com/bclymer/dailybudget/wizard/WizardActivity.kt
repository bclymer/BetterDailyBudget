package com.bclymer.dailybudget.wizard

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.bclymer.dailybudget.R
import com.bclymer.dailybudget.core.database.BudgetRepository
import com.bclymer.dailybudget.core.database.UserRepository
import com.bclymer.dailybudget.extensions.inflate
import com.bclymer.dailybudget.extensions.observeOnMainThread
import com.bclymer.dailybudget.extensions.toDoubleOrZero
import com.bclymer.dailybudget.extensions.valueChanges
import com.bclymer.dailybudget.models.Budget
import com.bclymer.dailybudget.models.User
import com.jakewharton.rxbinding.view.clicks
import com.jakewharton.rxbinding.widget.textChanges
import com.redbooth.WelcomeCoordinatorLayout
import kotlinx.android.synthetic.main.wizard_screen_three.*
import kotlinx.android.synthetic.main.wizard_screen_two.*
import kotlinx.android.synthetic.main.wizard_view_budget.view.*
import rx.Observable
import java.text.NumberFormat

class WizardActivity : AppCompatActivity() {

    private var budgets: MutableList<Budget> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wizard)

        val coordinator = findViewById(R.id.activity_wizard) as WelcomeCoordinatorLayout
        coordinator.addPage(
                R.layout.wizard_screen_one,
                R.layout.wizard_screen_two,
                R.layout.wizard_screen_three
        )

        numberpickerIncomeInterval.minValue = 1
        numberpickerIncomeInterval.maxValue = 365
        numberpickerIncomeInterval.wrapSelectorWheel = false

        setupSectionTwo()
        setupScreenThree()
    }

    private fun setupSectionTwo() {
        UserRepository.getUserOrCreate()
                .observeOnMainThread()
                .subscribe {
                    setupListeners()
                    setDefaults(it)
                }
    }

    private fun setDefaults(user: User) {
        edittextIncome.setText(user.income.toString())
        numberpickerIncomeInterval.value = user.payInterval
    }

    private fun setupListeners() {
        Observable
                .combineLatest(edittextIncome.textChanges(), numberpickerIncomeInterval.valueChanges(), { incomeStr, days ->
                    val income = incomeStr.toString().toDoubleOrZero()
                    UserRepository.updateIncome(income, days).subscribe()
                    income / days
                })
                .map { NumberFormat.getCurrencyInstance().format(it) }
                .map { "Daily Income: $it" }
                .observeOnMainThread()
                .subscribe { textviewDailyIncome.text = it }
    }

    private fun setupScreenThree() {
        recyclerviewBudgets.layoutManager = LinearLayoutManager(this)
        recyclerviewBudgets.adapter = BudgetsAdapter()

        BudgetRepository.getAll()
                .observeOnMainThread()
                .subscribe {
                    budgets = it.toMutableList()
                    recyclerviewBudgets.adapter.notifyDataSetChanged()
                }

        floatingButtonCreateBudget.clicks()
                .flatMap { BudgetRepository.create() }
                .doOnNext { budgets.add(it) }
                .observeOnMainThread()
                .subscribe {
                    recyclerviewBudgets.adapter.notifyDataSetChanged()
                }
    }

    private inner class BudgetsAdapter : RecyclerView.Adapter<BudgetViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) = BudgetViewHolder(parent?.inflate(R.layout.wizard_view_budget) as ViewGroup)

        override fun getItemCount() = budgets.size

        override fun onBindViewHolder(holder: BudgetViewHolder?, position: Int) {
            holder?.bind(budgets[position], position)
        }
    }

    private inner class BudgetViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent) {
        fun bind(budget: Budget, position: Int) {
            itemView.budgetView.setBudget(budget)
            itemView.buttonDelete.setOnClickListener {
                val budget = budgets[position]
                BudgetRepository.delete(budget.id).subscribe {
                    budgets.removeAt(position)
                    recyclerviewBudgets.adapter.notifyDataSetChanged()
                }
            }
        }
    }
}
