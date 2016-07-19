package com.bclymer.dailybudget.models

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

/**
 * Created by Brian on 7/18/2016.
 */
open class Category : RealmObject() {

    @PrimaryKey
    @Required
    open var id: Long? = null
    open var name: String = ""
    open var transactions: RealmList<Transaction>? = null

}