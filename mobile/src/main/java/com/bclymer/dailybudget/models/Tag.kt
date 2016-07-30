package com.bclymer.dailybudget.models

import com.bclymer.dailybudget.core.database.PrimaryKeyGenerator
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by Brian on 7/18/2016.
 */
open class Tag : RealmObject() {

    @PrimaryKey
    open var id: Long = 0
    open var name: String = ""
    open var transactions: RealmList<Transaction>? = null

    companion object {
        fun create(realm: Realm): Tag {
            val tag = Tag()
            tag.id = PrimaryKeyGenerator.getId(Tag::class, realm)
            return realm.copyToRealm(tag)
        }
    }

}