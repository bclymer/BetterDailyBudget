package com.bclymer.dailybudget.core.database

import com.bclymer.dailybudget.extensions.add
import com.bclymer.dailybudget.extensions.asVoid
import com.bclymer.dailybudget.extensions.atMidnight
import com.bclymer.dailybudget.extensions.daysUntil
import com.bclymer.dailybudget.models.Budget
import com.bclymer.dailybudget.models.Transaction
import rx.Observable
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by brianclymer on 7/29/16.
 * Copyright Travefy, Inc.
 */
internal object BudgetRepository : BaseRepository<Budget>(Budget::class) {

    fun monitorAll(): Observable<List<Budget>> =
            UserRepository.monitorUser().map { it.budgets.toList() }

    fun updateAllowances(): Observable<Unit> {
        return monitorAll()
                .take(1)
                .flatMap { Observable.from(it) }
                .flatMap {
                    updateAllowance(it)
                }
                .toList()
                .asVoid()
    }

    private fun updateAllowance(budget: Budget): Observable<Unit> {
        val daysMissing = budget.lastDateApplied.daysUntil(Date())
        return executeTransaction {
            budget.funds += daysMissing * budget.dailyAllocation
            val transactions = mutableListOf<Transaction>()
            for (i in 0..daysMissing) {
                val newTransaction = Transaction.create(this)
                newTransaction.date = budget.lastDateApplied.add(i, TimeUnit.DAYS).atMidnight
                newTransaction.systemAllowance = true
                transactions.add(newTransaction)
            }
        }.asVoid()
    }

}