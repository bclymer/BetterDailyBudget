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
        Category::class,
        RealmDoubleArray::class,
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
                            "Allowance",
                            "Automobile",
                            "Amusement",
                            "Movies",
                            "Alcohol",
                            "Coffee Shops",
                            "Food",
                            "Gift",
                            "Health & Fitness",
                            "Home Improvement",
                            "Personal Care",
                            "Pets",
                            "Shopping",
                            "Travel",
                            "Taxes",
                            "Vacation"
                    )
                    defaultCategories.forEachIndexed { index, name ->
                        val category = Category.create(realm)
                        category.name = name
                    }
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

        var currentVersion = oldVersion
        val schema = realm.schema
        if (currentVersion == 0L) {

        }
    }

}