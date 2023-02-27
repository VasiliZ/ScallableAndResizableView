package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import coil.compose.AsyncImage
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ScalableImage()
                }
            }
        }
    }
}

@Composable
fun ScalableImage() {
    val scale = remember {
        mutableStateOf(1f)
    }

    val offset = remember {
        mutableStateOf(Offset.Zero)
    }
    val density = LocalConfiguration.current
    val viewHeight = remember { mutableStateOf(0) }
    val viewWidth = remember { mutableStateOf(0) }

    Box(modifier = Modifier
        .fillMaxSize()
        .navigationBarsPadding()
        .statusBarsPadding()
        .clip(RectangleShape)
        .pointerInput(Unit) {
            detectTransformGestures { _, pan, zoom, _ ->
                scale.value *= zoom
                offset.value += pan
            }
        }) {
        AsyncImage(
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()

                .onGloballyPositioned {
                    viewHeight.value = it.size.height
                    viewWidth.value = it.size.width
                }
                .graphicsLayer {
                    scaleX = maxOf(1f, minOf(3f, scale.value))
                    scaleY = maxOf(1f, minOf(3f, scale.value))

                    density.apply {
                        if (scale.value > 1f) {
                            translationY =
                                getMaxTranslationValue(offset.value.y, viewHeight.value, scaleY)
                            translationX =
                                getMaxTranslationValue(offset.value.x, viewWidth.value, scaleX)
                        } else {
                            offset.value = Offset.Zero
                        }
                    }
                },
            model = "https://www.scusd.edu/sites/main/files/imagecache/tile/main-images/camera_lense_0.jpeg",
            contentDescription = null
        )
    }
}

private fun getMaxTranslationValue(offset: Float, size: Int, scaleX: Float): Float {
    return if (offset < 0) {
        maxOf(
            offset,
            ((-size / 2 * scaleX) + size / 2)
        )
    } else {
        minOf(
            offset,
            ((size / 2 * scaleX) - size / 2)
        )
    }
}