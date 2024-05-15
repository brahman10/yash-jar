@file:OptIn(ExperimentalGlideComposeApi::class)
package com.jar.gold_redemption.impl.ui.cart_complete_payment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.jar.app.core_compose_ui.component.JarPrimaryButton
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.views.LabelAndValueCompose
import com.jar.app.core_compose_ui.views.LabelValueComposeView
import com.jar.app.core_compose_ui.views.SingleExpandableCard
import com.jar.app.core_compose_ui.views.payments.PaymentTimelineView
import com.jar.app.core_compose_ui.views.payments.TimelineViewData

@Composable
fun RenderOrderDetails(title: Int = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_order_details, list: List<LabelAndValueCompose>, isExpanded: MutableState<Boolean>) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 16.dp, end = 16.dp, top = 0.dp),
        shape = RoundedCornerShape(12.dp),
    backgroundColor = colorResource(id = com.jar.app.core_ui.R.color.color_2E2942),) {
        SingleExpandableCard(isExpanded, {
            Text(
                text = stringResource(id = title),
                style = JarTypography.body1,
                modifier = Modifier.padding(16.dp),
                color = colorResource(id = com.jar.app.core_ui.R.color.color_ACA1D3)
            )
        }) {
            LabelValueComposeView(modifier = Modifier.padding(horizontal = 16.dp), list = list)
        }
    }
}
@Composable
internal fun RenderTransactionStatus(
    modifier: Modifier,
    list: List<TimelineViewData>,
    horizontalMargin: Dp = 12.dp,
    backgroundColor: Color = colorResource(id = com.jar.app.core_ui.R.color.color_3C3357),
    cardBackgroundColor: Color = colorResource(id = com.jar.app.core_ui.R.color.color_3C3357),
    statusBottomText: String?,
    retryButtonPressed: (() -> Unit)? = null,
    refreshButtonPressed: (() -> Unit)? = null,
) {
    Card(
        modifier = modifier
            .padding(horizontal = horizontalMargin)
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        shape = RoundedCornerShape(16.dp),
        backgroundColor = cardBackgroundColor
    ) {
        Column(modifier.padding(top = 12.dp, start = 16.dp, end = 16.dp)) {
            Text(
                stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_transaction_status),
                modifier = Modifier.padding(bottom = 20.dp),
                color = colorResource(
                    id = com.jar.app.core_ui.R.color.color_ACA1D3
                )
            )

            PaymentTimelineView(
                Modifier,
                timelineViewDataList = list,
                bottomText = statusBottomText.orEmpty(),
                renderCustomViewsInRightContent = {
                    if (list[it].isRefreshButtonShown == true) {
                        RenderRefreshButton(refreshButtonPressed)
                    }
                },
                retryButtonPressed = retryButtonPressed,
            )

        }
    }
}

