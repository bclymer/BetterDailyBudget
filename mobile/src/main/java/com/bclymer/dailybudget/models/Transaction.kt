package com.bclymer.dailybudget.models

import com.bclymer.dailybudget.core.database.PrimaryKeyGenerator
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * Created by Brian on 7/18/2016.
 */
open class Transaction : RealmObject() {

    @PrimaryKey
    open var id: Long = 0
    open var notes: String = ""
    open var date: Date = Date()
    open var amount: Double = 0.0
    open var tags: RealmList<Tag> = RealmList()
    open var systemAllowance: Boolean = false

    companion object {
        fun create(realm: Realm): Transaction {
            val transaction = Transaction()
            transaction.id = PrimaryKeyGenerator.getId(Transaction::class, realm)
            transaction.date = Date()
            return realm.copyToRealm(transaction)
        }
    }

}