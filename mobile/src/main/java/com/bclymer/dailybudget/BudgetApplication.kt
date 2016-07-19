package com.bclymer.dailybudget

import android.app.Application

/**
 * Created by Brian on 7/18/2016.
 */
class BudgetApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        DatabaseManager.setup(this)
    }

}