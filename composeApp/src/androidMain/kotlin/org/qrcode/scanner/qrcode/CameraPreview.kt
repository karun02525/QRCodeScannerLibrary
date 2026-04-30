package org.qrcode.scanner.qrcode

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.atomic.AtomicBoolean


@OptIn(ExperimentalGetImage::class)
@Composable
fun CameraPreview(
    state    : CameraPreviewStateAndroid,
    modifier : Modifier = Modifier,
) {
    val context        = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val executor       = remember { ContextCompat.getMainExecutor(context) }
    val mlKitScanner   = remember { BarcodeScanning.getClient() }

    // Thread-safe one-shot guard shared between camera stream + gallery
    val hasScanned = remember { AtomicBoolean(false) }

    // Camera ref — ONLY used for torch (avoids full camera rebind for torch)
    var camera by remember { mutableStateOf<Camera?>(null) }

    // ── Gallery Picker ────────────────────────────────────────────────────────
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri: Uri? ->
        uri?.let {
            processGalleryUri(
                context  = context,
                uri      = it,
                scanner  = mlKitScanner,
                onResult = { raw ->
                    // Same one-shot guard — camera + gallery never both fire
                    if (hasScanned.compareAndSet(false, true)) {
                        state.deliverScanResult(raw, null)
                    }
                },
                onError = state::deliverError,
            )
        }
    }

    // ── Inject composable-only slots into state ───────────────────────────────
    SideEffect {
        state._launchGallery = {
            galleryLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
        state._resetScan = {
            hasScanned.set(false)
        }
    }

    // ── Torch control ─────────────────────────────────────────────────────────
    // Fires when: (a) state.torchEnabled flips  (b) camera ref becomes non-null
    // No camera rebind needed — cameraControl.enableTorch() is lightweight
    LaunchedEffect(state.torchEnabled, camera) {
        camera?.cameraControl?.enableTorch(state.torchEnabled)
    }

    // ── Scan guard reset on camera flip ───────────────────────────────────────
    LaunchedEffect(state.isFrontCamera) {
        hasScanned.set(false)
    }

    // ── Stable PreviewView ────────────────────────────────────────────────────
    // Created OUTSIDE key() block → same surface reused on flip → no black flash
    val previewView = remember {
        PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }

    // ── Camera Binding ────────────────────────────────────────────────────────
    // key(state.isFrontCamera) → when isFrontCamera changes:
    //   Step 1 → onDispose() fires  → unbindAll, camera = null
    //   Step 2 → block re-runs      → bindToLifecycle with new selector
    key(state.isFrontCamera) {
        DisposableEffect(lifecycleOwner) {
            var cameraProvider: ProcessCameraProvider? = null

            val cameraSelector = if (state.isFrontCamera)
                CameraSelector.DEFAULT_FRONT_CAMERA
            else
                CameraSelector.DEFAULT_BACK_CAMERA

            val future = ProcessCameraProvider.getInstance(context)

            future.addListener(
                {
                    runCatching {
                        cameraProvider = future.get()

                        // Use Case 1: Camera preview feed
                        val preview = Preview.Builder()
                            .build()
                            .also { it.surfaceProvider = previewView.surfaceProvider }

                        // Use Case 2: ML Kit barcode analysis
                        val imageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .apply {
                                setAnalyzer(executor) { imageProxy ->
                                    analyzeFrame(
                                        imageProxy = imageProxy,
                                        scanner    = mlKitScanner,
                                        hasScanned = hasScanned,
                                        onResult   = { raw -> state.deliverScanResult(raw,null) },
                                    )
                                }
                            }

                        // Always unbind before rebinding — prevents "use case already bound"
                        cameraProvider?.unbindAll()

                        // Bind and store camera ref for torch control
                        camera = cameraProvider?.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageAnalysis,
                        )

                        // Apply current torch state right after binding
                        camera?.cameraControl?.enableTorch(state.torchEnabled)

                    }.onFailure { error ->
                        state.deliverError(error.message ?: "Camera init failed")
                    }
                },
                executor,
            )

            onDispose {
                cameraProvider?.unbindAll()
                camera = null   // clears torch LaunchedEffect reference
            }
        }
    }

    // ── Render: pure camera surface only ─────────────────────────────────────
    AndroidView(
        factory  = { previewView },
        modifier = modifier,
    )
}

// ── Private helpers ───────────────────────────────────────────────────────────

/**
 * Processes one camera frame.
 * Fast-exits without processing if scan already captured.
 */
@OptIn(ExperimentalGetImage::class)
private fun analyzeFrame(
    imageProxy : ImageProxy,
    scanner    : BarcodeScanner,
    hasScanned : AtomicBoolean,
    onResult   : (String) -> Unit,
) {
    val mediaImage = imageProxy.image

    // Skip frame if no image or already scanned
    if (mediaImage == null || hasScanned.get()) {
        imageProxy.close()
        return
    }

    val inputImage = InputImage.fromMediaImage(
        mediaImage,
        imageProxy.imageInfo.rotationDegrees,
    )

    scanner.process(inputImage)
        .addOnSuccessListener { barcodes ->
            barcodes.firstOrNull()?.rawValue?.let { raw ->
                // compareAndSet → only ONE concurrent frame wins the race
                if (hasScanned.compareAndSet(false, true)) {
                    onResult(raw)
                }
            }
        }
        .addOnCompleteListener {
            // CRITICAL: always close — skipping this permanently stalls the analyser
            imageProxy.close()
        }
}

/**
 * Processes a gallery [Uri] via ML Kit.
 * Delivers raw STRING result — the [Uri] never leaves this function.
 */
private fun processGalleryUri(
    context  : Context,
    uri      : Uri,
    scanner  : BarcodeScanner,
    onResult : (String) -> Unit,
    onError  : (String) -> Unit,
) {
    // fromFilePath handles bitmap decode + EXIF rotation automatically
    val inputImage = runCatching { InputImage.fromFilePath(context, uri) }
        .getOrElse { e ->
            onError("Failed to load image: ${e.message}")
            return
        }

    scanner.process(inputImage)
        .addOnSuccessListener { barcodes ->
            val raw = barcodes.firstOrNull()?.rawValue
            if (raw != null) onResult(raw)
            else onError("No QR code or barcode found in selected image")
        }
        .addOnFailureListener { e ->
            onError("Gallery scan failed: ${e.message}")
        }
}