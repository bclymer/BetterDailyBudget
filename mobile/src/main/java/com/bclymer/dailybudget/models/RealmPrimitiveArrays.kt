package com.bclymer.dailybudget.models

import io.realm.RealmObject

/**
 * Created by Brian on 7/18/2016.
 */
open class RealmDoubleArray : RealmObject() {

    open var joinedDoubles: String? = null

}

fun RealmDoubleArray.toList(): List<Double> {
    if (joinedDoubles != null) {
        return joinedDoubles!!.split(",").filter { !it.isNullOrBlank() }.map { it.toDouble() }
    } else {
        return listOf()
    }
}