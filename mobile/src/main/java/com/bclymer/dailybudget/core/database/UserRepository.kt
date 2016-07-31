package com.bclymer.dailybudget.core.database

import com.bclymer.dailybudget.models.User
import rx.Observable

/**
 * Created by brianclymer on 7/26/16.
 * Copyright Travefy, Inc.
 */

// TODO: dagger 2
internal object UserRepository : BaseRepository<User>(User::class) {

    fun monitorUser(): Observable<User> {
        val user = where { findFirst() }
        if (user != null) {
            return user.asObservable()
        } else {
            return executeTransaction {
                createObject(User::class.java)
            }
        }
    }

    fun updateIncome(income: Double, payInterval: Int): Observable<Unit> {
        return executeTransactionAsync {
            val user = where().findFirst()
            user.income = income
            user.payInterval = payInterval
        }
    }

    fun finishedSetup(): Observable<Unit> {
        return monitorUser()
                .take(1)
                .flatMap { user ->
                    executeTransaction {
                        user.finishedSetup = true
                    }
                }
                .map { }
    }

    fun verifyBudgetsWithinBounds(): Observable<BudgetVerification> {
        return totalBudgetAllocationsPerDay()
                .withLatestFrom(totalStaticExpensesPerDay(), { budgets, expenses ->
                    budgets to expenses
                })
                .withLatestFrom(monitorUser().take(1)) { totalDailyUse, user ->
                    val (budgets, expenses) = totalDailyUse
                    val verification = BudgetVerification(
                            budgets = budgets,
                            expenses = expenses,
                            income = user.dailyAllocation
                    )
                    if ((budgets + expenses) > user.dailyAllocation) {
                        verification.valid = false
                    }
                    verification
                }
    }

    fun totalStaticExpensesPerDay(): Observable<Double> =
            monitorUser().take(1).map { it.staticExpenses.sum("dailyAllocation")?.toDouble() ?: 0.0 }

    fun totalBudgetAllocationsPerDay(): Observable<Double> =
            monitorUser().take(1).map { it.budgets.sum("dailyAllocation")?.toDouble() ?: 0.0 }

}

data class BudgetVerification(var valid: Boolean = true, val budgets: Double, val expenses: Double, val income: Double)