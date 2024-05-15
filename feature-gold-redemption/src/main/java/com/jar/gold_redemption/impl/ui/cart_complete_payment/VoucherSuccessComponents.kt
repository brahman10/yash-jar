package com.jar.gold_redemption.impl.ui.cart_complete_payment

import android.view.View
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jar.app.core_base.util.orZero
import com.jar.app.core_compose_ui.component.RenderImagePillButton
import com.jar.app.core_ui.R
import com.jar.app.feature_gold_redemption.shared.domain.model.CardType
import com.jar.gold_redemption.impl.ui.my_vouchers.bottom_sheet.RenderVoucherDetailCard
import com.jar.app.feature_gold_redemption.shared.domain.model.CardStatus
import com.jar.app.feature_gold_redemption.shared.data.network.model.UserVoucher
import java.lang.ref.WeakReference

@Composable
@Preview
fun VoucherSuccessComponentsPreview() {
    VoucherSuccessComponents(
        Modifier,
        listOf<UserVoucher>(
            UserVoucher(
                voucherId = "",
                amount = 500,
                imageUrl = "",
                viewDetails = "",
                voucherName = "ASDASD",
                calendarUrl = "",
                code = "asdasd",
                noOfDaysToRedeem = 24,
                voucherExpiredText = "asdasd",
                voucherProcessingText = "asdas",
                validTillText = "ASdasd",
                currentState = CardType.GOLD.name,
                myVouchersType = CardStatus.EXPIRED.name,
                creationDate = ""
            ),
            UserVoucher(
                voucherId = "",
                amount = 500,
                imageUrl = "",
                viewDetails = "",
                voucherName = "ASDASD",
                calendarUrl = "",
                code = "asdasd",
                noOfDaysToRedeem = 24,
                voucherExpiredText = "asdasd",
                voucherProcessingText = "asdas",
                validTillText = "ASdasd",
                currentState = CardType.GOLD.name,
                myVouchersType = CardStatus.EXPIRED.name,
                creationDate = ""
            ),
            UserVoucher(
                voucherId = "",
                amount = 500,
                imageUrl = "",
                viewDetails = "",
                voucherName = "ASDASD",
                calendarUrl = "",
                code = "asdasd",
                noOfDaysToRedeem = 24,
                voucherExpiredText = "asdasd",
                voucherProcessingText = "asdas",
                validTillText = "ASdasd",
                currentState = CardType.GOLD.name,
                myVouchersType = CardStatus.EXPIRED.name,
                creationDate = ""
            )
        ),
        Modifier,
        0.5f,
        true
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun VoucherSuccessComponents(
    defaultModifier: Modifier,
    list: List<UserVoucher>,
    modifier: Modifier,
    alpha: Float = 1f,
    showCardNoHideBtn: Boolean,
    viewRef: WeakReference<View?>? = null,
) {
    val state = rememberLazyListState()
    val snapLayoutInfoProvider = SnapLayoutInfoProvider(state)
    val snappingLayout = remember(state) { snapLayoutInfoProvider }
    val rememberSnapFlingBehavior = rememberSnapFlingBehavior(snappingLayout)
    val buttonText = remember(list) { derivedStateOf {
        "${ if (state.firstVisibleItemScrollOffset > 0)  state.firstVisibleItemIndex + 2 else state.firstVisibleItemIndex + 1}/${list.size}"
    }}

    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        LazyRow(
            modifier = modifier
                .fillMaxWidth()
                .align(Alignment.Start),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            state = state,
            flingBehavior = rememberSnapFlingBehavior,
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp)
        ) {
            items(list) {
                RenderVoucherDetailCard(
                    modifier = Modifier
                        .fillParentMaxWidth(if (list.size > 1) 0.95f else 1f),
                    amount = it.amount.orZero().toFloat(),
                    voucher = it,
                    bgColor = colorResource(id = R.color.color_3c3357),
                    showCardContainer = true,
                    showCardNoHideBtn = false,
                    isCardNoHidden = false,
                    showCopyClipboardBtn = false,
                    alpha = alpha,
                    elevation = 0.dp,
                    viewRef = viewRef,
                    showVoucherNameOnBanner = true,
                    imageUrl = it.imageUrl.orEmpty(),
                )
            }
        }
        if (list.size > 1) {
            Row(
                defaultModifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                RenderImagePillButton(
                    text = buttonText.value,
                    bgColor = com.jar.app.core_ui.R.color.color_5A5076,
                    textColor = com.jar.app.core_ui.R.color.color_D5CDF2,
                    maxLines = 1
                )
            }
        }

    }
}