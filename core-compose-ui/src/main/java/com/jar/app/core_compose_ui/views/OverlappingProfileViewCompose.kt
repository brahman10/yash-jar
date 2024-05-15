package com.jar.app.core_compose_ui.views

import androidx.annotation.FloatRange
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jar.app.core_compose_ui.R
import com.jar.app.core_compose_ui.utils.OverlappingRow


@Composable
@Preview
fun OverlappingProfileViewComposePreview() {
    OverlappingProfileViewCompose(
        list = listOf(
            "https://d21tpkh2l1zb46.cloudfront.net/Dhanteras'22/PeopleImages/g 1.png",
            "https://d21tpkh2l1zb46.cloudfront.net/Dhanteras'22/PeopleImages/g 1.png",
            "https://d21tpkh2l1zb46.cloudfront.net/Dhanteras'22/PeopleImages/g 1.png",
            "https://d21tpkh2l1zb46.cloudfront.net/Dhanteras'22/PeopleImages/g 1.png",
        )
    )
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun OverlappingProfileViewCompose(list: List<String?>, size: Dp = 64.dp) {
    OverlappingRow(
        overlapFactor = 0.5f
    ) {
        for (i in list.indices) {
            GlideImage(
                model = list[i],
                contentDescription = null,
                modifier = Modifier
                    .width(size)
                    .height(size)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    }
}
