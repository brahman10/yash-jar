package com.jar.feature_quests.impl.ui.rewards_screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jar.app.core_compose_ui.component.noRippleDebounceClickable
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.feature_quests.impl.ui.dashboard_screen.fromHex
import com.jar.feature_quests.shared.domain.model.RewardItem

@Preview
@Composable
fun PreviewRewardItemView() {
    RewardViewItem(
        rewardItem = RewardItem(
            icon = "https://path.to.icon.png",
            iconColor = "#FF0000",
            backgroundColor = "#FFFFFF",
            title = "Sample Title",
            description = "This is a sample description.",
            bottomText = "Optional Bottom Text",
            bottomColor = "#00FF00",
            shimmerUrl = "https://path.to.shimmer.gif"
        ),
        { x, y ->

        },
        0
    )
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun RewardViewItem(rewardItem: RewardItem, onClick: (rewardItem: RewardItem, index: Int) -> Unit, index: Int) {
    val boxHeight = LocalConfiguration.current.screenHeightDp.dp/3.5f
    Box(Modifier.height(boxHeight)) {
        Column(Modifier.height(boxHeight)) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .weight(8f)
                    .background(
                        fromHex(rewardItem.backgroundColor),
                        RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    )
                    .padding(16.dp)
                    .noRippleDebounceClickable {
                        onClick.invoke(rewardItem, index)
                    }
            ) {
                GlideImage(
                    model = rewardItem.icon,
                    modifier = Modifier
                        .background(fromHex(rewardItem.iconColor), CircleShape)
                        .padding(12.dp)
                        .height(boxHeight / 7)
                        .aspectRatio(1f),
                    contentDescription = ""
                )
                Text(
                    rewardItem.title,
                    style = JarTypography.body2.copy(fontSize = 12.sp),
                    color = Color.White,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = rewardItem.description,
                    style = JarTypography.h4,
                    color = Color.White,
                )
            }
            Column(
                Modifier
                    .fillMaxWidth()
                    .weight(2f)
                    .background(
                        fromHex(rewardItem.bottomColor),
                        RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
                    )
                    .padding(2.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = rewardItem.bottomText.orEmpty(),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    color = Color.White,
                    style = JarTypography.body2.copy(fontSize = 12.sp)
                )
            }
        }
        GlideImage(
            model = rewardItem.shimmerUrl,
            contentDescription = "",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.clip(
                RoundedCornerShape(20.dp)
            )
        )
    }
}
