package com.jar.gold_redemption.impl.ui.my_vouchers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jar.app.core_ui.R
import com.jar.app.feature_gold_redemption.shared.domain.model.CardType

@Composable
@Preview
fun VoucherCardTopBgPreview() {
    Column(
        Modifier
            .fillMaxWidth()
            .background(colorResource(id = R.color.color_2E2942))
    ) {
        VoucherCardTopBg (cardType = CardType.DIAMOND) {
            Spacer(
                Modifier
                    .width(60.dp)
            )
        }
    }
}

@Composable
fun VoucherCardTopBg(
    modifier: Modifier = Modifier,
    cardType: CardType,
    RightSection: @Composable() (() -> Unit)
) {
    val image = painterResource(id = if (cardType == CardType.DIAMOND) com.jar.app.feature_gold_redemption.R.drawable.card_silver_bg else com.jar.app.feature_gold_redemption.R.drawable.card_gold_bg)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .paint(
                image,
                alignment = Alignment.BottomCenter,
//                contentScale = ContentScale.Fit,
            )
        ) {
        RightSection()

        }
}
