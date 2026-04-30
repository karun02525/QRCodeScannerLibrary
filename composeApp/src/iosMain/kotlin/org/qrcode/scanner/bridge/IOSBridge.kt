package org.qrcode.scanner.bridge

import org.qrcode.scanner.qrcode.CameraPreviewState


interface IOSBridge : NativeBridge {
    fun qrCodeScannerUi(
        state: CameraPreviewState
    ): Any
}

/** Single entry-point set from iOSApp.swift before Koin starts. */
var iosBridge: IOSBridge? = null

