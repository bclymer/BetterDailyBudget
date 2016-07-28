package com.bclymer.dailybudget.models

import com.bclymer.dailybudget.core.database.PrimaryKeyGenerator
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Brian on 7/18/2016.
 */
open class Category : RealmObject() {

    @PrimaryKey
    open var id: Long = 0
    open var name: String = ""
    open var transactions: RealmList<Transaction>? = null

    companion object {
        fun create(realm: Realm): Category {
            val category = Category()
            category.id = PrimaryKeyGenerator.getId(Category::class, realm)
            return realm.copyToRealm(category)
        }
    }

}