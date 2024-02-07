package com.espressodev.gptmap.feature.snapTo_script

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.espressodev.gptmap.core.designsystem.GmIcons
import com.espressodev.gptmap.core.designsystem.R.drawable.ai_icon
import com.espressodev.gptmap.core.designsystem.component.LetterInCircleNotAnimated
import com.espressodev.gptmap.core.designsystem.theme.GptmapTheme
import com.espressodev.gptmap.core.designsystem.util.rememberKeyboardAsState
import com.espressodev.gptmap.core.model.AiResponseStatus
import com.espressodev.gptmap.core.model.ImageMessage
import com.espressodev.gptmap.feature.screenshot_gallery.InputSelector
import com.espressodev.gptmap.feature.screenshot_gallery.SnapToScriptUiEvent
import com.espressodev.gptmap.feature.screenshot_gallery.SnapToScriptUiState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import com.espressodev.gptmap.core.designsystem.R.string as AppText

@Composable
fun SnapToScriptRoute(
    popUp: () -> Unit,
    viewModel: SnapToScriptViewModel = hiltViewModel()
) {
    val uiState by viewModel.snapToScriptUiState.collectAsStateWithLifecycle()
    val messages by viewModel.messages.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        SnapToScriptScreen(
            uiState = uiState,
            messages = messages,
            onEvent = viewModel::onEvent,
        )
    }


    BackHandler {
        viewModel.onEvent(SnapToScriptUiEvent.OnReset)
        popUp()
    }
}

