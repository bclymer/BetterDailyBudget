package com.bclymer.dailybudget.models

import io.realm.RealmList
import io.realm.RealmObject

/**
 * Created by Brian on 7/18/2016.
 */
open class User : RealmObject() {

    open var income: Double = 0.toDouble()
    open var payInterval: Int = 0
    open var budgets: RealmList<Budget>? = null

}