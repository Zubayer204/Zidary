package org.zcorp.zidary.di

import org.koin.dsl.module
import org.zcorp.zidary.db.ZidaryDatabase

actual val platformModule = module {
    single { database }
}

lateinit var database: ZidaryDatabase