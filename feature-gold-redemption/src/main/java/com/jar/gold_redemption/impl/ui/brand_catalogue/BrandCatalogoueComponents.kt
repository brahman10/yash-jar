package com.jar.gold_redemption.impl.ui.brand_catalogue

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.views.RenderHorizontalFilterList
import com.jar.app.core_compose_ui.views.TagWithShadow
import com.jar.app.feature_gold_redemption.R
import com.jar.app.feature_gold_redemption.shared.data.network.model.VoucherProducts


@Composable
fun RenderHorizontalList(
    selectedIndexPass: State<Int?>? = null,
    voucherCategoryTitleList: State<List<String>?>, listSize: Int, function: (Int) -> Unit
) {

    LazyColumn {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(id = com.jar.app.core_ui.R.color.color_272239))
                    .padding(vertical = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
//                                    .padding(bottom = 16.dp)
                ) {
                    Text(
                        stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_explore_vouchers),
                        color = Color.White,
                        style = JarTypography.h2,
                        modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)
                    )
                    RenderHorizontalFilterList(Modifier, voucherCategoryTitleList.value ?: listOf(), 1, selectedIndexPass = selectedIndexPass, function = function)
                }
            }
        }
        item {
            Text(
                stringResource(
                    id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_showing_d_vouchers,
                    listSize
                ),
                color = colorResource(id = com.jar.app.core_ui.R.color.color_D5CDF2),
                style = JarTypography.body1,
                modifier = Modifier.padding(
                    start = 16.dp,
                    top = 16.dp,
                    bottom = 16.dp
                )
            )
        }
    }
}

@Composable
@Preview
fun RenderEmptySection() {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    Column(
        modifier = Modifier.fillMaxWidth().height(screenHeight/2),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(
            Modifier
                .fillMaxWidth()
                .weight(1f)
        )
        Image(
            painterResource(id = com.jar.app.feature_gold_redemption.R.drawable.feature_gold_redemption_empty_voucher),
            contentDescription = ""
        )
        Text(
            stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_empty_vouchers_listing),
            modifier = Modifier.padding(top = 16.dp, bottom = 32.dp),
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Spacer(
            Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}
@Composable
@Preview
fun PreviewRenderVoucherItem() {
    RenderVoucherItem(Modifier, VoucherProducts(
        "", "", "",
        "", "", ""
    ), {
    })
}


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
internal fun RenderVoucherItem(modifier: Modifier = Modifier, it: VoucherProducts?, function: (VoucherProducts?) -> Unit) {
    Card(modifier = modifier
        .fillMaxWidth()
        .debounceClickable {
            function(it)
        }, shape = RoundedCornerShape(8.dp)) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier
                .height(height = 130.dp)
                .background(Color.White)) {
                GlideImage(
                    model = it?.imageUrl,
                    "",
                    modifier = Modifier
                        .height(120.dp)
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    contentScale = ContentScale.Fit
                )
                it?.discountText?.let {
                    TagWithShadow(
                        Modifier.padding(top = 12.dp),
                        it,
                        colorResource(id = com.jar.app.core_ui.R.color.color_1EA787),
                        colorResource(
                            id = com.jar.app.core_ui.R.color.color_58DDC8
                        )
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = colorResource(id = com.jar.app.core_ui.R.color.color_3C3357))
            ) {
                Text(
                    it?.title.orEmpty(),
                    style = JarTypography.h6,
                    modifier = Modifier.padding(start = 12.dp, top = 12.dp, end = 12.dp),
                    color = Color.White,
                    lineHeight = 24.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2,
                    minLines = 2
                )
                Text(
                    it?.startingAmountText.orEmpty(),
                    style = JarTypography.body1,
                    color = colorResource(id = com.jar.app.core_ui.R.color.color_ACA1D3),
                    modifier = Modifier.padding(
                        start = 12.dp,
                        end = 12.dp,
                        bottom = 12.dp,
                        top = 8.dp
                    ),
                )
            }
        }
    }
}
fun String.addEmptyLines(lines: Int) = this + "\n".repeat(lines)