package org.qrcode.scanner.bridge

data class LocationData(val latitude: Double, val longitude: Double)

interface NativeBridge {
    fun showToast(message: String)
    fun share(message: String)
}

