package com.bclymer.dailybudget.core.database

import com.bclymer.dailybudget.models.Category
import rx.Observable

/**
 * Created by brianclymer on 7/28/16.
 * Copyright Travefy, Inc.
 */

internal object CategoryRepository : BaseRepository<Category>(Category::class) {

    fun monitorAll(): Observable<List<Category>> {
        return where { findAll() }
                .asObservable()
                .map { it.toList() }
    }

}