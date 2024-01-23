package com.espressodev.gptmap.feature.screenshot_gallery

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.IconType
import com.espressodev.gptmap.core.designsystem.TextType
import com.espressodev.gptmap.core.designsystem.component.GmEditAlertDialog
import com.espressodev.gptmap.core.designsystem.component.GmTopAppBar
import com.espressodev.gptmap.core.designsystem.component.LottieAnimationPlaceholder
import com.espressodev.gptmap.core.designsystem.component.ShimmerImage
import com.espressodev.gptmap.core.designsystem.component.darkBottomOverlayBrush
import com.espressodev.gptmap.core.designsystem.theme.GptmapTheme
import com.espressodev.gptmap.core.model.ImageSummary
import com.espressodev.gptmap.core.model.Response
import kotlinx.collections.immutable.PersistentList
import java.time.LocalDateTime
import kotlin.math.absoluteValue
import com.espressodev.gptmap.core.designsystem.R.raw as AppRaw
import com.espressodev.gptmap.core.designsystem.R.string as AppText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenshotGalleryRoute(
    popUp: () -> Unit,
    viewModel: ScreenshotGalleryViewModel = hiltViewModel()
) {
    val imageAnalysesResponse by viewModel.imageAnalyses.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            GmTopAppBar(
                text = TextType.Res(AppText.screenshot_gallery),
                icon = IconType.Vector(GmIcons.ImageSearchDefault),
                onBackClick = popUp,
                editText = uiState.selectedImageSummary.title,
                isInEditMode = uiState.uiIsInEditMode,
                onEditClick = { viewModel.onEvent(ScreenshotGalleryUiEvent.OnEditClick) },
                onDeleteClick = { viewModel.onEvent(ScreenshotGalleryUiEvent.OnDeleteClick) },
                onCancelClick = { viewModel.onEvent(ScreenshotGalleryUiEvent.OnCancelClick) }
            )
        }
    ) {
        when (val result = imageAnalysesResponse) {
            is Response.Failure -> {
                LottieAnimationPlaceholder(AppRaw.confused_man_404)
            }

            Response.Loading -> {}
            is Response.Success -> {
                if (result.data.isNotEmpty()) {
                    ScreenshotGalleryScreen(
                        modifier = Modifier.padding(it),
                        images = result.data,
                        onLongClick = { imageSummary ->
                            viewModel.onEvent(
                                ScreenshotGalleryUiEvent.OnLongClickToImage(imageSummary)
                            )
                        },
                        selectedId = uiState.selectedImageSummary.id
                    )
                } else {
                    LottieAnimationPlaceholder(
                        modifier = Modifier.padding(it),
                        rawRes = AppRaw.nothing_here_anim
                    )
                }
            }
        }
    }

    BackHandler {
        if (uiState.uiIsInEditMode) {
            viewModel.onEvent(ScreenshotGalleryUiEvent.Reset)
        }
    }

    if (uiState.editDialogState) {
        GmEditAlertDialog(
            title = AppText.screenshot_gallery_edit_dialog_title,
            textFieldLabel = AppText.screenshot_gallery_edit_dialog_text_field_placeholder,
            onConfirm = { viewModel.onEvent(ScreenshotGalleryUiEvent.OnEditDialogConfirm(it)) },
            onDismiss = { viewModel.onEvent(ScreenshotGalleryUiEvent.OnEditDialogDismiss) }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScreenshotGalleryScreen(
    images: PersistentList<ImageSummary>,
    onLongClick: (ImageSummary) -> Unit,
    selectedId: String,
    modifier: Modifier = Modifier,
) {
    var currentPage by rememberSaveable { mutableIntStateOf(0) }
    var dialogState by rememberSaveable { mutableStateOf(false) }
    val pagerState = rememberPagerState(initialPage = currentPage, pageCount = { images.size })
    if (dialogState) {
        GalleryView(images = images, pagerState = pagerState, onDismiss = { dialogState = false })
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp),
        modifier = modifier
    ) {
        itemsIndexed(
            images,
            key = { _, imageSummary -> imageSummary.id }
        ) { index, imageSummary ->
            ImageCard(
                imageSummary = imageSummary,
                onClick = {
                    currentPage = index
                    dialogState = true
                },
                onLongClick = { onLongClick(imageSummary) },
                isSelected = selectedId == imageSummary.id
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageCard(
    imageSummary: ImageSummary,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {}
) {
    var isImageLoaded by remember { mutableStateOf(value = false) }
    val borderStroke = if (isSelected) 3.dp else 0.dp
    val elevation = if (isSelected) 8.dp else 0.dp

    Box(
        modifier = modifier
            .shadow(elevation, shape)
            .clip(shape)
            .border(borderStroke, MaterialTheme.colorScheme.primary, shape)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
    ) {
        ShimmerImage(
            imageSummary.imageUrl,
            modifier = Modifier.aspectRatio(1f),
            onSuccess = { isImageLoaded = true }
        )
        if (isImageLoaded)
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(brush = darkBottomOverlayBrush)
            )
        Text(
            text = imageSummary.title,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .basicMarquee(iterations = Int.MAX_VALUE),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            color = Color.White
        )

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun GalleryView(
    images: PersistentList<ImageSummary>,
    pagerState: PagerState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Log.d("GalleryView", "images: $images")
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .zIndex(2f), contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HorizontalPager(
                    state = pagerState,
                    contentPadding = PaddingValues(16.dp)
                ) { page ->
                    Card(
                        Modifier
                            .graphicsLayer {
                                val pageOffset =
                                    (pagerState.currentPage - page + pagerState.currentPageOffsetFraction)
                                        .absoluteValue

                                val scale = lerp(
                                    start = 0.7f,
                                    stop = 1f,
                                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                )
                                scaleX = scale
                                scaleY = scale

                                alpha = lerp(
                                    start = 0.5f,
                                    stop = 1f,
                                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                )
                            }
                            .aspectRatio(1f)
                    ) {
                        ImageCard(imageSummary = images[page])
                    }
                }
                DotsIndicator(
                    totalDots = images.size,
                    selectedIndex = pagerState.currentPage,
                    maxDots = minOf(images.size, 5)
                )
            }
        }
    }
}

@Composable
fun DotsIndicator(
    totalDots: Int,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    activeDotColor: Color = Color.White,
    inactiveDotColor: Color = Color.LightGray,
    spacing: Dp = 8.dp,
    selectedDotSize: Dp = 16.dp,
    maxDots: Int = 5
) {
    val listState = rememberLazyListState()
    val totalWidth: Dp = selectedDotSize * maxDots + spacing * (maxDots - 1)
    val widthInPx = with(LocalDensity.current) { selectedDotSize.toPx() }

    LaunchedEffect(key1 = selectedIndex) {
        val viewportSize = listState.layoutInfo.viewportSize
        listState.animateScrollToItem(
            selectedIndex,
            (widthInPx / 2 - viewportSize.width / 2).toInt()
        )
    }

    LazyRow(
        modifier = modifier.width(totalWidth),
        state = listState,
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically,
        userScrollEnabled = false
    ) {
        items(totalDots) { index ->
            val scale = animateFloatAsState(
                targetValue = when (index) {
                    selectedIndex -> 1f
                    selectedIndex - 1, selectedIndex + 1 -> 0.70f
                    else -> 0.4f
                },
                animationSpec = tween(
                    durationMillis = 500,
                    easing = FastOutSlowInEasing
                ),
                label = "Dot sizing animation"
            )
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scale.value
                        scaleY = scale.value
                    }
                    .size(selectedDotSize)
                    .clip(CircleShape)
                    .background(
                        color = if (index == selectedIndex) activeDotColor else inactiveDotColor
                    )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ScreenshotGalleryPreview() {
    GptmapTheme {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            ImageCard(
                imageSummary = ImageSummary(
                    id = "debet",
                    imageUrl = "https://www.google.com/#q=fames",
                    title = "himenaeos",
                    date = LocalDateTime.now()
                ),
                isSelected = true,
                onClick = {},
                onLongClick = {},
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}