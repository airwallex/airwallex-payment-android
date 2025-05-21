package com.airwallex.android.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch

typealias StandardBottomSheetDismissal = (onDismiss: () -> Unit) -> Unit

data class SheetDetails<ContentType : Any> @OptIn(ExperimentalMaterial3Api::class) constructor(
    val contentType: ContentType,
    val sheetState: SheetState,
    val onDismissRequested: StandardBottomSheetDismissal
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <ContentType : Any> StandardBottomSheet(
    sheetContent: @Composable (SheetDetails<ContentType>) -> Unit,
    modifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null,
    skipPartiallyExpanded: Boolean = false,
    content: @Composable (onPresentRequested: (ContentType) -> Unit) -> Unit
) {
    var contentTypeToPresent by remember { mutableStateOf<ContentType?>(null) }
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = skipPartiallyExpanded)

    fun dismiss(onCompletion: (() -> Unit)? = null) {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                contentTypeToPresent = null
                onCompletion?.invoke()
            }
        }
    }

    // There are quite a few ways to observe sheet states. We could also use the sheetState.targetValue
    // as the launch effect key. Or we could observe the isPresentingSheet state instead. Or we could
    // use the confirmStateChange on the rememberModalBottomSheetState method. But from my testing,
    // using snapshotFlow to observe the sheetState works best.
    LaunchedEffect(key1 = Unit) {
        snapshotFlow { sheetState.targetValue }.collect {
            if (it == SheetValue.Hidden) {
                onDismiss?.invoke()
            }
        }
    }

    Box {
        Box(modifier = Modifier.zIndex(1f)) {
            if (contentTypeToPresent != null) {
                ModalBottomSheet(
                    onDismissRequest = {
                        contentTypeToPresent = null
                    },
                    modifier = modifier,
                    sheetState = sheetState,
                    shape = MaterialTheme.shapes.small.copy(
                        bottomStart = CornerSize(0.dp),
                        bottomEnd = CornerSize(0.dp)
                    ), // 8dp corner radius, matching View based
                    dragHandle = null,
                    content = {
                        contentTypeToPresent?.also {
                            sheetContent(
                                SheetDetails(
                                    contentType = it,
                                    sheetState = sheetState,
                                    onDismissRequested = ::dismiss
                                )
                            )
                        }
                    },
                )
            }
        }

        Box(modifier = Modifier.zIndex(2f)) {
            content {
                contentTypeToPresent = it
            }
        }
    }
}