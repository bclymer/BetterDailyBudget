package com.bclymer.dailybudget.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import java.util.*

/**
 * Created by Brian on 7/18/2016.
 */
open class Transaction : RealmObject() {

    @PrimaryKey
    @Required
    open var id: Long? = null
    open var notes: String = ""
    open var date: Date = Date()
    open var amounts: RealmList<RealmDoubleArray>? = null
    open var category: Category? = null

}