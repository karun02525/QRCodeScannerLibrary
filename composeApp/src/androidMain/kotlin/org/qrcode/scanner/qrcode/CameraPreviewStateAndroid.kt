package org.qrcode.scanner.qrcode

import androidx.compose.runtime.*

/**
 * Minimal state — owns all camera toggle logic.
 *
 * Toggle flow (example: torch):
 *   External button pressed
 *        ↓
 *   cameraState.toggleTorch()
 *        ↓
 *   torchEnabled flips (true ↔ false)
 *        ↓
 *   LaunchedEffect in CameraPreview fires → hardware torch applied
 *        ↓
 *   onTorchAction() → notifies KMP/ViewModel
 */
@Stable
class CameraPreviewStateAndroid(
    private val onScanResult: (String?, String?) -> Unit,
    private val onTorchAction: () -> Unit = {},
    private val onFlipAction: () -> Unit = {},
    private val onGalleryAction: () -> Unit = {},
    private val onError: (String) -> Unit = {},
) {

    // ── Observable state (drives LaunchedEffect + UI) ─────────────────────────

    /** Read by BottomControlsBar to show torch ON/OFF icon. */
    var torchEnabled by mutableStateOf(false)
        private set

    /** Triggers key(isFrontCamera) in CameraPreview → camera rebind. */
    var isFrontCamera by mutableStateOf(false)
        private set

    // ── Internal slots (set via SideEffect inside CameraPreview) ─────────────
    // Bridges composable-only APIs back into this class.

    internal var _launchGallery: (() -> Unit)? = null
    internal var _resetScan: (() -> Unit)? = null

    // ── Toggle Actions ────────────────────────────────────────────────────────

    /**
     * TOGGLE: torch ON → OFF → ON
     * Silent no-op when front camera is active (no torch hardware).
     * After toggle: calls [onTorchAction] to notify KMP/ViewModel.
     */
    fun toggleTorch() {
        if (!isFrontCamera) {
            torchEnabled = !torchEnabled   // ← actual toggle
            onTorchAction()               // ← notify outside
        }
    }

    /**
     * TOGGLE: back camera → front camera → back camera.
     * Auto-disables torch when switching to front (no hardware).
     * After toggle: calls [onFlipAction] to notify KMP/ViewModel.
     */
    fun flipCamera() {
        isFrontCamera = !isFrontCamera     // ← actual toggle
        if (isFrontCamera) {
            torchEnabled = false           // front camera has no torch
        }
        onFlipAction()                     // ← notify outside
    }

    /**
     * Opens system photo picker.
     * Result → ML Kit → raw String delivered via [onScanResult].
     * Calls [onGalleryAction] to notify KMP/ViewModel that picker opened.
     */
    fun openGallery() {
        _launchGallery?.invoke()           // ← actual gallery open
        onGalleryAction()                  // ← notify outside
    }

    /**
     * Re-arms the one-shot scan guard.
     * Call after [onScanResult] result has been consumed by the UI.
     */
    fun resetScan() {
        _resetScan?.invoke()
    }

    // ── Internal delivery (called only from CameraPreview) ───────────────────

    internal fun deliverScanResult(value: String?,value2: String?) = onScanResult(value,value2)
    internal fun deliverError(message: String) = onError(message)
}