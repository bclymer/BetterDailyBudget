package com.bclymer.dailybudget.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

/**
 * Created by Brian on 7/18/2016.
 */
open class Budget : RealmObject() {

    @PrimaryKey
    @Required
    open var id: Long? = null
    open var name: String = ""
    open var dailyAllocation: Double = 0.toDouble()
    open var rolloverPeriodInDays: Int = 0
    open var cachedValue: Double = 0.toDouble()
    open var transactions: RealmList<Transaction>? = null

}