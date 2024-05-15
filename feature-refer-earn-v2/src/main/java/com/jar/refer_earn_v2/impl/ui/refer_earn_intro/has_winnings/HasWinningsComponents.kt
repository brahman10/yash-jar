package com.jar.refer_earn_v2.impl.ui.refer_earn_intro.has_winnings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jar.app.base.util.orFalse
import com.jar.app.core_base.util.orZero
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.theme.frauncesFontFamily
import com.jar.app.feature_refer_earn_v2.R
import com.jar.app.feature_refer_earn_v2.shared.domain.model.ReferIntroScreenData
import com.jar.app.core_compose_ui.utils.sdp




@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun RenderWithRewards(
    columnScope: ColumnScope,
    introScreenData: State<ReferIntroScreenData?>,
    RenderBottomSection: @Composable () -> Unit,
    rewardSection: (String) -> Unit, viewReferralsClick: () -> Unit
) {
    val state = rememberScrollState()
    columnScope.apply {
        val buttonText =
            if (!introScreenData.value?.hasWinnings.orFalse())
                pluralStringResource(
                    id = com.jar.app.feature_refer_earn_v2.shared.R.plurals.feature_refer_view_referrals,
                    count = introScreenData.value?.referralCount.orZero(),
                    introScreenData.value?.referralCount.orZero()
                )
            else
                stringResource(id = com.jar.app.feature_refer_earn_v2.shared.R.string.feature_refer_view_referrals)

        Column(Modifier
            .then(if (LocalConfiguration.current.screenHeightDp < 580) Modifier.verticalScroll(state = state) else Modifier)) {
            Column(Modifier.background(colorResource(id = com.jar.app.core_ui.R.color.color_6038CE))) {
                Column(
                    Modifier
                        .background(
                            colorResource(id = com.jar.app.core_ui.R.color.color_221D32),
                            shape = RoundedCornerShape(bottomStart = 16.sdp, bottomEnd = 16.sdp)
                        )
                        .padding(start = 16.sdp, end = 16.sdp, bottom = 16.sdp)
                ) {
                    Spacer(
                        Modifier
                            .fillMaxWidth()
                            .height(10.sdp)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            stringResource(id = com.jar.app.feature_refer_earn_v2.shared.R.string.feature_refer_your_rewards),
                            style = JarTypography.dynamic.h4.copy(
                                fontFamily = frauncesFontFamily,
                            ),
                            color = Color.White
                        )
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = buttonText,
                            modifier = Modifier
                                .background(
                                    color = colorResource(
                                        id = R.color.color_33FFFAF2
                                    ),
                                    shape = RoundedCornerShape(6.sdp)
                                )
                                .padding(horizontal = 8.sdp, vertical = 5.sdp)
                                .debounceClickable { viewReferralsClick() },
                            color = Color.White,
                            style = JarTypography.dynamic.spRegular,
                            maxLines = 1
                        )
                    }
                    Spacer(
                        Modifier
                            .fillMaxWidth()
                            .height(10.sdp)
                    )
                    Row {
                        RenderRewardsCardList(introScreenData.value?.rewards, rewardSection)
                    }
                }
            }

            Column(
                Modifier
                    .background(colorResource(id = com.jar.app.core_ui.R.color.color_6038CE))
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 24.sdp, end = 24.sdp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(0.6f)) {
                        introScreenData?.value?.newRewardsText?.takeIf { !it.isBlank() }?.let {
                            Text(
                                text = it,
                                modifier = Modifier
                                    .background(
                                        color = colorResource(
                                            id = R.color.color_33FFFAF2
                                        ),
                                        shape = RoundedCornerShape(6.sdp)
                                    )
                                    .padding(horizontal = 12.sdp, vertical = 4.sdp),
                                color = Color.White,
                                style = JarTypography.dynamic.spRegular,
                                maxLines = 1
                            )
                        }

                        Text(
                            text = introScreenData.value?.staticContent?.header.orEmpty(),
                            style = JarTypography.dynamic.h4.copy(
                                fontFamily = frauncesFontFamily,
                            ),
                            color = Color.White,
                            maxLines = 2,
                        )
                    }
                    GlideImage(
                        model = introScreenData?.value?.staticContent?.icon,
                        contentDescription = "",
                        modifier = Modifier.weight(0.4f)
                    )
                }

                RenderBottomSection()
            }
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(colorResource(id = com.jar.app.core_ui.R.color.color_6038CE))
            )
        }
    }
}