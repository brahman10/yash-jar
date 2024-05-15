package com.jar.gold_redemption.impl.ui.voucher_purchase

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jar.app.base.util.getFormattedAmount
import com.jar.app.core_base.util.orZero
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_ui.R
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.AMOUNT_CHANGED_FROM
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.AMOUNT_CHANGED_TO
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.GOLD_BENEFIT
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.GOLD_BENEFIT_PERCENTAGE
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.MINIMUM_VOUCHER_AMOUNT
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.REDEMPTION_AVAILABILITY
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.Redemption_VAmountChanged
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.VOUCHER_QUANTITY
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.VOUCHER_TITLE
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.VOUCHER_TYPE
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import java.lang.ref.WeakReference

@Composable
@Preview
fun RenderAmountRowPreview1() {
    RenderAmountRow(500f, true) {
    }
}

@Composable
@Preview
fun RenderAmountRowPreview2() {
    RenderAmountRow(1000f, false) {
    }
}

@Composable
fun RenderAmountRow(amount: Float, isSelected: Boolean, function: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .then(
                if (isSelected) {
                    Modifier.background(colorResource(id = com.jar.app.core_ui.R.color.color_3C3357))
                } else {
                    Modifier
                }
            )
            .padding(vertical = 16.dp)
            .debounceClickable {
                function()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(24.dp))
        Text(
            text = (stringResource(
                id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_feature_buy_gold_currency_sign_x_string,
                amount.toInt().getFormattedAmount()
            )),
            style = JarTypography.body1,
            color = colorResource(
                id = com.jar.app.core_ui.R.color.color_EEEAFF
            )
        )
        Spacer(modifier = Modifier.weight(1f))
        if (isSelected) {
            Icon(
                painter = painterResource(id = com.jar.app.feature_gold_redemption.R.drawable.feature_gold_redemption_tick),
                contentDescription = "",
                tint = Color.White,
                modifier = Modifier.width(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(24.dp))
    }
}

@Composable
internal fun RenderAmountBottomSheet(analyticsHandler: AnalyticsApi, viewModel: VoucherPurchaseViewModel, function: () -> Unit) {
    val observeAsState = viewModel.amountList.observeAsState()
    val selectedIndex = remember { mutableStateOf<Int>(0) }
    val context = LocalContext.current
    LaunchedEffect(key1 = viewModel.selectedAmount, block = {
        if (observeAsState.value.isNullOrEmpty()) return@LaunchedEffect
        selectedIndex.value =
            observeAsState.value?.indexOfFirst { it == viewModel.selectedAmount.value } ?: 0
    })

    LazyColumn(
        Modifier
            .fillMaxWidth()
            .background(colorResource(id = com.jar.app.core_ui.R.color.color_2e2942)),
        userScrollEnabled = false
    ) {
        item {
            Row(Modifier.padding(vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = (stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.select_voucher_amount)),
                    style = JarTypography.h2,
                    color = colorResource(
                        id = R.color.white
                    )
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    painter = painterResource(id = R.drawable.core_ui_ic_close),
                    contentDescription = "",
                    tint = Color.White,
                    modifier = Modifier.debounceClickable {
                        function()
                    }
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
        observeAsState.value?.let {
            itemsIndexed(it) { index, amount ->
                amount?.let { it1 ->
                    RenderAmountRow(it1, index == selectedIndex.value) {
                        selectedIndex.value = index
                        viewModel.setSelectedAmount(it1)
                        viewModel.resetValues(WeakReference(context))
                        val it = viewModel.voucherPurchase.value
                        analyticsHandler.postEvent(
                            Redemption_VAmountChanged, mapOf<String, String>(
                            AMOUNT_CHANGED_FROM to viewModel.selectedAmount?.value.orZero().toString(),
                            AMOUNT_CHANGED_TO to it1.orZero().toString(),
                            VOUCHER_TITLE to it?.title.orEmpty(),
                            VOUCHER_TYPE to it?.type.orEmpty(),
                            GOLD_BENEFIT_PERCENTAGE to it?.discountPercentage?.orZero()?.toString().orEmpty(),
                            MINIMUM_VOUCHER_AMOUNT to it?.amountList?.getOrNull(0).orZero().toString(),
                            GOLD_BENEFIT to (it?.amountList?.getOrNull(0).orZero().times(it?.discountPercentage.orZero())).orZero().toString(),
                            REDEMPTION_AVAILABILITY to (computeAvailability(it)),
                            VOUCHER_QUANTITY to viewModel.quantity.value.orZero().toString()
                        ))
                        function()
                    }
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}


