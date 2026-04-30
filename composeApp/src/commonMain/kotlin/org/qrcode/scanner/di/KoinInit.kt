package org.qrcode.scanner.di

import org.koin.core.context.startKoin
import org.koin.core.module.Module

object KoinHelper {
    fun startKoinApp(platformModules: List<Module>) {
        startKoin {
            modules(commonModule + platformModules)
        }
    }
}