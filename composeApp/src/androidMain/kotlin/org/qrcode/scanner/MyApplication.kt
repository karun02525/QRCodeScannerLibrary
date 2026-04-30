package org.qrcode.scanner

import android.app.Application
import org.qrcode.scanner.di.KoinHelper
import org.qrcode.scanner.di.androidModule

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        KoinHelper.startKoinApp(
            platformModules = listOf(androidModule)
        )
    }
}