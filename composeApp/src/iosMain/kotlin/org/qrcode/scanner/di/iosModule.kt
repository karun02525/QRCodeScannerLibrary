package org.qrcode.scanner.di

import org.koin.dsl.module
import org.qrcode.scanner.bridge.IOSNativeBridgeUI
import org.qrcode.scanner.bridge.NativeBridge
import org.qrcode.scanner.bridge.NativeBridgeUI
import org.qrcode.scanner.bridge.iosBridge

val iosModule = module {
    // iosBridge is set from Swift (iOSApp.swift) before this module is loaded
    single<NativeBridge> {
        iosBridge ?: error("iosBridge not set. Call IOSBridgeKt.iosBridge = IOSBridgeImpl() before startKoinApp.")
    }
    single<NativeBridgeUI> { IOSNativeBridgeUI() }
}