@Composable
fun SnapToScriptScreen(
    uiState: SnapToScriptUiState,
    messages: ImmutableList<ImageMessage>,
    onEvent: (SnapToScriptUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardState by rememberKeyboardAsState()
    val (keyboardHeight, setKeyboardHeight) = remember { mutableStateOf(300.dp) }
    val scrollState = rememberLazyListState()

    LaunchedEffect(key1 = keyboardState) {
        if (keyboardState.first) {
            setKeyboardHeight(keyboardState.second)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Messages(
            scrollState = scrollState,
            messages = messages,
            userFirstChar = uiState.userFirstChar,
            imageUrl = uiState.imageUrl,
            aiResponseStatus = uiState.aiResponseStatus,
            onTypingEnd = {},
        )

        ChatTextSection(
            value = uiState.value,
            keyboardHeight = keyboardHeight,
            inputSelector = uiState.inputSelector,
            onValueChange = { onEvent(SnapToScriptUiEvent.OnValueChanged(it)) },
            onSendClick = { onEvent(SnapToScriptUiEvent.OnSendClick) },
            onMicClick = { onEvent(SnapToScriptUiEvent.OnMicClick) },
            onMicOffClick = { onEvent(SnapToScriptUiEvent.OnMicOffClick) },
            onKeyboardClick = { onEvent(SnapToScriptUiEvent.OnKeyboardClick) },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .imePadding()
                .statusBarsPadding()
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Messages(
    scrollState: LazyListState,
    messages: ImmutableList<ImageMessage>,
    userFirstChar: Char,
    imageUrl: String,
    aiResponseStatus: AiResponseStatus,
    onTypingEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Log.d("Messages", "Messages:")
    val scope = rememberCoroutineScope()
    Box(modifier = modifier.padding(8.dp)) {
        LazyColumn(reverseLayout = true, state = scrollState, modifier = Modifier.fillMaxSize()) {
            item { Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.ime)) }

            item { Spacer(modifier = Modifier.height(56.dp)) }

            itemsIndexed(
                items = messages,
                key = { _, message -> message.id }
            ) { index, imageMessage ->
                Spacer(modifier = Modifier.padding(8.dp))

                BotMessageSection(
                    message = imageMessage.response,
                    isLastItem = index == 0,
                    aiResponseStatus = aiResponseStatus,
                    onTypingEnd = onTypingEnd
                )

                Spacer(modifier = Modifier.padding(8.dp))
                UserMessageSection(message = imageMessage.request, firstChar = userFirstChar)
            }

            stickyHeader {
                ImageSection(imageUrl = imageUrl)
            }
        }

        val jumpThreshold = with(LocalDensity.current) {
            56.dp.toPx()
        }

        val jumpToBottomButtonEnabled by remember {
            derivedStateOf {
                scrollState.firstVisibleItemIndex > 1 ||
                        scrollState.firstVisibleItemScrollOffset > jumpThreshold
            }
        }

        JumpToBottom(
            enabled = jumpToBottomButtonEnabled,
            onClick = {
                scope.launch {
                    scrollState.animateScrollToItem(0)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .imePadding()
        )
    }
}

@Composable
fun JumpToBottom(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = enabled,
        modifier = modifier.offset(y = (-72).dp),
        enter = fadeIn(animationSpec = tween(400)),
        exit = fadeOut(animationSpec = tween(400))
    ) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 4.dp
            )
        ) {
            Icon(imageVector = GmIcons.DoubleDownDefault, contentDescription = null)
        }
    }
}

@Composable
fun UserMessageSection(message: String, firstChar: Char) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        LetterInCircleNotAnimated(
            size = 20.dp,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Text(text = firstChar.toString(), style = MaterialTheme.typography.labelMedium)
        }
        Column {
            Text(
                text = stringResource(id = AppText.you),
                style = MaterialTheme.typography.bodyMedium,
                letterSpacing = 0.8.sp
            )
            Text(text = message, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun BotMessageSection(
    message: String,
    isLastItem: Boolean,
    aiResponseStatus: AiResponseStatus = AiResponseStatus.Idle,
    onTypingEnd: () -> Unit = {}
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        LetterInCircleNotAnimated {
            Image(
                painter = painterResource(id = ai_icon),
                contentDescription = null,
                modifier = Modifier.size(17.dp)
            )
        }
        Column {
            Text(
                text = stringResource(id = AppText.gemini),
                style = MaterialTheme.typography.bodyMedium,
                letterSpacing = 0.8.sp
            )
            when {
                aiResponseStatus == AiResponseStatus.Success && isLastItem -> {
                    TypeWriter(message = message, onTypingEnd = onTypingEnd)
                }

                aiResponseStatus == AiResponseStatus.Loading && isLastItem -> {
                    Text(text = "Typing...", style = MaterialTheme.typography.bodyLarge)
                }

                else -> {
                    Text(text = message, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
fun TypeWriter(
    message: String,
    typeDelayMillis: Long = 10L,
    onTypingEnd: () -> Unit
) {
    var textToDisplay by remember { mutableStateOf("") }
    var currentIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(key1 = Unit) {
        while (currentIndex < message.length) {
            textToDisplay += message[currentIndex]
            delay(typeDelayMillis)
            currentIndex++
        }
        onTypingEnd()
    }

    Text(
        text = textToDisplay,
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
fun ImageSection(imageUrl: String, modifier: Modifier = Modifier) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Surface(
            modifier = modifier.size(175.dp),
            shape = RoundedCornerShape(8.dp),
            shadowElevation = 2.dp
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = stringResource(id = AppText.selected_image),
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ChatTextSection(
    value: String,
    keyboardHeight: Dp,
    inputSelector: InputSelector,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onMicClick: () -> Unit,
    onMicOffClick: () -> Unit,
    onKeyboardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isTextFieldEmpty by remember(value) {
        derivedStateOf { value.isBlank() }
    }
    val keyboard = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    LaunchedEffect(key1 = inputSelector) {
        if (inputSelector == InputSelector.Keyboard) {
            focusRequester.requestFocus()
        }
    }

//    if (inputSelector != InputSelector.None) {
//        BackHandler(onBack = )
//    }

    val permission = rememberPermissionState(permission = android.Manifest.permission.RECORD_AUDIO)
    val (shouldShowDialog, setShouldShowDialog) = remember { mutableStateOf(value = false) }

    LaunchedEffect(key1 = shouldShowDialog) {
        if (shouldShowDialog and !permission.status.isGranted) {
            permission.launchPermissionRequest()
        }
        setShouldShowDialog(false)
    }

    val (textFieldValue) = remember(value) {
        mutableStateOf(
            TextFieldValue(
                value,
                TextRange(value.length)
            )
        )
    }

    Surface(modifier = modifier) {
        Column {
            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = textFieldValue,
                    onValueChange = {
                        onValueChange(it.text)
                    },
                    shape = RoundedCornerShape(24.dp),
                    placeholder = { Text(text = stringResource(AppText.message)) },
                    trailingIcon = {
                        when {
                            isTextFieldEmpty -> when (inputSelector) {
                                InputSelector.None, InputSelector.Keyboard -> {
                                    IconButton(
                                        onClick = {
                                            scope.launch {
                                                if (!permission.status.isGranted) {
                                                    setShouldShowDialog(true)
                                                } else {
                                                    focusManager.clearFocus()
                                                    keyboard?.hide()
                                                    delay(50L)
                                                    onMicClick()
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = GmIcons.MicOutlined,
                                            contentDescription = stringResource(id = AppText.mic)
                                        )
                                    }
                                }

                                else -> {}
                            }

                            else -> IconButton(onClick = { onValueChange("") }) {
                                Icon(
                                    imageVector = GmIcons.ClearDefault,
                                    contentDescription = stringResource(id = AppText.clear)
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .focusRequester(focusRequester)
                        .onFocusEvent { state ->
                            if (state.isFocused) {
                                onKeyboardClick()
                            }
                        }
                )
                FilledTonalIconButton(
                    onClick = {
                        keyboard?.hide()
                        onSendClick()
                    },
                    modifier = Modifier.padding(start = 4.dp),
                    enabled = !isTextFieldEmpty
                ) {
                    Icon(
                        imageVector = GmIcons.ArrowUpwardDefault,
                        contentDescription = stringResource(id = AppText.send)
                    )
                }
            }
            AnimatedVisibility(inputSelector == InputSelector.MicBox) {
                MicBox(
                    keyboardHeight = keyboardHeight,
                    onClick = onMicOffClick
                )
            }
        }
    }
}

@Composable
private fun MicBox(keyboardHeight: Dp, onClick: () -> Unit) {
    var duration by remember { mutableStateOf(Duration.ZERO) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            duration += 1.seconds
        }
    }
    Surface(
        modifier = Modifier
            .height(keyboardHeight)
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.secondaryContainer,
        onClick = onClick
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "volume effect")
        val animation = infiniteTransition.animateFloat(
            initialValue = 0.5f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(3000),
                repeatMode = RepeatMode.Reverse
            ),
            label = "animation volume"
        )
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .scale(animation.value)
                    .size(100.dp)
                    .align(Alignment.Center)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f))
            )
            Text(
                duration.toComponents { minutes, seconds, _ ->
                    val min = minutes.toString().padStart(2, '0')
                    val sec = seconds.toString().padStart(2, '0')
                    "$min:$sec"
                },
                Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp),
                style = MaterialTheme.typography.titleMedium
            )
            Row(
                modifier = Modifier.align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(imageVector = GmIcons.RecordDefault, contentDescription = null)
                Text(text = stringResource(id = AppText.tap_to_stop_record))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SnapToScriptScreen() {
    GptmapTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            JumpToBottom(
                enabled = true,
                onClick = {},
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}