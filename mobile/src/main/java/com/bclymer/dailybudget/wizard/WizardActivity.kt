package com.bclymer.dailybudget.wizard

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.bclymer.dailybudget.R
import com.bclymer.dailybudget.core.database.StaticExpenseRepository
import com.bclymer.dailybudget.core.database.UserRepository
import com.bclymer.dailybudget.extensions.*
import com.bclymer.dailybudget.models.StaticExpense
import com.bclymer.dailybudget.models.User
import com.jakewharton.rxbinding.view.clicks
import com.jakewharton.rxbinding.widget.textChanges
import kotlinx.android.synthetic.main.activity_wizard.*
import kotlinx.android.synthetic.main.static_expense_listitem.view.*
import kotlinx.android.synthetic.main.wizard_screen_three.*
import kotlinx.android.synthetic.main.wizard_screen_two.*
import rx.Observable
import java.text.NumberFormat

class WizardActivity : AppCompatActivity() {

    private var staticExpenses: MutableList<StaticExpense> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wizard)

        welcomeCoordinator.addPage(
                R.layout.wizard_screen_one,
                R.layout.wizard_screen_two,
                R.layout.wizard_screen_three
        )

        floatingButtonPageThreeNext.setOnClickListener {
            UserRepository.finishedSetup().safeSubscribe {
                finish()
            }
        }

        numberpickerIncomeInterval.minValue = 1
        numberpickerIncomeInterval.maxValue = 365
        numberpickerIncomeInterval.wrapSelectorWheel = false

        setupSectionTwo()
        setupScreenThree()
    }

    private fun setupSectionTwo() {
        UserRepository.monitorUser()
                .take(1)
                .observeOnMainThread()
                .safeSubscribe(onNext = {
                    setupListeners()
                    setDefaults(it)
                })
    }

    private fun setDefaults(user: User) {
        edittextIncome.setText(user.income.toString())
        numberpickerIncomeInterval.value = user.payInterval
    }

    private fun setupListeners() {
        Observable
                .combineLatest(edittextIncome.textChanges(), numberpickerIncomeInterval.valueChanges(), { incomeStr, days ->
                    incomeStr.toString().toDoubleOrZero() to days
                })
                .delay { UserRepository.updateIncome(it.first, it.second) }
                .map { it.first / it.second }
                .map { NumberFormat.getCurrencyInstance().format(it) }
                .map { "Daily Income: $it" }
                .observeOnMainThread()
                .safeSubscribe(onNext = { textviewDailyIncome.text = it })
    }

    private fun setupScreenThree() {
        recyclerviewBudgets.layoutManager = LinearLayoutManager(this)
        recyclerviewBudgets.adapter = StaticExpenseAdapter()

        StaticExpenseRepository.monitorAll()
                .observeOnMainThread()
                .safeSubscribe(onNext = {
                    // TODO compare IDs or something. Maybe one swapped.
                    if (it.size == staticExpenses.size) return@safeSubscribe
                    staticExpenses = it.toMutableList()
                    recyclerviewBudgets.adapter.notifyDataSetChanged()
                })

        floatingButtonCreateBudget.clicks()
                .flatMap { StaticExpenseRepository.create() }
                .safeSubscribe()
    }

    private inner class StaticExpenseAdapter : RecyclerView.Adapter<StaticExpenseHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) = StaticExpenseHolder(parent?.inflate(R.layout.static_expense_listitem) as ViewGroup)

        override fun getItemCount() = staticExpenses.size

        override fun onBindViewHolder(holder: StaticExpenseHolder?, position: Int) {
            holder?.bind(staticExpenses[position])
        }
    }

    private inner class StaticExpenseHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent) {
        fun bind(staticExpense: StaticExpense) {
            itemView.staticExpenseView.setStaticExpense(staticExpense)
            itemView.buttonDelete.setOnClickListener {
                StaticExpenseRepository.delete(staticExpense.id).safeSubscribe()
            }
        }
    }
}
