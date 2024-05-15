package com.jar.refer_earn_v2.impl.ui.refer_earn_intro.referral_bottomsheet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.jar.app.core_compose_ui.utils.sdp
import androidx.paging.compose.LazyPagingItems
import com.jar.app.core_compose_ui.component.RenderImagePillButton
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_ui.R
import com.jar.app.feature_refer_earn_v2.shared.domain.model.Referral
import com.jar.app.feature_refer_earn_v2.shared.util.ReferEarnV2Constants.Analytics.Contact_Support
import com.jar.app.feature_refer_earn_v2.shared.util.ReferEarnV2Constants.Analytics.Cross_clicked
import com.jar.app.core_compose_ui.utils.ssp

@Composable
fun RenderBottomSheet(
    items: LazyPagingItems<Referral>,
    onItemClick: (String) -> Unit,
    count: Int,
    postAnalyticsOnButtonClick: (String) -> Unit,
    closeBottomSheetFxn: () -> Unit,
    RightSectionClick: () -> Unit
) {
    val listHeaderText =
        pluralStringResource(
            id = com.jar.app.feature_refer_earn_v2.shared.R.plurals.feature_refer_earn_bottomsheet_header,
            count = count,
            count
        )

    Column(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f)
            .padding(top = 16.sdp, bottom = 8.sdp)) {
        Icon(
            painter = painterResource(id = R.drawable.core_ui_ic_close),
            contentDescription = "",
            tint = Color.White,
            modifier = Modifier
                .debounceClickable {
                    postAnalyticsOnButtonClick(Cross_clicked)
                    closeBottomSheetFxn()
                }
                .align(Alignment.End)
                .padding(end = 16.sdp)
        )
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(8.sdp))
        Row (Modifier, verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(16.sdp))
            Text(text = listHeaderText, style = JarTypography.dynamic.h4, color = colorResource(id = R.color.white))
            Spacer(modifier = Modifier.weight(1f))
            RenderImagePillButton(
                text = stringResource(com.jar.app.feature_refer_earn_v2.shared.R.string.feature_refer_contact_support),
                modifier = Modifier
                    .debounceClickable {
                        postAnalyticsOnButtonClick(Contact_Support)
                        RightSectionClick()
                     },
                drawableRes = R.drawable.ic_whatsapp_help_support_20dp,
                bgColor = R.color.color_3E3953,
                textColor = R.color.color_D5CDF2,
                cornerRadius = 8.sdp,
                maxLines = 1,
                style = JarTypography.dynamic.bodyRegular.copy(fontSize = 12.ssp),
                iconSize = 18.sdp
            )
            Spacer(modifier = Modifier.width(16.sdp))
        }
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(8.sdp))
        RenderMainList(
            items,
            onItemClick
        )
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .weight(1f))
    }
}

@Composable
fun RenderMainList(
    statesList: LazyPagingItems<Referral>,
    deeplinkNavigateFunction: (String) -> Unit
) {
    val listState: LazyListState = rememberLazyListState()
    LazyColumn(state = listState) {
        items(statesList.itemSnapshotList.toList()) {
            it?.let { it1 -> RenderReferralItem(list = it1, deeplinkNavigateFxn = deeplinkNavigateFunction) }
        }
    }
}
