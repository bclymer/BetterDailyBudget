package com.bclymer.dailybudget

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.ViewGroup
import com.bclymer.dailybudget.core.database.BudgetRepository
import com.bclymer.dailybudget.extensions.inflate
import com.bclymer.dailybudget.extensions.observeOnMainThread
import com.bclymer.dailybudget.extensions.safeSubscribe
import com.bclymer.dailybudget.extensions.snack
import com.bclymer.dailybudget.models.Budget
import com.bclymer.dailybudget.wizard.WizardActivity
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.main_budget_listitem.view.*
import java.text.NumberFormat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        recyclerviewBudgets.layoutManager = LinearLayoutManager(this)
        recyclerviewBudgets.adapter = BudgetsAdapter()

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            startActivity(Intent(view.context, WizardActivity::class.java))
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show()
        }
    }

    private inner class BudgetsAdapter : RecyclerView.Adapter<BudgetViewHolder>() {

        private var budgets = listOf<Budget>()

        init {
            BudgetRepository.monitorAll()
                    .observeOnMainThread()
                    .safeSubscribe(onNext = {
                        budgets = it
                        notifyDataSetChanged()
                    })
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) =
                BudgetViewHolder(parent?.inflate(R.layout.main_budget_listitem) as ViewGroup)

        override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) =
                holder.bind(budgets[position])

        override fun getItemCount() =
                budgets.size

    }

    private inner class BudgetViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent) {
        fun bind(budget: Budget) {
            itemView.textviewName.text = budget.name
            itemView.textviewAmount.text = NumberFormat.getCurrencyInstance().format(budget.dailyAllocation)
            itemView.buttonAddTransaction.setOnClickListener {
                itemView.snack("Alrighty.")
            }
        }
    }

}
