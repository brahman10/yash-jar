package com.jar.health_insurance.impl.ui.landing_page.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AutoScrollLazyLayout(list: List<String>, modifier: Modifier = Modifier,
                content: @Composable (String) -> Unit) {
    var itemsListState by remember { mutableStateOf(list) }
    val lazyListState = rememberLazyStaggeredGridState()
    val coroutineScope = rememberCoroutineScope()

    LazyHorizontalStaggeredGrid(
        state = lazyListState,
        rows = StaggeredGridCells.Fixed(2),
        modifier = Modifier.height(100.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalItemSpacing = 8.dp
    ) {
        items(itemsListState) {
            content(it)
            Spacer(modifier = Modifier.width(8.dp))

            if (it == itemsListState.last()) {
                val currentList = itemsListState

               // val secondPart = currentList.subList(0, lazyListState.firstVisibleItemIndex)
                val firstPart = currentList.subList(0, currentList.size)

                LaunchedEffect(key1 = Unit) {
                    coroutineScope.launch {
                        lazyListState.scrollToItem(
                            0,
                           currentList.size
                        )
                    }
                }

                itemsListState = firstPart //+ secondPart
            }
        }
    }
    LaunchedEffect(Unit) {
        autoScroll(lazyListState)
    }
}

@OptIn(ExperimentalFoundationApi::class)
private  suspend fun autoScroll(lazyListState: LazyStaggeredGridState) {
    lazyListState.scroll(MutatePriority.PreventUserInput) {
        scrollBy(SCROLL_DX)
    }
    delay(DELAY_BETWEEN_SCROLL_MS)

    autoScroll(lazyListState)
}

private const val DELAY_BETWEEN_SCROLL_MS = 4L
private const val SCROLL_DX = 2f



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StaggeredGrid(
    modifier: Modifier = Modifier,
    rows: Int = 3,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier.basicMarquee()
    ) { measurables, constraints ->
        val rowWidths = IntArray(rows) { 0 } // Keep track of the width of each row
        val rowHeights = IntArray(rows) { 0 } // Keep track of the height of each row

        // Don't constrain child views further, measure them with given constraints
        val placeables = measurables.mapIndexed { index, measurable ->
            val placeable = measurable.measure(constraints)

            // Track the width and max height of each row
            val row = index % rows
            rowWidths[row] += placeable.width
            rowHeights[row] = max(rowHeights[row], placeable.height)

            placeable
        }

        // Grid's width is the widest row
        val width = rowWidths.maxOrNull()?.coerceIn(constraints.minWidth, constraints.maxWidth)
            ?: constraints.minWidth
        // Grid's height is the sum of each row
        val height = rowHeights.sum().coerceIn(constraints.minHeight, constraints.maxHeight)

        // y co-ord of each row
        val rowY = IntArray(rows) { 0 }
        for (i in 1 until rows) {
            rowY[i] = rowY[i - 1] + rowHeights[i - 1]
        }
        layout(width, height) {
            // x co-ord we have placed up to, per row
            val rowX = IntArray(rows) { 0 }
            placeables.forEachIndexed { index, placeable ->
                val row = index % rows
                placeable.place(
                    x = rowX[row],
                    y = rowY[row]
                )
                rowX[row] += placeable.width
            }
        }
    }
}