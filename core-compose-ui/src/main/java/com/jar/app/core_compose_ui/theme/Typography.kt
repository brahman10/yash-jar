package com.jar.app.core_compose_ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.TextUnit
import com.intuit.ssp.R
import com.jar.app.core_compose_ui.theme.JarTypography.b1
import com.jar.app.core_compose_ui.theme.JarTypography.b2
import com.jar.app.core_compose_ui.theme.JarTypography.b3
import com.jar.app.core_compose_ui.theme.JarTypography.b4
import com.jar.app.core_compose_ui.theme.JarTypography.b5
import com.jar.app.core_compose_ui.theme.JarTypography.bodyBold
import com.jar.app.core_compose_ui.theme.JarTypography.bodyRegular
import com.jar.app.core_compose_ui.theme.JarTypography.bodySemiBold
import com.jar.app.core_compose_ui.theme.JarTypography.h1
import com.jar.app.core_compose_ui.theme.JarTypography.h2
import com.jar.app.core_compose_ui.theme.JarTypography.h3
import com.jar.app.core_compose_ui.theme.JarTypography.h4
import com.jar.app.core_compose_ui.theme.JarTypography.h5
import com.jar.app.core_compose_ui.theme.JarTypography.h6
import com.jar.app.core_compose_ui.theme.JarTypography.label
import com.jar.app.core_compose_ui.theme.JarTypography.largeBodyRegular
import com.jar.app.core_compose_ui.theme.JarTypography.pBold
import com.jar.app.core_compose_ui.theme.JarTypography.pRegular
import com.jar.app.core_compose_ui.theme.JarTypography.pSemiBold
import com.jar.app.core_compose_ui.theme.JarTypography.smallLabel
import com.jar.app.core_compose_ui.theme.JarTypography.spRegular
import com.jar.app.core_compose_ui.theme.JarTypography.t1
import com.jar.app.core_compose_ui.theme.JarTypography.t2
import com.jar.app.core_compose_ui.theme.JarTypography.t3
import com.jar.app.core_compose_ui.theme.JarTypography.t4

@OptIn(ExperimentalTextApi::class)
val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = com.jar.app.core_ui.R.array.com_google_android_gms_fonts_certs
)

@OptIn(ExperimentalTextApi::class)
val interFont = GoogleFont("Inter")

@OptIn(ExperimentalTextApi::class)
val robotoFont = GoogleFont("Roboto")


@OptIn(ExperimentalTextApi::class)
val frauncesFont = GoogleFont("Fraunces")

val hindFont = GoogleFont("Hind")


@OptIn(ExperimentalTextApi::class)
val frauncesFontFamily = FontFamily(
    Font(googleFont = frauncesFont, fontProvider = provider),
    Font(googleFont = frauncesFont, fontProvider = provider, weight = FontWeight.Bold)
)

val hindFontFamily = FontFamily(
    Font(googleFont = hindFont, fontProvider = provider),
    Font(googleFont = hindFont, fontProvider = provider, weight = FontWeight.Bold)
)

@OptIn(ExperimentalTextApi::class)
val jarFontFamily = FontFamily(
    Font(googleFont = interFont, fontProvider = provider),
    Font(googleFont = interFont, fontProvider = provider, weight = FontWeight.Bold),
    Font(googleFont = interFont, fontProvider = provider, weight = FontWeight.SemiBold),
    // Font(googleFont = robotoFont, fontProvider = provider, weight = FontWeight.Bold)
)

val jarInterFontFamily = FontFamily(
    Font(googleFont = interFont, fontProvider = provider)
)

fun h6Fun(textSizeResource: (id: Int) -> TextUnit) = TextStyle(
    fontFamily = jarFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = textSizeResource(R.dimen._14ssp),
    lineHeight = textSizeResource(R.dimen._20ssp)
)

fun h5Fun(textSizeResource: (id: Int) -> TextUnit) = TextStyle(
    fontFamily = jarFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = textSizeResource(R.dimen._16ssp),
    lineHeight = textSizeResource(R.dimen._20ssp)
)
fun h4Fun(textSizeResource: (id: Int) -> TextUnit)  = TextStyle(
    fontFamily = jarFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = textSizeResource(R.dimen._20ssp),
    lineHeight = textSizeResource(R.dimen._24ssp)
)
fun h3Fun(textSizeResource: (id: Int) -> TextUnit)  = TextStyle(
    fontFamily = jarFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = textSizeResource(R.dimen._24ssp),
    lineHeight = textSizeResource(R.dimen._28ssp)
)

fun h2Fun(textSizeResource: (id: Int) -> TextUnit)  = TextStyle(
    fontFamily = jarFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = textSizeResource(R.dimen._28ssp),
    lineHeight = textSizeResource(R.dimen._32ssp)
)

fun h1Fun(textSizeResource: (id: Int) -> TextUnit) = TextStyle(
    fontFamily = jarFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = textSizeResource(R.dimen._32ssp),
    lineHeight = textSizeResource(R.dimen._40ssp)
)

fun largeBodyRegularFun(textSizeResource: (id: Int) -> TextUnit)  = TextStyle(
    fontFamily = jarFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = textSizeResource(R.dimen._16ssp),
    lineHeight = textSizeResource(R.dimen._24ssp)
)

fun bodyBoldFun(textSizeResource: (id: Int) -> TextUnit)  = TextStyle(
    fontFamily = jarFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = textSizeResource(R.dimen._14ssp),
    lineHeight = textSizeResource(R.dimen._20ssp)
)

