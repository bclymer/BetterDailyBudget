package com.bclymer.dailybudget.core

import android.content.Intent
import com.bclymer.dailybudget.MainActivity
import com.bclymer.dailybudget.wizard.WizardActivity

/**
 * Created by brianclymer on 7/31/16.
 * Copyright Travefy, Inc.
 */

fun MainActivity.presentWizard() {
    startActivity(Intent(this, WizardActivity::class.java))
}