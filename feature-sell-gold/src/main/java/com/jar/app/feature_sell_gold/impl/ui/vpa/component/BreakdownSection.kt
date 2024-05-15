package com.jar.app.feature_sell_gold.impl.ui.vpa.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.jar.app.core_base.shared.CoreBaseMR.colors.color_272239
import com.jar.app.core_base.shared.CoreBaseMR.colors.color_EBB46A
import com.jar.app.core_base.shared.CoreBaseMR.colors.color_EBB46A33
import com.jar.app.core_compose_ui.component.JarPrimaryButton
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_ui.R
import com.jar.app.feature_sell_gold.shared.MR.strings.confirm
import com.jar.app.feature_sell_gold.shared.MR.strings.view_breakdown

@Composable
fun BreakdownSection(
    withdrawalPrice: String,
    modifier: Modifier = Modifier,
    isEnabled: Boolean,
    onConfirmClick: () -> Unit,
    onViewBreakdownClick: () -> Unit
) {
    Row(
        modifier = modifier
            .background(colorResource(id = color_272239.resourceId))
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "₹$withdrawalPrice",
                style = JarTypography.h3,
                color = Color.White
            )
            Spacer(modifier = Modifier.size(8.dp))
            Row(
                modifier = Modifier.clickable(enabled = isEnabled, onClick = onViewBreakdownClick)
            ) {
                Text(
                    text = stringResource(id = view_breakdown.resourceId),
                    style = JarTypography.caption,
                    textDecoration = TextDecoration.Underline,
                    color = if (isEnabled) {
                        colorResource(id = color_EBB46A.resourceId)
                    } else {
                        colorResource(id = color_EBB46A33.resourceId)
                    }
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_down_chevron),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    alpha = if (isEnabled) 1f else 0.5f
                )
            }
        }
        JarPrimaryButton(
            text = stringResource(id = confirm.resourceId),
            onClick = onConfirmClick,
            modifier = Modifier.size(164.dp, 56.dp),
            isEnabled = isEnabled,
            isAllCaps = false
        )
    }
}