package com.bclymer.dailybudget.core.database

import android.content.Context
import com.bclymer.dailybudget.models.*
import io.realm.DynamicRealm
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmMigration
import io.realm.annotations.RealmModule

/**
 * Created by Brian on 7/18/2016.
 */
@RealmModule(classes = arrayOf(
        Budget::class,
        Tag::class,
        StaticExpense::class,
        Transaction::class,
        User::class
))
class RealmManager {
}

object DatabaseManager {

    fun setup(context: Context) {
        val realmConfiguration = RealmConfiguration.Builder(context)
                .schemaVersion(1)
                .migration(Migration())
                .modules(RealmManager())
                .initialData { realm ->
                    val defaultCategories = listOf(
                            "allowance",
                            "automobile",
                            "amusement",
                            "alcohol",
                            "coffee",
                            "food",
                            "gas",
                            "gift",
                            "groceries",
                            "health",
                            "home",
                            "movies",
                            "personal",
                            "pets",
                            "shopping",
                            "travel",
                            "taxes",
                            "vacation"
                    )
                    defaultCategories.forEachIndexed { index, name ->
                        val tag = Tag.create(realm)
                        tag.name = name
                    }

                    val user = realm.createObject(User::class.java)
                    val defaultBudget = Budget.create(realm)
                    defaultBudget.defaultBudget = true
                    user.budgets.add(defaultBudget)
                }
                .build()
        Realm.setDefaultConfiguration(realmConfiguration)
    }

}

private class Migration : RealmMigration {

    override fun migrate(realm: DynamicRealm?, oldVersion: Long, newVersion: Long) {
        if (realm == null) {
            return // uhhh? what should I do here
        }

        /*
        var currentVersion = oldVersion
        val schema = realm.schema
        if (currentVersion == 0L) {

        }
        */
    }

}