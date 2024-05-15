package com.jar.app.core_compose_ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jar.app.core_compose_ui.theme.JarTypography
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.lang.Math.abs

val headers = listOf<String>(
    "A",
    "B",
    "C",
    "D",
    "E",
    "F",
    "G",
    "H",
    "I",
    "J",
    "K",
    "L",
    "M",
    "N",
    "O",
    "P",
    "Q",
    "R",
    "S",
    "T",
    "U",
    "V",
    "W",
    "X",
    "Y",
    "Z",
)

fun updateSelectedIndexIfNeeded(
    offset: Float,
    scope: CoroutineScope,
    offsets: SnapshotStateMap<Int, Float>,
    listState: LazyListState,
    selectedHeaderIndex: MutableState<Int>,
    findIndex: (String) -> Int
) {
    val index = offsets
        .mapValues { abs(it.value - offset) }
        .entries
        .minByOrNull { it.value }
        ?.key ?: return
    if (selectedHeaderIndex.value == index) return
    selectedHeaderIndex.value = index
    val selectedItemIndex =
        findIndex(headers[index])
    if (selectedItemIndex == -1) {
        return
    }
    scope.launch {
        listState.scrollToItem(selectedItemIndex)
    }
}

@Composable
fun AlphabeticalHeaderScroller(
    offsets: SnapshotStateMap<Int, Float>,
    function: (Float) -> Unit
) {
    Column(
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxHeight()
            .pointerInput(Unit) {
                detectTapGestures {
                    function(it.y)
                }
            }
            .pointerInput(Unit) {
                detectVerticalDragGestures { change, _ ->
                    function(change.position.y)
                }
            }
    ) {
        headers.forEachIndexed { i, header ->
            Text(
                header,
                color = colorResource(id = com.jar.app.core_ui.R.color.color_ACA1D3),
                modifier = Modifier.padding(start = 2.dp, end = 2.dp, top = 0.dp, bottom = 0.dp).onGloballyPositioned {
                    offsets[i] = it.boundsInParent().center.y
                },
                style = JarTypography.body1.copy(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                fontSize = 12.sp,
                letterSpacing = 0.sp,
                lineHeight = 0.sp
            )
        }
    }
}


@Composable
@Preview
fun AlphabeticalScrollerPreview() {
    val scope = rememberCoroutineScope()
    val items = remember { LoremIpsum().values.map { it.uppercase() }.first().split(" ").toSet().toList().sortedBy { it.uppercase() } }
    Row(Modifier.height(400.dp)) {
        val listState = rememberLazyListState()
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f)
        ) {
            items(items) {
                Text(it)
            }
        }
        val offsets = remember { mutableStateMapOf<Int, Float>() }
        val selectedHeaderIndex = remember { mutableStateOf(0) }

        AlphabeticalHeaderScroller(
            offsets,
        ) {
            updateSelectedIndexIfNeeded(
                it,
                scope,
                offsets,
                listState,
                selectedHeaderIndex
            ) { items.indexOfFirst { item -> item.first().uppercase() == it } }
        }
    }
}




