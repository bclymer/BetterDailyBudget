package com.bclymer.dailybudget.core.database

import com.bclymer.dailybudget.models.Tag
import rx.Observable

/**
 * Created by brianclymer on 7/28/16.
 * Copyright Travefy, Inc.
 */

internal object TagRepository : BaseRepository<Tag>(Tag::class) {

    fun monitorAll(): Observable<List<Tag>> {
        return where { findAll() }
                .asObservable()
                .map { it.toList() }
    }

}