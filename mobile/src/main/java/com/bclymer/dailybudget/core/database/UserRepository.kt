package com.bclymer.dailybudget.core.database

import com.bclymer.dailybudget.models.User
import rx.Observable

/**
 * Created by brianclymer on 7/26/16.
 * Copyright Travefy, Inc.
 */

// TODO: dagger 2
internal object UserRepository : BaseRepository<User>(User::class) {

    fun getUserOrCreate(): Observable<User> {
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

}