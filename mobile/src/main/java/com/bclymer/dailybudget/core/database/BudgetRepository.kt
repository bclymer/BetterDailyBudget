package com.bclymer.dailybudget.core.database

import com.bclymer.dailybudget.models.Budget
import rx.Observable

/**
 * Created by brianclymer on 7/29/16.
 * Copyright Travefy, Inc.
 */
internal object BudgetRepository : BaseRepository<Budget>(Budget::class) {

    fun monitorAll(): Observable<List<Budget>> =
            UserRepository.monitorUser().map { it.budgets.toList() }

}