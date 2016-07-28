package com.bclymer.dailybudget.core.database

import io.realm.Realm
import io.realm.RealmObject
import java.util.*
import java.util.concurrent.atomic.AtomicLong
import kotlin.reflect.KClass

/**
 * Created by brianclymer on 7/28/16.
 * Copyright Travefy, Inc.
 */

internal object PrimaryKeyGenerator {

    private val classesMap: HashMap<KClass<*>, AtomicLong> = hashMapOf()

    internal fun <T> getId(clazz: KClass<T>, realm: Realm): Long where T : RealmObject {
        synchronized(classesMap) {
            if (!classesMap.contains(clazz)) {
                val maxId = realm.where(clazz.java).max("id")?.toLong() ?: 0
                classesMap[clazz] = AtomicLong(maxId)
            }
            return classesMap[clazz]!!.incrementAndGet()
        }
    }

}