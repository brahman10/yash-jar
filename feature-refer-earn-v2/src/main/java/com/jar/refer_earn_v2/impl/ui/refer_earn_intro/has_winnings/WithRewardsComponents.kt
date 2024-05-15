package com.jar.refer_earn_v2.impl.ui.refer_earn_intro.has_winnings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jar.app.core_compose_ui.component.noRippleDebounceClickable
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_ui.R
import com.jar.app.feature_refer_earn_v2.shared.domain.model.RewardData
import com.jar.app.core_compose_ui.utils.sdp

@Composable
@Preview
fun RenderRewardsCardPreview() {
    RenderRewardsCardList(
        listOf(
            RewardData(
                "Winnins",
                "https://cdn.myjar.app/ReferAndEarn/referral-jar-winning.png",
                "JAR_WINNINGS",
                title = "Winnings"
            ),
            RewardData(
                "Winnins",
                "https://cdn.myjar.app/ReferAndEarn/referral-jar-winning.png",
                "JAR_WINNINGS",
                title = "Winnings"
            ),
            RewardData(
                "Winnins",
                "https://cdn.myjar.app/ReferAndEarn/referral-jar-winning.png",
                "JAR_WINNINGS",
                title = "Winnings"
            ),
        ),
        { }
    )
}


@Composable
fun RenderRewardsCardList(list: List<RewardData?>?, rewardSection: (String) -> Unit) {
    Card (Modifier.fillMaxWidth(), RoundedCornerShape(20.sdp), backgroundColor = Color.White) {
        Row(
            Modifier
                .height(IntrinsicSize.Min)
        ) {
            list?.forEachIndexed { index, rewardsCardData ->
                if (rewardsCardData != null)
                    this.RenderRewardCard(rewardsCardData, rewardSection)
                if (index != list.size - 1) {
                    Divider(
                        modifier = Modifier
                            .padding(horizontal = 8.sdp, vertical = 10.sdp)
                            .width(2.sdp)
                            .fillMaxHeight()
                        ,
                        color = colorResource(id = R.color.color_ACA1D3).copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun RowScope.RenderRewardCard(rewardsCardData: RewardData, rewardSection: (String) -> Unit) {
    Column(modifier = Modifier.weight(1f).fillMaxHeight().noRippleDebounceClickable { rewardSection(rewardsCardData.referralRewardType.orEmpty()) }, horizontalAlignment = Alignment.CenterHorizontally) {
        GlideImage(rewardsCardData.icon, contentDescription = "", modifier = Modifier.size(36.sdp))
        Text(
            text = rewardsCardData.title.orEmpty(),
            modifier = Modifier.padding(top = 6.sdp, bottom = 8.sdp),
            style = JarTypography.dynamic.pSemiBold,
            color = colorResource(
                id = com.jar.app.core_ui.R.color.color_29243C
            )
        )

        Spacer(Modifier.fillMaxWidth().weight(1f))
        rewardsCardData.amountText?.let {
            Text(
                it,
                modifier = Modifier.padding(bottom = 0.dp),
                style = JarTypography.dynamic.h3,
                color = colorResource(
                    id = com.jar.app.core_ui.R.color.color_221D32
                )
            )
        } ?: run {
            Image(
                painter = painterResource(id = com.jar.app.feature_refer_earn_v2.R.drawable.feature_refer_earn_lock),
                contentDescription = "",
                modifier = Modifier.size(30.sdp).padding(bottom = 6.sdp)
            )
        }
    }
}

