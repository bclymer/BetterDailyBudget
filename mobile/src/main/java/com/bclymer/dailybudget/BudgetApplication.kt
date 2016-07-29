package com.bclymer.dailybudget

import android.app.Application
import com.bclymer.dailybudget.core.database.BudgetRepository
import com.bclymer.dailybudget.core.database.DatabaseManager
import com.bclymer.dailybudget.extensions.safeSubscribe

/**
 * Created by Brian on 7/18/2016.
 */
class BudgetApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        DatabaseManager.setup(this)

        // TODO profile this.
        BudgetRepository.updateAllowances().safeSubscribe()
    }

}