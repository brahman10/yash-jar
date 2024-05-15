package com.jar.gold_redemption.impl.ui.voucher_success

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.feature_gold_redemption.R
import com.jar.app.feature_gold_redemption.shared.data.network.model.PurchaseItemData
import kotlinx.coroutines.Job

@Composable
internal fun RenderVoucherPurchaseSummaryBottomSheet(
    voucherList: List<PurchaseItemData>?,
    function1: Boolean,
    function: () -> Job
) {
    BackHandler(enabled = function1) {
        function()
    }
    Column(
        Modifier
            .fillMaxWidth()
            .background(color = colorResource(id = com.jar.app.core_ui.R.color.color_272239))
    ) {
        Row(Modifier.padding(vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = (stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_voucher_purchase_summary)),
                style = JarTypography.h6,
                color = colorResource(
                    id = com.jar.app.core_ui.R.color.white
                ),
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = painterResource(id = com.jar.app.core_ui.R.drawable.core_ui_ic_close),
                contentDescription = "",
                tint = Color.White,
                modifier = Modifier.debounceClickable {
                    function()
                }
            )
            Spacer(modifier = Modifier.width(16.dp))
        }
        LazyColumn(contentPadding = PaddingValues(16.dp)) {
            items(voucherList?.size ?: 0) { index ->
                val item = voucherList?.get(index)
                item?.let {
                    VoucherPurchaseSummaryItem(item)
                }
            }
        }
    }
}