package de.hsfl.budgetBinder.android

import android.app.Application

class BudgetBinderApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }
    companion object {
        lateinit var instance: BudgetBinderApplication
            private set
    }
}
