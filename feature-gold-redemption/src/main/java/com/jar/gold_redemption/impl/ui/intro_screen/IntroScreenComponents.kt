package com.jar.gold_redemption.impl.ui.intro_screen

import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.jar.app.base.util.getSecondAndMillisecondFormat
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.frauncesFontFamily
import com.jar.app.core_compose_ui.theme.jarFontFamily
import com.jar.app.core_compose_ui.utils.GradientAngle
import com.jar.app.core_compose_ui.utils.GradientOffset
import com.jar.app.core_compose_ui.utils.angledGradientBackground
import com.jar.app.core_compose_ui.views.RenderBaseToolBar
import com.jar.app.feature_gold_redemption.shared.data.network.model.VouchersEducation
import com.jar.gold_redemption.impl.ui.common_ui.*
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import java.text.SimpleDateFormat
import java.util.Date


@Composable
fun RenderToolBar(myOrdersText: State<String?>, OnRightButtonClick: () -> Unit, backPress: () -> Unit) {
    RenderBaseToolBar(modifier = Modifier.background(colorResource(id = com.jar.app.core_ui.R.color.color_480B10)), onBackClick = {
        backPress()
    }, title = stringResource(com.jar.app.feature_gold_redemption.shared.R.string.jewellery_vouchers), {
        GoldButton(
            modifier = Modifier
                .padding(end = 16.dp)
                .debounceClickable { OnRightButtonClick() },
            text = myOrdersText.value ?: stringResource(com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_my_orders),
            fontSize = 14.sp,
            fontFamily = jarFontFamily
        )
    })
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MainContent(
    goldDiamondTableImageLink: State<String?>,
    fromScreen: String? = null,
    startTime: Long? = null,
    eventName: String? = null,
    apiResponseCount: Int? = null,
    analyticsHandler: AnalyticsApi? = null,
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    var screenRecomposeCount by remember { mutableStateOf(apiResponseCount) }

    Column(
        Modifier.fillMaxWidth()
    ) {
        IntroScreenArc {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                .padding(top = 0.dp, start = 20.dp, end = 20.dp)
                .graphicsLayer {
                    translationY = 100.dp.value
                }) {
                GoldText(
                    text = stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.celebrate_every_occasion_with_gold_and_diamond_vouchers),
                    fontSize = 26.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.W400
                )
                GlideImage(
                    model = goldDiamondTableImageLink.value,
                    contentDescription = "",
                    modifier = Modifier
                        .height(screenHeight / 2.2f)
                        .align(Alignment.CenterHorizontally),
                    requestBuilderTransform = {
                        it.addListener(object : RequestListener<Drawable?> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable?>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable?>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                if (screenRecomposeCount == 2 && (fromScreen == BaseConstants.BuyGoldFlowContext.HAMBURGER_MENU || fromScreen == EventKey.HOME_SCREEN)) {
                                    screenRecomposeCount = screenRecomposeCount?.plus(1)
                                    val isFromCache =
                                        dataSource === DataSource.MEMORY_CACHE || dataSource === DataSource.DATA_DISK_CACHE
                                    val currentTime = System.currentTimeMillis()
                                    analyticsHandler!!.postEvent(eventName.orEmpty(),
                                        mapOf(
                                            EventKey.IS_FROM_CACHE to isFromCache,
                                            EventKey.TIME_IT_TOOK to getSecondAndMillisecondFormat(endTimeTime = currentTime, startTime = startTime!!)
                                        )
                                    )
                                }
                                return false
                            }
                        })
                    }

                    )
            }
        }
        BrandPartnerContainer()
    }
}

@Composable
@Preview
fun IntroScreenArcPreview() {
    IntroScreenArc {
        Column(
            Modifier
                .fillMaxWidth()
                .height(100.dp)
        ) {

        }
    }
}

