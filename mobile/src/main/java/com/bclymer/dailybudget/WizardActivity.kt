package com.bclymer.dailybudget

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.NumberPicker
import com.redbooth.WelcomeCoordinatorLayout

class WizardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wizard)

        val coordinator = findViewById(R.id.activity_wizard) as WelcomeCoordinatorLayout
        coordinator.addPage(
                R.layout.wizard_screen_one,
                R.layout.wizard_screen_two,
                R.layout.wizard_screen_three
        )

        val numberPicker = coordinator.findViewById(R.id.wizard_screen_two_numberpicker) as NumberPicker
        numberPicker.minValue = 1
        numberPicker.maxValue = 365
        numberPicker.wrapSelectorWheel = false
    }
}
