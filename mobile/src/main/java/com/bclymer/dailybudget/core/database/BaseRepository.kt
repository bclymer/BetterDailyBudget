package com.bclymer.dailybudget.core.database

import com.bclymer.dailybudget.core.failOnBackgroundThread
import com.bclymer.dailybudget.extensions.findById
import com.bclymer.dailybudget.extensions.findByIdAsync
import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmQuery
import rx.Observable
import kotlin.reflect.KClass

/**
 * Created by brianclymer on 7/26/16.
 * Copyright Travefy, Inc.
 */
internal abstract class BaseRepository<T>(private val clazz: KClass<T>) where T : RealmObject {

    companion object MainRealm {
        private val mainRealm by lazy {
            val realm = Realm.getDefaultInstance()
            realm.isAutoRefresh = true
            realm
        }
    }

    inline fun <T> loadDb(func: Realm.() -> T): T {
        failOnBackgroundThread("Load db uses the main realm, and can only be called on the main thread")
        return mainRealm.func()
    }

    inline fun <R> where(func: RealmQuery<T>.() -> R): R {
        return loadDb { where().func() }
    }

    fun Realm.where(): RealmQuery<T> {
        return where(clazz.java)
    }

    fun Realm.findById(id: Long): T? {
        return findById(clazz, id)
    }

    fun Realm.findByIdAsync(id: Long): Observable<T?> {
        return findByIdAsync(clazz, id)
    }

    inline fun <T> executeTransactionAsync(crossinline func: Realm.() -> T): Observable<T> {
        failOnBackgroundThread("Async writes must be called from the main thread")
        return Observable.create<T> { sub ->
            val realm = Realm.getDefaultInstance()
            realm.executeTransactionAsync({
                sub.onNext(it.func())
            }, {
                sub.onCompleted()
                realm.close()
            }, { error ->
                sub.onError(error)
                realm.close()
            })
        }
    }

    inline fun <T> executeTransaction(crossinline func: Realm.() -> T): Observable<T> {
        failOnBackgroundThread("Async writes must be called from the main thread")
        return Observable.create<T> { sub ->
            val realm = Realm.getDefaultInstance()
            sub.onStart()
            realm.executeTransaction {
                sub.onNext(it.func())
            }
            sub.onCompleted()
        }
    }

}

internal class EntityNotFoundException(clazz: KClass<*>, id: Long) : RuntimeException("Couldn't find ${clazz.simpleName} with id $id")