fun bodySemiBoldFun(textSizeResource: (id: Int) -> TextUnit)  = TextStyle(
    fontFamily = jarFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = textSizeResource(R.dimen._14ssp),
    lineHeight = textSizeResource(R.dimen._20ssp)
)
fun bodyRegularFun(textSizeResource: (id: Int) -> TextUnit)  = TextStyle(
    fontFamily = jarFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = textSizeResource(R.dimen._14ssp),
    lineHeight = textSizeResource(R.dimen._20ssp)
)
fun pBoldFun(textSizeResource: (id: Int) -> TextUnit)  = TextStyle(
    fontFamily = jarFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = textSizeResource(R.dimen._12ssp),
    lineHeight = textSizeResource(R.dimen._12ssp)
)
fun pSemiBoldFun(textSizeResource: (id: Int) -> TextUnit)  = TextStyle(
    fontFamily = jarFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = textSizeResource(R.dimen._12ssp),
    lineHeight = textSizeResource(R.dimen._18ssp)
)
fun pRegularFun(textSizeResource: (id: Int) -> TextUnit)  = TextStyle(
    fontFamily = jarFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = textSizeResource(R.dimen._12ssp),
    lineHeight = textSizeResource(R.dimen._18ssp)
)
fun spRegularFun(textSizeResource: (id: Int) -> TextUnit)  = TextStyle(
    fontFamily = jarFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = textSizeResource(R.dimen._10ssp),
    lineHeight = textSizeResource(R.dimen._14ssp)
)

fun labelFun(textSizeResource: (id: Int) -> TextUnit)  = TextStyle(
    fontFamily = jarFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = textSizeResource(R.dimen._14ssp),
    lineHeight = textSizeResource(R.dimen._14ssp)
)
fun smallLabelFun(textSizeResource: (id: Int) -> TextUnit)  = TextStyle(
    fontFamily = jarFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = textSizeResource(R.dimen._12ssp),
    lineHeight = textSizeResource(R.dimen._12ssp)
)

fun t1Fun(textSizeResource: (id: Int) -> TextUnit) = TextStyle(
    fontFamily = jarFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = textSizeResource(R.dimen._16ssp),
    lineHeight = textSizeResource(R.dimen._20ssp)
)

fun t2Fun(textSizeResource: (id: Int) -> TextUnit) = TextStyle(
    fontFamily = jarFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = textSizeResource(R.dimen._14ssp),
    lineHeight = textSizeResource(R.dimen._18ssp)
)

fun t3Fun(textSizeResource: (id: Int) -> TextUnit) = TextStyle(
    fontFamily = jarFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = textSizeResource(R.dimen._12ssp),
    lineHeight = textSizeResource(R.dimen._16ssp)
)

fun t4Fun(textSizeResource: (id: Int) -> TextUnit) = TextStyle(
    fontFamily = jarFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = textSizeResource(R.dimen._10ssp),
    lineHeight = textSizeResource(R.dimen._12ssp)
)

fun b1Fun(textSizeResource: (id: Int) -> TextUnit) = TextStyle(
    fontFamily = jarFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = textSizeResource(R.dimen._20ssp),
    lineHeight = textSizeResource(R.dimen._28ssp)
)

fun b2Fun(textSizeResource: (id: Int) -> TextUnit) = TextStyle(
    fontFamily = jarFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = textSizeResource(R.dimen._16ssp),
    lineHeight = textSizeResource(R.dimen._24ssp)
)

fun b3Fun(textSizeResource: (id: Int) -> TextUnit) = TextStyle(
    fontFamily = jarFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = textSizeResource(R.dimen._14ssp),
    lineHeight = textSizeResource(R.dimen._22ssp)
)

fun b4Fun(textSizeResource: (id: Int) -> TextUnit) = TextStyle(
    fontFamily = jarFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = textSizeResource(R.dimen._12ssp),
    lineHeight = textSizeResource(R.dimen._20ssp)
)

fun b5Fun(textSizeResource: (id: Int) -> TextUnit) = TextStyle(
    fontFamily = jarFontFamily,
    fontWeight = FontWeight.Normal,
    fontSize = textSizeResource(R.dimen._10ssp),
    lineHeight = textSizeResource(R.dimen._16ssp)
)

internal val LocalJarTypography = staticCompositionLocalOf {
    JarTypographyData(
        h1 = h1,
        h2 = h2,
        h3 = h3,
        h4 = h4,
        h5 = h5,
        h6 = h6,
        t1 = t1,
        t2 = t2,
        t3 = t3,
        t4 = t4,
        b1 = b1,
        b2 = b2,
        b3 = b3,
        b4 = b4,
        b5 = b5,
        largeBodyRegular = largeBodyRegular,
        bodyBold = bodyBold,
        bodySemiBold = bodySemiBold,
        bodyRegular = bodyRegular,
        pBold = pBold,
        pSemiBold = pSemiBold,
        pRegular = pRegular,
        spRegular = spRegular,
        label = label,
        smallLabel = smallLabel,
    )
}
@Immutable
data class JarTypographyData(
    val h1: TextStyle,
    val h2: TextStyle,
    val h3: TextStyle,
    val h4: TextStyle,
    val h5: TextStyle,
    val h6: TextStyle,
    val t1: TextStyle,
    val t2: TextStyle,
    val t3: TextStyle,
    val t4: TextStyle,
    val b1: TextStyle,
    val b2: TextStyle,
    val b3: TextStyle,
    val b4: TextStyle,
    val b5: TextStyle,
    val largeBodyRegular: TextStyle,
    val bodyBold: TextStyle,
    val bodySemiBold: TextStyle,
    val bodyRegular: TextStyle,
    val pBold: TextStyle,
    val pSemiBold: TextStyle,
    val pRegular: TextStyle,
    val spRegular: TextStyle,
    val label: TextStyle,
    val smallLabel: TextStyle,
)