@Composable
fun IntroScreenArc(function: (@Composable () -> Unit)) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val gradientOffset = remember { GradientOffset(GradientAngle.CW90) }
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(1f)
            .background(colorResource(id = com.jar.app.core_ui.R.color.color_480B10))
            .padding(start = 20.dp, end = 20.dp, top = 8.dp)
            .clip(
                RoundedCornerShape(
                    topStart = screenWidth / 2, topEnd = screenWidth / 2
                )
            )
            // clip to the circle shape
            .border(
                width = 1.5.dp, brush = Brush.linearGradient(
                    colors = listOf(
                        colorResource(id = com.jar.app.core_ui.R.color.color_93722f),
                        colorResource(id = com.jar.app.core_ui.R.color.color_9c8350),
                        colorResource(id = com.jar.app.core_ui.R.color.color_bfa673),
                        colorResource(id = com.jar.app.core_ui.R.color.color_dbc28f),
                        colorResource(id = com.jar.app.core_ui.R.color.color_886f3c),
                        colorResource(id = com.jar.app.core_ui.R.color.color_765e2a),
                        colorResource(id = com.jar.app.core_ui.R.color.color_43090D),
                        colorResource(id = com.jar.app.core_ui.R.color.color_43090D),
                        colorResource(id = com.jar.app.core_ui.R.color.color_43090D),
                        colorResource(id = com.jar.app.core_ui.R.color.color_43090D),
                        colorResource(id = com.jar.app.core_ui.R.color.color_43090D),
                        colorResource(id = com.jar.app.core_ui.R.color.color_43090D),
                        colorResource(id = com.jar.app.core_ui.R.color.color_43090D),
                        colorResource(id = com.jar.app.core_ui.R.color.color_43090D),
                        colorResource(id = com.jar.app.core_ui.R.color.color_43090D),
                        colorResource(id = com.jar.app.core_ui.R.color.color_43090D),
                        colorResource(id = com.jar.app.core_ui.R.color.color_43090D),
                        colorResource(id = com.jar.app.core_ui.R.color.color_43090D),
                        colorResource(id = com.jar.app.core_ui.R.color.color_43090D),
                        colorResource(id = com.jar.app.core_ui.R.color.color_43090D),
                    ),
                    tileMode = TileMode.Mirror,
                    start = gradientOffset.start,
                    end = gradientOffset.end
                ), shape = RoundedCornerShape(
                    topStart = screenWidth / 2, topEnd = screenWidth / 2
                )
            )
            .background(
                brush = Brush.linearGradient(
                    listOf(
                        colorResource(id = com.jar.app.core_ui.R.color.color_9C2E33),
                        colorResource(id = com.jar.app.core_ui.R.color.color_43090D),
                    )
                )
            ), contentAlignment = Alignment.TopCenter
    ) {
        function()
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun BrandPartnerContainer() {
    val gradientOffset = remember { GradientOffset(GradientAngle.CW90) }
    Row(
        modifier = Modifier
            .background(colorResource(id = com.jar.app.core_ui.R.color.color_480B10))
            .background(
                Color.White, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            )
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(vertical = 20.dp), Arrangement.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_brand_partners),
                fontSize = 24.sp,
                style = TextStyle(
                    brush = Brush.linearGradient(
                        colors = getGoldGradientColorList(),
                        start = gradientOffset.start,
                        end = gradientOffset.end,
                    ), fontFamily = frauncesFontFamily, fontStyle = FontStyle.Normal, fontWeight = FontWeight.Normal
                ),
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun BoxWithCircleTextImage(title: String, id: String?) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    BoxWithConstraints(
        modifier = Modifier
            .background(colorResource(id = com.jar.app.core_ui.R.color.color_711C21))
            .width(screenWidth / 3.5f)
            .padding(bottom = 20.dp), contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier
            .background(colorResource(id = com.jar.app.core_ui.R.color.color_711C21))
            .graphicsLayer {
                translationY = (maxWidth * 1.3f).value / 2
            }
            .width(maxWidth)
            .height(maxWidth * 1.3f)
            .background(
                color = Color.White.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)
            )
            .goldBorder(shape = RoundedCornerShape(8.dp), width = 1.5.dp)
            .padding(top = 20.dp), contentAlignment = Alignment.Center) {
            GoldText(text = title, textAlign = TextAlign.Center, fontSize = 20.sp, modifier = Modifier.padding(horizontal = 4.dp))
        }
        Box(
            modifier = Modifier
                .width(maxWidth / 2)
                .height(maxWidth / 2)
                .zIndex(2f)
                .align(Alignment.TopCenter)
                .background(
                    colorResource(id = com.jar.app.core_ui.R.color.color_711C21),
                    shape = CircleShape
                )
                .background(color = Color.White.copy(alpha = 0.1f), shape = CircleShape)
                .goldBorder(shape = CircleShape, width = 1.5.dp)
                .padding(4.dp)
        ) {
            GlideImage(
                id, contentDescription = "", modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            )
        }
    }
}

