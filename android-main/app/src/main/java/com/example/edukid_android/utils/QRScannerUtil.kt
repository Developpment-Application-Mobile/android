package com.example.edukid_android.utils

// utils/QRScannerUtil.kt


import android.Manifest
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

class QRScannerUtil(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner
) {
    private val executor = Executors.newSingleThreadExecutor()
    private val scanner: BarcodeScanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
    )

    // Internal method to set up the camera and analysis
    @OptIn(ExperimentalGetImage::class)
    internal fun setupCameraAnalysis(
        previewView: PreviewView,
        onQRDetected: (String?) -> Unit
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { analysis ->
                    analysis.setAnalyzer(executor) { imageProxy ->
                        val mediaImage = imageProxy.image
                        if (mediaImage != null) {
                            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                            scanner.process(image)
                                .addOnSuccessListener { barcodes ->
                                    for (barcode in barcodes) {
                                        barcode.rawValue?.let { qrCode ->
                                            onQRDetected(qrCode)
                                        }
                                    }
                                }
                                .addOnFailureListener {
                                    onQRDetected(null)
                                }
                                .addOnCompleteListener {
                                    imageProxy.close()
                                }
                        } else {
                            imageProxy.close()
                        }
                    }
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )
            } catch (exc: Exception) {
                onQRDetected(null)
            }
        }, ContextCompat.getMainExecutor(context))
    }
}

@Composable
fun QRScannerComposable(
    showScanner: Boolean,
    onResult: (String?) -> Unit,
    modifier: Modifier = Modifier.fillMaxSize()  // Default to full screen, but customizable
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val qrScannerUtil = remember { QRScannerUtil(context, lifecycleOwner) }

    if (showScanner) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    // Ensure the camera preview fits within the square without bleeding
                    scaleType = PreviewView.ScaleType.FIT_CENTER
                }
                qrScannerUtil.setupCameraAnalysis(previewView) { qrCode ->
                    onResult(qrCode)
                }
                previewView
            },
            modifier = modifier
        )
    }
}

// Now, in any Composable where you have a button, you can use it like this:

//@Composable
//fun YourScreen() {
//    var showScanner by remember { mutableStateOf(false) }
//
//    // Your button here
//    Button(
//        onClick = {
//            // Request permission and show scanner
//            val permissionLauncher = rememberLauncherForActivityResult(
//                contract = ActivityResultContracts.RequestPermission()
//            ) { granted ->
//                if (granted) {
//                    showScanner = true
//                } else {
//                    // Handle permission denied
//                }
//            }
//            permissionLauncher.launch(Manifest.permission.CAMERA)
//        },
//        // ... other button properties
//    ) {
//        Text("Scan QR")
//    }
//
//    // Include the scanner composable
//    QRScannerComposable(
//        showScanner = showScanner,
//        onResult = { qrCode ->
//            // Handle the result
//            println("Scanned QR: $qrCode")
//            showScanner = false  // Close scanner after result
//        }
//    )
//}
