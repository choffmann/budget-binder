package de.hsfl.budgetBinder.android

import android.app.Application
import de.hsfl.budgetBinder.common.HelloWorld
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.bindSingleton

class BudgetBinderApplication: Application(), DIAware {
    override val di by DI.lazy {
        import(androidXModule(this@BudgetBinderApplication))
        bindSingleton { HelloWorld() }
    }
}