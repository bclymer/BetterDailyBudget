package com.bclymer.dailybudget.core.database

import com.bclymer.dailybudget.models.Budget
import rx.Observable

/**
 * Created by brianclymer on 7/28/16.
 * Copyright Travefy, Inc.
 */
internal object BudgetRepository : BaseRepository<Budget>(Budget::class) {

    fun create(): Observable<Budget> {
        return executeTransaction {
            Budget.create(this)
        }
    }

    fun getAll(): Observable<List<Budget>> {
        val obs = Observable.create<List<Budget>> { sub ->
            sub.onStart()
            val budgets = where { findAll() }
            sub.onNext(budgets.toList())
            sub.onCompleted()
        }
        return obs
    }

    fun get(budgetId: Long): Observable<Budget> {
        val obs = Observable.create<Budget> { sub ->
            sub.onStart()
            val budget = loadDb { findById(budgetId) }
            if (budget != null) {
                sub.onNext(budget)
                sub.onCompleted()
            } else {
                sub.onError(EntityNotFoundException(Budget::class, budgetId))
            }
        }
        return obs
    }

    fun delete(budgetId: Long): Observable<Unit> {
        return get(budgetId).flatMap { budget ->
            executeTransaction {
                budget.deleteFromRealm()
            }
        }
    }

    fun updateBudget(budgetId: Long, name: String? = null, amount: Double? = null, intervalInDays: Int? = null): Observable<Unit> {
        return get(budgetId)
                .flatMap { budget ->
                    executeTransaction {
                        if (name != null) {
                            budget.name = name
                        }
                        if (amount != null) {
                            budget.amount = amount
                        }
                        if (intervalInDays != null) {
                            budget.intervalInDays = intervalInDays
                        }
                    }
                }
    }

}