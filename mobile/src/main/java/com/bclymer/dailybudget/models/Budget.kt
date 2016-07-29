package com.bclymer.dailybudget.models

import com.bclymer.dailybudget.core.database.PrimaryKeyGenerator
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Brian on 7/18/2016.
 */
open class Budget : RealmObject() {

    @PrimaryKey
    open var id: Long = 0
    open var name: String = ""
    open var allocation: Double = 0.toDouble()
    open var intervalInDays: Int = 0
    open var cachedValue: Double = 0.toDouble()
    open var transactions: RealmList<Transaction>? = null

    val dailyAllocation: Double
        get() = allocation / intervalInDays

    companion object {
        fun create(realm: Realm): Budget {
            val budget = Budget()
            budget.id = PrimaryKeyGenerator.getId(Budget::class, realm)
            return realm.copyToRealm(budget)
        }
    }

}