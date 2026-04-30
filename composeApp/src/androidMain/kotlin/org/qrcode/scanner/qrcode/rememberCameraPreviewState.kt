package org.qrcode.scanner.qrcode

import androidx.compose.runtime.*

/**
 * Creates and remembers [CameraPreviewStateAndroid].
 *
 * [rememberUpdatedState] ensures every callback always references the latest
 * lambda — safe even when the parent recomposes with new lambda instances.
 *
 * Usage:
 *   val cameraState = rememberCameraPreviewState(
 *       onScanResult    = { result -> viewModel.onResult(result) },
 *       onTorchAction   = { viewModel.onTorchToggled()           },
 *       onFlipAction    = { viewModel.onCameraFlipped()          },
 *       onGalleryAction = { viewModel.onGalleryOpened()          },
 *       onError         = { msg -> viewModel.onError(msg)        },
 *   )
 *
 *   // Wire buttons (wherever BottomControlsBar is rendered):
 *   isFlashOn     = cameraState.torchEnabled
 *   onToggleFlash = cameraState::toggleTorch    ← torch toggle
 *   onFlipCamera  = cameraState::flipCamera     ← flip toggle
 *   onGallery     = cameraState::openGallery    ← gallery toggle
 */
@Composable
fun rememberCameraPreviewStateAndroid(
    onScanResult: (String?, String?) -> Unit,
    onTorchAction: () -> Unit = {},
    onFlipAction: () -> Unit = {},
    onGalleryAction: () -> Unit = {},
    onError: (String) -> Unit = {},
): CameraPreviewStateAndroid {

    // Stable State<T> wrappers — always point to the latest lambda reference
    val latestScanResult = rememberUpdatedState(onScanResult)
    val latestTorchAction = rememberUpdatedState(onTorchAction)
    val latestFlipAction = rememberUpdatedState(onFlipAction)
    val latestGalleryAction = rememberUpdatedState(onGalleryAction)
    val latestError = rememberUpdatedState(onError)

    // Created ONCE — delegates to State wrappers so lambdas are always fresh
    return remember {
        CameraPreviewStateAndroid(
            onScanResult = { result, error ->
                onScanResult(result, error)
            },
            onTorchAction = { latestTorchAction.value() },
            onFlipAction = { latestFlipAction.value() },
            onGalleryAction = { latestGalleryAction.value() },
            onError = { latestError.value(it) },
        )
    }
}