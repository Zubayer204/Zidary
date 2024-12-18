package org.zcorp.zidary.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import org.zcorp.zidary.model.data.JournalFactory
import org.zcorp.zidary.viewModel.CalendarVM
import org.zcorp.zidary.viewModel.HomeVM
import org.zcorp.zidary.viewModel.JournalComposeVM
import org.zcorp.zidary.viewModel.SyncVM

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(commonModule)
    }

val commonModule = module {
    single { JournalFactory(get()) }
    single { HomeVM(get()) }
    single { CalendarVM(get()) }
    single { JournalComposeVM(get()) }
    single { SyncVM(get()) }
}