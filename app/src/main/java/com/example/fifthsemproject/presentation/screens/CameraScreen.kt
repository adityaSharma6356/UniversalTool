package com.example.fifthsemproject.presentation.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Paint
import android.util.Log
import android.util.Size
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.compose.BackHandler
import androidx.camera.core.CameraEffect
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fifthsemproject.R
import com.example.fifthsemproject.presentation.viewmodels.CameraViewModel
import kotlinx.coroutines.launch


@Composable
fun CameraScreen(
    viewModel: CameraViewModel = viewModel()
) {
    if(viewModel.showImage){
        viewModel.state.capturedImage?.let { capturedImage: Bitmap ->
            CapturedImageBitmapDialog(
                capturedImage = capturedImage,
                onDismissRequest = viewModel::onCapturedPhotoConsumed,
                viewModel
            )
            viewModel.state = viewModel.state.copy(capturingInProgress = false)
            Log.d("camLog", "done")
        }
    } else {
        CameraContent(
            viewModel = viewModel,
            onPhotoCaptured = viewModel::onPhotoCaptured
        )
    }
}

@Composable
private fun CapturedImageBitmapDialog(
    capturedImage: Bitmap,
    onDismissRequest: () -> Unit,
    viewModel: CameraViewModel
) {
    BackHandler(viewModel.showImage) {
        onDismissRequest()
    }
    val capturedImageBitmap: ImageBitmap = remember { capturedImage.asImageBitmap() }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Box {
            val wd = LocalConfiguration.current.screenWidthDp
            val ht = LocalConfiguration.current.screenHeightDp
            val density = LocalDensity.current.density
            Image(
                bitmap = capturedImageBitmap,
                contentDescription = "Captured photo"
            )
            SelectionContainer {
                Canvas(modifier = Modifier){
                    drawIntoCanvas { canvas ->
                        viewModel.visionTextOutput?.let { text ->
                            val paint = Paint().apply {
                                color = Color.Black.toArgb()
                                textSize = 12 * density
                            }
                            val backgroundPaint = Paint().apply {
                                color = Color(255, 255, 255, 174).toArgb()
                            }

                            for (block in text.textBlocks) {
                                for(element in block.lines){
                                    val boundingBox = element.boundingBox
                                    if (boundingBox != null) {

                                        canvas.nativeCanvas.save()
                                        canvas.nativeCanvas.rotate(element.angle, boundingBox.centerX().toFloat(), boundingBox.centerY().toFloat())

                                        canvas.nativeCanvas.drawRect(
                                            boundingBox.left.toFloat()-8,
                                            boundingBox.top.toFloat()-8,
                                            boundingBox.right.toFloat()+8,
                                            boundingBox.bottom.toFloat()+8,
                                            backgroundPaint
                                        )
                                        canvas.nativeCanvas.drawText(
                                            element.text,
                                            boundingBox.left.toFloat(),
                                            boundingBox.top.toFloat() + paint.textSize,
                                            paint
                                        )

                                        canvas.nativeCanvas.restore()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Log.d("camLog", viewModel.state.imageText)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
private fun CameraContent(
    viewModel: CameraViewModel,
    onPhotoCaptured: (Bitmap) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraController = remember { LifecycleCameraController(context) }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val config = LocalConfiguration.current
        val screenWidth = config.screenWidthDp
        val viewHeight = screenWidth/3*4
        Card(
            shape = RectangleShape,
            modifier = Modifier
                .height(viewHeight.dp)
                .width((screenWidth + 1).dp)) {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize(),
                factory = { context ->
                    PreviewView(context).apply {
                        layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                        setBackgroundColor(android.graphics.Color.BLACK)
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                        scaleType = PreviewView.ScaleType.FILL_START
                    }.also { previewView ->
                        cameraController.imageCaptureTargetSize = CameraController.OutputSize(Size(720, 1280))
                        cameraController.imageAnalysisBackpressureStrategy = ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
                        previewView.controller = cameraController
                        cameraController.bindToLifecycle(lifecycleOwner)
                    }
                }
            )
        }
        if(viewModel.state.capturingInProgress){
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        Row(modifier = Modifier.fillMaxWidth().height(100.dp), verticalAlignment = Alignment.CenterVertically ) {
            IconButton(onClick = { viewModel.switchFlash(cameraController, !viewModel.flashON) }) {
                Icon(painter = painterResource(id = R.drawable.flash_icon), contentDescription = "flash", tint = if(viewModel.flashON) Color.Yellow else Color.White)
            }
        }
        Spacer(modifier = Modifier.weight(1f))

        Button(
            colors = ButtonDefaults.buttonColors(containerColor = Color(255, 255, 255, 255)),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .padding(bottom = 50.dp)
                .size(70.dp),
            onClick = {
                viewModel.state = viewModel.state.copy(capturingInProgress = true)
                viewModel.captureAndProcess(
                    context = context,
                    cameraController = cameraController
                )
            }) {}
    }
}