@file:OptIn(ExperimentalGlideComposeApi::class)

package com.jar.app.feature_sell_gold.impl.ui.amount.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.jar.app.core_base.shared.CoreBaseMR
import com.jar.app.core_compose_ui.component.ICON_GRAVITY_END
import com.jar.app.core_compose_ui.component.JarCommonBoldText
import com.jar.app.core_compose_ui.component.JarImage
import com.jar.app.core_compose_ui.component.JarPrimaryButton
import com.jar.app.core_compose_ui.component.JarSecondaryButton
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.utils.generateAnnotatedFromHtmlString
import com.jar.app.core_ui.R
import com.jar.app.feature_sell_gold.shared.domain.models.KycDetailsResponse

@Composable
fun VerificationBottomSheet(
    kycDetails: KycDetailsResponse?,
    modifier: Modifier = Modifier,
    onCloseClick: () -> Unit,
    onVerifyClick: () -> Unit,
    onContactUsClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 308.dp, max = 436.dp)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        IconButton(
            onClick = onCloseClick,
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.End)
        ) {
            Image(
                painter = painterResource(id = R.drawable.core_ui_ic_cross_outline),
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.size(8.dp))
        JarImage(
            imageUrl = kycDetails?.bottomSheet?.iconLink,
            modifier = Modifier.size(80.dp),
            contentDescription = null
        )
        Spacer(modifier = Modifier.size(16.dp))
        JarCommonBoldText(
            text = kycDetails?.bottomSheet?.title.orEmpty(),
            style = JarTypography.h5.copy(textAlign = TextAlign.Center),
            color = Color.White,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = kycDetails?.bottomSheet?.description.orEmpty().generateAnnotatedFromHtmlString(),
            style = JarTypography.body2.copy(textAlign = TextAlign.Center),
            color = colorResource(id = CoreBaseMR.colors.color_ACA1D3FF.resourceId),
            modifier = Modifier.padding(horizontal = 16.dp),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.size(24.dp))
        JarPrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            text = kycDetails?.bottomSheet?.verifyCta.orEmpty(),
            onClick = onVerifyClick,
            icon = kycDetails?.bottomSheet?.buttonIcon,
            iconGravity = ICON_GRAVITY_END,
            isAllCaps = false
        )
        if (kycDetails?.docType == "ID") {
            Spacer(modifier = Modifier.size(16.dp))
            JarSecondaryButton(
                modifier = Modifier.fillMaxWidth(),
                text = kycDetails.bottomSheet?.contactCta.orEmpty(),
                onClick = onContactUsClick,
                isAllCaps = false
            )
        }
    }
}