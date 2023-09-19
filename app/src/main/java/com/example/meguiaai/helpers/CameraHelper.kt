package com.example.meguiaai.helpers

import android.content.Context
import android.graphics.Bitmap
import android.util.Size
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraProvider
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner

class CameraHelper(
    private var context: Context,
    private var previewView: PreviewView
) {
    private var bitmapImage: Bitmap? = null
    private lateinit var cameraProvider: ProcessCameraProvider

    fun bindCameraUseCase() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            cameraProvider.bindToLifecycle(getLifecycleOwner(), getCameraSelector(), buildPreview(), buildImageAnalysis())
        }, ContextCompat.getMainExecutor(context))
    }

    private fun getCameraSelector(): CameraSelector {
        return CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
    }

    private fun getCameraProvider(): CameraProvider {
        return cameraProvider
    }

    private fun getLifecycleOwner(): LifecycleOwner {
        return context as LifecycleOwner
    }

    private fun buildPreview(): Preview {
        val preview = Preview.Builder().build()
        preview.setSurfaceProvider(previewView.surfaceProvider)

        return preview
    }

    private fun buildImageAnalysis(): ImageAnalysis {
        val imageAnalysis = ImageAnalysis.Builder()
            .setOutputImageFormat(OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
            .setTargetResolution(Size(240, 320))
            .build()

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
            if (bitmapImage == null) {
                bitmapImage = Bitmap.createBitmap(
                    imageProxy.width,
                    imageProxy.height,
                    Bitmap.Config.ARGB_8888
                )
            }
            imageProxy.close()
        }

        return imageAnalysis
    }
}