package com.bclymer.dailybudget.models

import com.bclymer.dailybudget.core.database.PrimaryKeyGenerator
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * Created by brianclymer on 7/29/16.
 * Copyright Travefy, Inc.
 */

open class Budget : RealmObject() {

    @PrimaryKey
    open var id: Long = 0
    open var name: String = ""
    open var dailyAllocation: Double = 0.0
    open var funds: Double = 0.0
    open var lastDateApplied: Date = Date()
    open var transactions: RealmList<Transaction> = RealmList()
    open var defaultBudget: Boolean = false

    companion object {
        fun create(realm: Realm): Budget {
            val budget = Budget()
            budget.id = PrimaryKeyGenerator.getId(Budget::class, realm)
            return realm.copyToRealm(budget)
        }
    }

}