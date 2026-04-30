package org.qrcode.scanner.di
import org.koin.dsl.module
import org.qrcode.scanner.bridge.AndroidNativeBridge
import org.qrcode.scanner.bridge.AndroidNativeUI
import org.qrcode.scanner.bridge.NativeBridge
import org.qrcode.scanner.bridge.NativeBridgeUI

val androidModule = module {
    single<NativeBridge> { AndroidNativeBridge() }
    single<NativeBridgeUI> { AndroidNativeUI() }
}