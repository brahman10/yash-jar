package com.jar.gold_redemption.impl.ui.my_vouchers

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
@Preview
fun VoucherCardBottomBgPreview() {
    Column(
        Modifier
            .fillMaxWidth()
    ) {
        VoucherCardBottomBg(Alignment.CenterVertically) {
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            )
        }
    }
}

@Composable
fun VoucherCardBottomBg(
    verticalAlignment: Alignment.Vertical,
    RightSection: @Composable() (() -> Unit)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .paint(
                painter = painterResource(com.jar.app.feature_gold_redemption.R.drawable.card_bottom_bg),
                alignment = Alignment.TopCenter,
                contentScale = ContentScale.Fit,
            ),
        verticalAlignment = verticalAlignment,
    ) {
        RightSection()
    }
}