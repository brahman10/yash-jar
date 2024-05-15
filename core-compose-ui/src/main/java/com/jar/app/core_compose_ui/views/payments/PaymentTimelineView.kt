package com.jar.app.core_compose_ui.views.payments

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jar.app.core_compose_ui.views.RenderTimelineView


/**
 * A composable that renders card showing payments transaction statuses in form of vertical timeline
 * with three possible states (TransactionStatus.SUCCESS,TransactionStatus.PENDING,TransactionStatus.FAILED)
 * @param timelineViewDataList contains list of payment status data
 * @param bottomText description shown at bottom of timeline with divider
 * @param retryButtonPressed optional action to be executed when retry button is clicked
 * @param renderCustomViewsInRightContent optional custom view to be rendered
 */
@Composable
fun PaymentTimelineView(
    modifier: Modifier,
    timelineViewDataList: List<TimelineViewData>,
    bottomText: String?,
    retryButtonPressed: (() -> Unit)?,
    shouldShowDividerAtLast: (() -> Boolean)? = null,
    renderCustomViewsInRightContent: (@Composable (Int) -> Unit)? = null
) {
    RenderTimelineView(
        modifier, timelineViewDataList.size,
        {
            RenderLeftIconContent(
                timelineViewDataList[it],
                isStrokeShown(timelineViewDataList[it].status)
            )
        },
        {
            Column(Modifier.fillMaxWidth()) {
                val timelineViewData = timelineViewDataList[it]
                RenderRightContent(timelineViewData)
                RenderRetryRow(
                    refreshText = timelineViewData.refreshText,
                    shouldShowRetryButton = timelineViewData.isRetryButtonEnabled
                        ?: shouldShowRetryRow(timelineViewData.status),
                    shouldShowDivider = shouldShowRefreshTextDivider(
                        refreshText = timelineViewData.refreshText,
                        isLastIndex = it == timelineViewDataList.lastIndex,
                        shouldShowDividerAtLast = shouldShowDividerAtLast
                    ),
                    retryButtonPressed = retryButtonPressed,
                    refreshTextTypography = timelineViewData.refreshTextTypography,
                    refreshTextMaxLines = timelineViewData.refreshTextMaxLines
                )
                renderCustomViewsInRightContent?.invoke(it)

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                )
            }
        },
        {
            colorForDivider(
                timelineViewDataList[it].status,
                timelineViewDataList.getOrNull(it + 1)?.status
            )
        },
        bottomText = bottomText
    )
}

@Composable
@Preview
fun PaymentTimelineViewPreview() {
    val list: List<TimelineViewData> = listOf(
        TimelineViewData(TransactionStatus.SUCCESS, "Bonus initiated"),
        TimelineViewData(
            TransactionStatus.PENDING,
            "Bonus initiated",
            refreshText = "Hello testing",
            date = "02 Mar ’23, 5:15pm"
        ),
        TimelineViewData(
            TransactionStatus.FAILED,
            "Bonus initiated",
            refreshText = "Hello testing",
            date = "02 Mar ’23, 5:15pm"
        ),
    )
    PaymentTimelineView(Modifier, list, "", {})
}
