package com.bclymer.dailybudget.core.database

import com.bclymer.dailybudget.extensions.daysUntil
import com.bclymer.dailybudget.models.Budget
import com.bclymer.dailybudget.models.Transaction
import io.realm.Sort
import rx.Observable
import java.util.*

/**
 * Created by brianclymer on 7/28/16.
 * Copyright Travefy, Inc.
 */
internal object BudgetRepository : BaseRepository<Budget>(Budget::class) {

    fun create(addInitialAllowance: Boolean = true): Observable<Budget> {
        val obs = executeTransaction {
            Budget.create(this)
        }

        if (addInitialAllowance) {
            return obs.flatMap { budget ->
                TransactionRepository.createSystemAllowance().map {
                    budget.transactions?.add(it)
                    budget
                }
            }
        } else {
            return obs
        }
    }

    fun monitorAll(): Observable<List<Budget>> {
        return where { findAll() }
                .asObservable()
                .map { it.toList() }
    }

    fun monitor(budgetId: Long): Observable<Budget> {
        val budget = loadDb { findById(budgetId) }
        if (budget != null) {
            return budget.asObservable()
        } else {
            return Observable.error(EntityNotFoundException(Budget::class, budgetId))
        }
    }

    fun delete(budgetId: Long): Observable<Unit> {
        return monitor(budgetId)
                .take(1)
                .flatMap { budget ->
                    executeTransaction {
                        budget.deleteFromRealm()
                    }
                }
    }

    fun updateBudget(budgetId: Long, name: String? = null, amount: Double? = null, intervalInDays: Int? = null): Observable<Unit> {
        return monitor(budgetId)
                .take(1)
                .flatMap { budget ->
                    executeTransaction {
                        if (name != null && budget.name != name) {
                            budget.name = name
                        }
                        if (amount != null && budget.allocation != amount) {
                            budget.allocation = amount
                        }
                        if (intervalInDays != null && budget.intervalInDays != intervalInDays) {
                            budget.intervalInDays = intervalInDays
                        }
                    }
                }
    }

    fun updateAllowances(): Observable<Unit> {
        return monitorAll()
                .take(1)
                .flatMap { Observable.from(it) }
                .flatMap {
                    updateAllowance(it)
                }
                .toList()
                .map { }
    }

    private fun updateAllowance(budget: Budget): Observable<Unit> {
        val transaction = budget.transactions!!.where().equalTo("systemAllowance", true).findAllSorted("id", Sort.DESCENDING).firstOrNull()
        if (transaction != null) {
            val daysMissing = transaction.date.daysUntil(Date())
            return executeTransaction {
                budget.cachedValue += daysMissing * budget.dailyAllocation
                val transactions = mutableListOf<Transaction>()
                for (i in 0..daysMissing) {
                    val newTransaction = Transaction.create(this)
                    newTransaction.systemAllowance = true
                    transactions.add(newTransaction)
                }
                budget.transactions?.addAll(transactions)
            }.map { }
        } else {
            return TransactionRepository.createSystemAllowance()
                    .map {
                        executeTransaction {
                            budget.cachedValue += budget.dailyAllocation
                            budget.transactions?.add(it)
                        }
                    }
                    .map { }
        }
    }

}