@Composable
@Preview
fun BoxWithCircleTextImagePreview() {
    BoxWithCircleTextImage("Buy a voucher on Jar", "")
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ExpandedContent3(footer: String, footerImage: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp)
            .angledGradientBackground(
                listOf(
                    colorResource(com.jar.app.core_ui.R.color.color_711C21),
                    colorResource(com.jar.app.core_ui.R.color.color_121127),
                ), angle = 270f
            )
            .padding(start = 20.dp, end = 20.dp, bottom = 40.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        GoldText(
            text = footer ?: "Get upto 10%\nextra gold in\nyour Jar\nlocker!", modifier = Modifier.fillMaxWidth(0.5f), fontSize = 24.sp, fontWeight = FontWeight.Bold
        )
        GlideImage(
            footerImage,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            contentDescription = "",
            alignment = Alignment.CenterEnd,
        )
    }
}

@Composable
internal fun ExpandedContent2(vouchersEducationList: List<VouchersEducation?>? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = colorResource(id = com.jar.app.core_ui.R.color.color_711C21)),
    ) {
        Spacer(Modifier.weight(1f))
        for (i in 0..2) {
            vouchersEducationList?.getOrNull(i)?.let {
                BoxWithCircleTextImage(it.title.orEmpty(), it?.imageUrl)
                if (i != 2) Spacer(Modifier.width(8.dp))
            }
        }
        Spacer(Modifier.weight(1f))
    }
}

@Composable
@Preview
fun ExpandedContent1(voucherEducationHeader: String? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(id = com.jar.app.core_ui.R.color.color_711C21))
            .padding(vertical = 30.dp), Arrangement.Center
    ) {
        VerticalGradientGoldText2(
            text = voucherEducationHeader ?: stringResource(id = com.jar.app.feature_gold_redemption.shared.R.string.feature_gold_redemption_how_voucher_work),
            fontSize = 26.sp,
            fontFamily = frauncesFontFamily
        )
    }
}

@Composable
@Preview
fun BrandImagesPreview() {
    BrandImagesContainer(
        imageList = listOf(
            "https://d21tpkh2l1zb46.cloudfront.net/Images/Gold_Redemption/brands/joyalukkas.png",
            "https://d21tpkh2l1zb46.cloudfront.net/Images/Gold_Redemption/brands/giva.png",
            "https://d21tpkh2l1zb46.cloudfront.net/Images/Gold_Redemption/brands/bluestone.png",
            "https://d21tpkh2l1zb46.cloudfront.net/Images/Gold_Redemption/brands/kalyan.png",
            "https://d21tpkh2l1zb46.cloudfront.net/Images/Gold_Redemption/brands/giva.png",
            "https://d21tpkh2l1zb46.cloudfront.net/Images/Gold_Redemption/brands/bluestone.png",
            "https://d21tpkh2l1zb46.cloudfront.net/Images/Gold_Redemption/brands/kalyan.png",
            "https://d21tpkh2l1zb46.cloudfront.net/Images/Gold_Redemption/brands/joyalukkas.png",
            "https://d21tpkh2l1zb46.cloudfront.net/Images/Gold_Redemption/brands/kalyan.png",
            "https://d21tpkh2l1zb46.cloudfront.net/Images/Gold_Redemption/brands/joyalukkas.png",
            "https://d21tpkh2l1zb46.cloudfront.net/Images/Gold_Redemption/brands/bluestone.png",
            "https://d21tpkh2l1zb46.cloudfront.net/Images/Gold_Redemption/brands/giva.png"
        )
    )
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalGlideComposeApi::class)
@Composable
fun BrandImagesContainer(imageList: List<String?>) {
    val images = rememberSaveable { mutableStateOf(imageList) }
    Column(modifier = Modifier.fillMaxWidth()) {
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 4.dp),
            maxItemsInEachRow = 4,
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(images.value.size) {
                images.value.getOrNull(it)?.let {
                    GlideImage(
                        model = it, contentDescription = "BrandImage", modifier = Modifier
                            .padding(horizontal = 4.dp, vertical = 20.dp)
                            .widthIn(max = 60.dp)
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .background(
                    Color.White, RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                )
                .fillMaxWidth()
                .height(40.dp)
        ) {

        }
    }
}