package org.zcorp.zidary.di

import org.zcorp.zidary.db.ZidaryDatabase
import org.koin.dsl.module

actual val platformModule = module {
    single { database }
}

lateinit var database: ZidaryDatabase

