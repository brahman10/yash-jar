package com.jar.app.feature_lending.impl.ui.realtime_flow.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jar.app.core_base.domain.model.Faq
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.views.ExpandableCardModel
import com.jar.app.core_compose_ui.views.ExpandableFaqCard
import com.jar.app.core_compose_ui.views.renderExpandableFaqList
import com.jar.app.core_ui.R


@Composable
fun RealTimeLandingFaqCard(
    modifier: Modifier = Modifier,
    title: String
) {



}

@Preview
@Composable
fun RealTimeLandingFaqCardPreview() {
    LazyColumn {
        item {

        }
    }
}