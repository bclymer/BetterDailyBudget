package com.bclymer.dailybudget

import android.app.Application
import com.bclymer.dailybudget.core.database.DatabaseManager

/**
 * Created by Brian on 7/18/2016.
 */
class BudgetApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        DatabaseManager.setup(this)

        // TODO profile this.
        //StaticExpenseRepository.updateAllowances().safeSubscribe()
    }

}