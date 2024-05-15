package com.jar.app.feature_sell_gold.impl.ui.amount.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.jar.app.core_compose_ui.component.JarImage
import com.jar.app.core_compose_ui.theme.JarTypography

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun VerificationStatusLabel(
    state: VerificationStatusState,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .widthIn(68.dp)
            .height(24.dp)
            .background(state.backgroundColor, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        JarImage(imageUrl = state.iconUrl, contentDescription = null)
        Spacer(modifier = Modifier.size(4.dp))
        state.text?.let {
            Text(
                text = it,
                style = JarTypography.caption.copy(fontWeight = FontWeight.ExtraBold)
            )
        }
    }
}

@Stable
data class VerificationStatusState(
    val iconUrl: String,
    val text: AnnotatedString?,
    val backgroundColor: Color
)