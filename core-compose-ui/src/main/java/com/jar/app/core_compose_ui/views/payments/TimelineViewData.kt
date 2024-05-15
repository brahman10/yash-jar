package com.jar.app.core_compose_ui.views.payments

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.jar.app.core_compose_ui.theme.JarTypography

data class TimelineViewData(
    val status: TransactionStatus? = null,
    val title: String,
    val date: String? = null,
    val refreshText: String? = null,
    val isRetryButtonEnabled: Boolean? = false,
    val isRefreshButtonShown: Boolean? = false,
    val refreshTextTypography: TextStyle = JarTypography.body2.copy(
        color = Color(0xFFACA1D3)
    ),
    val refreshTextMaxLines: Int = 2
)