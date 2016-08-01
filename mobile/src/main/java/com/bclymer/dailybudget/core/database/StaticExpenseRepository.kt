package com.bclymer.dailybudget.core.database

import com.bclymer.dailybudget.models.StaticExpense
import rx.Observable

/**
 * Created by brianclymer on 7/28/16.
 * Copyright Travefy, Inc.
 */
internal object StaticExpenseRepository : BaseRepository<StaticExpense>(StaticExpense::class) {

    fun create(): Observable<StaticExpense> {
        return executeTransaction {
            StaticExpense.create(this)
        }
    }

    fun monitorAll(): Observable<List<StaticExpense>> {
        return where { findAll() }
                .asObservable()
                .map { it.toList() }
    }

    fun monitor(staticExpenseId: Long): Observable<StaticExpense> {
        val budget = loadDb { findById(staticExpenseId) }
        if (budget != null) {
            return budget.asObservable()
        } else {
            return Observable.error(EntityNotFoundException(StaticExpense::class, staticExpenseId))
        }
    }

    fun delete(staticExpenseId: Long): Observable<Unit> {
        return monitor(staticExpenseId)
                .take(1)
                .flatMap { budget ->
                    executeTransaction {
                        budget.deleteFromRealm()
                    }
                }
    }

    fun updateStaticExpense(staticExpenseId: Long, name: String) =
            updateStaticExpense(staticExpenseId, name = name, amount = null, intervalInDays = null)

    fun updateStaticExpense(staticExpenseId: Long, amount: Double, intervalInDays: Int) =
            updateStaticExpense(staticExpenseId, name = null, amount = amount, intervalInDays = intervalInDays)

    private fun updateStaticExpense(staticExpenseId: Long, name: String? = null, amount: Double? = null, intervalInDays: Int? = null): Observable<Unit> {
        return monitor(staticExpenseId)
                .take(1)
                .flatMap { staticExpense ->
                    executeTransaction {
                        if (name != null && staticExpense.name != name) {
                            staticExpense.name = name
                        }
                        if (amount != null && staticExpense.allocation != amount &&
                                intervalInDays != null && staticExpense.intervalInDays != intervalInDays) {
                            staticExpense.allocation = amount
                            staticExpense.intervalInDays = intervalInDays
                        }
                    }
                }
    }

}