package com.bclymer.dailybudget.core.database

import com.bclymer.dailybudget.models.Transaction
import rx.Observable

/**
 * Created by brianclymer on 7/29/16.
 * Copyright Travefy, Inc.
 */

internal object TransactionRepository : BaseRepository<Transaction>(Transaction::class) {

    fun createSystemAllowance(): Observable<Transaction> {
        return executeTransaction {
            val transaction = Transaction.create(this)
            transaction.systemAllowance = true
            transaction
        }
    }

}