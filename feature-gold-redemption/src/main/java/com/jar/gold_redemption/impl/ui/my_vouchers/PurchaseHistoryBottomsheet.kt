package com.jar.gold_redemption.impl.ui.my_vouchers

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_ui.R
import com.jar.app.feature_gold_redemption.shared.data.network.model.PurchaseItemData
import kotlinx.coroutines.Job


@Composable
internal fun PurchaseHistoryBottomsheet(list: State<List<PurchaseItemData?>>, function: () -> Job, navigate: (PurchaseItemData) -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f)
            .background(colorResource(id = R.color.color_3C3357))
    ) {
        Image(
            painter = painterResource(id = com.jar.app.feature_gold_redemption.R.drawable.bottomsheet_handle),
            contentDescription = "",
            Modifier.padding(top = 16.dp, bottom = 20.dp).align(Alignment.CenterHorizontally).clickable {
                function()
            },
        )
        Row(Modifier.padding(bottom = 32.dp), verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(20.dp))
            Image(
                painter = painterResource(id = com.jar.app.feature_gold_redemption.R.drawable.alarm_clock_time),
                contentDescription = ""
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = (stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_payment_history)),
                style = JarTypography.h6,
                color = colorResource(
                    id = R.color.white
                ),
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.weight(1f))
        }
        LazyColumn(
            Modifier.fillMaxWidth(), contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(list.value.size) { index ->
                RenderPurchaseHistoryItem(list.value[index], navigate)
            }
        }
    }
}