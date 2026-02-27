package com.barcodebite.android.ui.screen.scanner

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.atomic.AtomicBoolean

class BarcodeAnalyzer(
    private val onBarcodeDetected: (String) -> Unit,
) : ImageAnalysis.Analyzer {
    private val scanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_EAN_13,
                Barcode.FORMAT_EAN_8,
                Barcode.FORMAT_UPC_A,
                Barcode.FORMAT_UPC_E,
                Barcode.FORMAT_CODE_128,
                Barcode.FORMAT_CODE_39,
                Barcode.FORMAT_QR_CODE,
            )
            .build(),
    )

    private val isProcessing = AtomicBoolean(false)
    private val emitted = AtomicBoolean(false)
    private val closed = AtomicBoolean(false)

    override fun analyze(imageProxy: ImageProxy) {
        if (closed.get() || emitted.get()) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        if (!isProcessing.compareAndSet(false, true)) {
            imageProxy.close()
            return
        }

        val input = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        scanner
            .process(input)
            .addOnSuccessListener { barcodes ->
                if (emitted.get()) {
                    return@addOnSuccessListener
                }

                val rawValue = barcodes
                    .firstNotNullOfOrNull { it.rawValue?.trim()?.takeIf(String::isNotBlank) }

                if (rawValue != null && emitted.compareAndSet(false, true)) {
                    onBarcodeDetected(rawValue)
                }
            }
            .addOnCompleteListener {
                isProcessing.set(false)
                imageProxy.close()
            }
    }

    fun close() {
        if (closed.compareAndSet(false, true)) {
            scanner.close()
        }
    }
}
