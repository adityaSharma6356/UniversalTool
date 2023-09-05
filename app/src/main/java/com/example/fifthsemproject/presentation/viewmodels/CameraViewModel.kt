package com.example.fifthsemproject.presentation.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fifthsemproject.presentation.screens.image_to_pdf.rotateBitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions
import kotlinx.coroutines.launch

data class CameraState(
    val capturedImage: Bitmap? = null,
    var capturingInProgress : Boolean = false,
    var imageText : String = ""
)

class CameraViewModel : ViewModel(), ImageAnalysis.Analyzer {

    var state by mutableStateOf(CameraState())
    var showImage by mutableStateOf(false)
    var visionTextOutput: Text? = null
    var flashON by mutableStateOf(false)

    fun switchFlash(cameraController: LifecycleCameraController, switch: Boolean){
        cameraController.imageCaptureFlashMode = if(switch) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF
        cameraController.enableTorch(switch)
        flashON = !flashON
    }

    fun captureAndProcess(context: Context, cameraController: LifecycleCameraController){
        viewModelScope.launch {
            Log.d("camLog", "capturing")
            val mainExecutor = ContextCompat.getMainExecutor(context)

            cameraController.takePicture(mainExecutor, object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    analyze(image)
                    val correctedBitmap: Bitmap = image
                        .toBitmap()
                        .rotateBitmap(image.imageInfo.rotationDegrees)
                    onPhotoCaptured(correctedBitmap)
                    image.close()
                }
                override fun onError(exception: ImageCaptureException) {
                    Log.e("CameraContent", "Error capturing image", exception)
                }
            })
        }
    }

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    override fun  analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            // Pass image to an ML Kit Vision API
            // ...
            val recognizer = TextRecognition.getClient(DevanagariTextRecognizerOptions.Builder().build())
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    visionTextOutput = visionText
                    state = state.copy(imageText = visionText.text)
                    showImage = true
                    // Task completed successfully
                    // ...
                }
                .addOnFailureListener { e ->
                    Log.e("camLog", e.message.toString())
                    Log.e("camLog", "error")
                    // Task failed with an exception
                    // ...
                }
        }
    }



    fun onPhotoCaptured(bitmap: Bitmap) {
        // TODO: Process your photo, for example store it in the MediaStore
        // here we only do a dummy showcase implementation
        updateCapturedPhotoState(bitmap)
    }

    fun onCapturedPhotoConsumed() {
        showImage = false
        updateCapturedPhotoState(null)
    }

    private fun updateCapturedPhotoState(updatedPhoto: Bitmap?) {
        state.capturedImage?.recycle()
        state = state.copy(capturedImage = updatedPhoto)
    }

    override fun onCleared() {
        state.capturedImage?.recycle()
        super.onCleared()
    }

}