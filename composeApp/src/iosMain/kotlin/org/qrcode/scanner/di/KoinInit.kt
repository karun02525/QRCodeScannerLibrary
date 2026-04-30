package org.qrcode.scanner.di

fun initKoin() {
    KoinHelper.startKoinApp(
        platformModules = listOf(iosModule)
    )
}