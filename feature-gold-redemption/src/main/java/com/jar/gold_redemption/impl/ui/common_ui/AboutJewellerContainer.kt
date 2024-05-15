package com.jar.gold_redemption.impl.ui.common_ui


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jar.app.base.util.openUrlInChromeTabOrExternalBrowser
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.utils.ComposeLinkText
import com.jar.app.core_compose_ui.utils.generateAnnotatedFromHtmlString
import com.jar.app.feature_gold_redemption.R
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.Redemption_OnlineRedemptionLinkClicked
import com.jar.app.feature_gold_redemption.shared.util.GoldRedemptionAnalyticsKeys.Redemption_VStateBSOpened

@Composable()
fun AboutJewellerContainer(
    modifier: Modifier = Modifier,
    inStoreRedemptionText: String? = null,
    onlineRedemptionText: String? = null,
    inStoreRedemptionTitleText: String? = null,
    onlineRedemptionTitleText: String? = null,
    showStatesDropdown: Boolean = false,
    title: String? = null,
    analyticsFunction: (it: String) -> Unit,
    onStatesDropDownClick: () -> Unit
) {
    val current = LocalContext.current
    if (inStoreRedemptionText.isNullOrBlank() &&
        onlineRedemptionText.isNullOrBlank()
    ) {
        return
    }
    Column(
        modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        title?.let {
            Spacer(modifier = Modifier.height(32.0.dp))
            Text(
                it,
                Modifier,
                color = Color.White,
                style = JarTypography.h6,
                fontSize = 24.sp
            )
        } ?: run {
            Spacer(modifier = Modifier.height(16.0.dp))
        }

        onlineRedemptionText?.let {
            Spacer(modifier = Modifier.height(16.0.dp))
            Text(
                onlineRedemptionTitleText ?: stringResource(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_online_redemption).uppercase(),
                Modifier,
                color = Color(0.67f, 0.63f, 0.83f, 1.0f),
                style = JarTypography.h6,

                )
            Spacer(modifier = Modifier.height(12.0.dp))
            ComposeLinkText(it) { text ->
                analyticsFunction(Redemption_OnlineRedemptionLinkClicked)
                openUrlInChromeTabOrExternalBrowser(current, text)
            }
        }

        if (!onlineRedemptionText.isNullOrBlank() && !inStoreRedemptionText.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(16.0.dp))
            Spacer(modifier = Modifier
                .height(1.0.dp)
                .fillMaxWidth()
                .background(colorResource(id = com.jar.app.core_ui.R.color.color_3C3357)))
        }

        inStoreRedemptionText?.let {
            Spacer(modifier = Modifier.height(16.0.dp))
            Text(
                inStoreRedemptionTitleText ?: stringResource(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_offline_store_list).uppercase(),
                Modifier,
                color = Color(0.67f, 0.63f, 0.83f, 1.0f),
                style = JarTypography.h6
            )

            Spacer(modifier = Modifier.height(12.0.dp))
            Text(
                it.generateAnnotatedFromHtmlString(),
                Modifier,
                color = Color(0.93f, 0.92f, 1.0f, 1.0f),
                style = JarTypography.body1
            )

            if (showStatesDropdown) {
                Spacer(modifier = Modifier.height(12.0.dp))
                DropDownText(onStatesDropDownClick, analyticsFunction)
            }
        }
    }

    Spacer(modifier = Modifier.height(32.0.dp))
}

@Composable
@Preview
fun DropDownTextPreview() {
    DropDownText({  }, { })
}

@Composable
fun DropDownText(onStatesDropDownClick: () -> Unit, analyticsFunction: (it: String) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(
                color = colorResource(id = com.jar.app.core_ui.R.color.color_3C3357),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 14.dp)
            .debounceClickable {
                analyticsFunction(Redemption_VStateBSOpened)
                onStatesDropDownClick()
            }
    ) {
        Text(
            text = stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.select_state),
            color = colorResource(id = com.jar.app.core_ui.R.color.color_ACA1D3),
            style = JarTypography.body1
        )
        Spacer(Modifier.weight(1f))
        Icon(
            Icons.Filled.ArrowDropDown,
            "Trailing icon for exposed dropdown menu",
            tint = colorResource(id = com.jar.app.core_ui.R.color.color_ACA1D3),
        )
    }
}

@Composable
@Preview
fun AboutJewelleryContainerPreview() {
    AboutJewellerContainer(Modifier, title = "Hello", analyticsFunction = {}) {}
}