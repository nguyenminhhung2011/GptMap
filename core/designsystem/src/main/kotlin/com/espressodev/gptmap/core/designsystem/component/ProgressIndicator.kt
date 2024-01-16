package com.espressodev.gptmap.core.designsystem.component

import androidx.annotation.RawRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.espressodev.gptmap.core.designsystem.R.raw as AppRaw

@Composable
fun GmCircularIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .zIndex(2f)
            .background(Color(0x80000000)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Color.White)
    }
}

@Composable
fun LottieAnimationView(
    @RawRes rawRes: Int,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.background
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(rawRes))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .zIndex(2f),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress }
        )
    }
}

@Preview
@Composable
fun LottieAnimationParentPreview() {
    LottieAnimationView(AppRaw.earth_orbit)
}
