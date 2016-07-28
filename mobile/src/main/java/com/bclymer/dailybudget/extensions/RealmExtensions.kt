package com.bclymer.dailybudget.extensions

import com.bclymer.dailybudget.core.failOnBackgroundThread
import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmQuery
import io.realm.Sort
import rx.Observable
import kotlin.reflect.KClass

/**
 * Created by brianclymer on 2/4/16.
 * Copyright Travefy, Inc.
 */

fun <T> Realm.findById(clazz: KClass<T>, id: Long): T? where T : RealmObject {
    val obs = where(clazz.java).equalTo("id", id).findFirst()
    return obs
}

fun <T> Realm.findByIdAsync(clazz: KClass<T>, id: Long): Observable<T?> where T : RealmObject {
    failOnBackgroundThread("Async queries are only allowed on the main thread.")
    val obs = this.where(clazz.java).equalTo("id", id).findAllAsync()
            .asObservable()
            .filter { it.isLoaded }
            .map { it.firstOrNull() }
            .take(1)
    return obs
}

fun <T> Realm.waitById(clazz: KClass<T>, id: Long): Observable<T> where T : RealmObject {
    val obs = this.where(clazz.java).equalTo("id", id)
            .monitorAsync()
            .map { it.firstOrNull() }
            .filterOutNulls()
            .take(1)
    return obs
}

fun <T> RealmQuery<T>.monitorSingleAsync(): Observable<T?> where T : RealmObject {
    val obs = this.findAllAsync()
            .asObservable()
            .filter { it.isLoaded }
            .map { it.firstOrNull() }
    return obs
}

fun <T> RealmQuery<T>.monitorAsync(): Observable<List<T>> where T : RealmObject {
    val obs = monitorSortedAsync()
    return obs
}

fun <T> RealmQuery<T>.monitorSortedAsync(sort: String? = null, order: Sort = Sort.ASCENDING): Observable<List<T>> where T : RealmObject {
    if (sort != null) {
        val obs = this.findAllSortedAsync(sort, order)
                .asObservable()
                .filter { it.isLoaded }
                .map { it.toList() }
        return obs
    } else {
        val obs = this.findAllAsync()
                .asObservable()
                .filter { it.isLoaded }
                .map { it.toList() }
        return obs
    }
}

fun <T> RealmQuery<T>.monitorCount(): Observable<Int> where T : RealmObject {
    val obs = this.findAllAsync()
            .asObservable()
            .filter { it.isLoaded }
            .map { it.size }
    return obs
}