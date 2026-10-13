package com.mafi.budgetapp

import android.app.Application
import com.mafi.budgetapp.di.AppContainer

class BudgetApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}