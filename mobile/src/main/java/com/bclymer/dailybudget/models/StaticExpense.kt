package com.bclymer.dailybudget.models

import com.bclymer.dailybudget.core.database.PrimaryKeyGenerator
import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Brian on 7/18/2016.
 */
open class StaticExpense : RealmObject() {

    @PrimaryKey
    open var id: Long = 0
    open var name: String = ""
    open var allocation: Double = 0.0
    open var intervalInDays: Int = 0
    open var dailyAllocation: Double = 0.0

    internal fun setExpenses(allocation: Double, intervalInDays: Int) {
        this.allocation = allocation
        this.intervalInDays = intervalInDays
        this.dailyAllocation = (allocation / intervalInDays)
    }

    companion object {
        fun create(realm: Realm): StaticExpense {
            val budget = StaticExpense()
            budget.id = PrimaryKeyGenerator.getId(StaticExpense::class, realm)
            return realm.copyToRealm(budget)
        }
    }

}