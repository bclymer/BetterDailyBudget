package com.bclymer.dailybudget.models

import io.realm.RealmList
import io.realm.RealmObject

/**
 * Created by Brian on 7/18/2016.
 */
open class User : RealmObject() {

    open var finishedSetup: Boolean = false
    open var income: Double = 0.0
    open var payInterval: Int = 0
    open var dailyAllocation: Double = 0.0
    open var staticExpenses: RealmList<StaticExpense> = RealmList()
    open var budgets: RealmList<Budget> = RealmList()

    internal fun setIncomeStats(income: Double, payInterval: Int) {
        this.income = income
        this.payInterval = payInterval
        this.dailyAllocation = (income / payInterval)
    }

}