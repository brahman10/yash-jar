@file:OptIn(ExperimentalGlideComposeApi::class)

package com.jar.health_insurance.impl.ui.manage_insurance_screen.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_compose_ui.component.ButtonType
import com.jar.app.core_compose_ui.component.JarButton
import com.jar.app.core_compose_ui.component.JarImage
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.utils.convertToAnnotatedString
import com.jar.app.core_compose_ui.views.CircularLayout
import com.jar.app.core_compose_ui.views.LabelAndValueCompose
import com.jar.app.core_compose_ui.views.LabelValueComposeView
import com.jar.app.core_ui.R
import com.jar.app.feature_health_insurance.shared.data.models.benefits.Benefit
import com.jar.app.feature_health_insurance.shared.data.models.manage_screen.InsuranceCTA
import com.jar.app.feature_health_insurance.shared.data.models.manage_screen.InsuranceDataSection
import com.jar.app.feature_health_insurance.shared.data.models.manage_screen.InsuranceTransactionStatus
import com.jar.app.feature_health_insurance.shared.data.models.manage_screen.ViewBenefits

@Composable
fun ManageScreenCardSection(
    sectionData: InsuranceDataSection,
    onViewBenefitButtonClicked: () -> Unit,
    onKycVerifyClicked: (String) -> Unit,
    onButtonClicked: (InsuranceCTA) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 32.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            JarImage(
                modifier = Modifier.size(24.dp),
                imageUrl = sectionData.titleIcon,
                contentDescription = "Title Icon"
            )
            Text(
                modifier = Modifier.padding(start = 12.dp),
                text = sectionData.title.orEmpty(),
                style = JarTypography.h4.copy(color = Color.White)
            )

        }

        Column {
            sectionData.benefits?.let {
                RenderBenefits(benefits = it)
            }
            sectionData.notification?.let { notification ->
                if (notification.showOnTop == true) {
                    Column {
                        NotificationView(
                            modifier = Modifier.padding(top = 16.dp),
                            notification = notification
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        CardContent(sectionData,
                            onViewBenefitButtonClicked,
                            handleClick = {
                                onButtonClicked(it)
                            })
                    }
                } else {
                    Column {
                        Spacer(modifier = Modifier.height(20.dp))
                        CardContent(
                            sectionData,
                            onViewBenefitButtonClicked,
                            handleClick = {
                                onButtonClicked(it)
                            })
                        NotificationView(
                            modifier = Modifier.padding(top = 16.dp),
                            notification = notification
                        )
                    }
                }
            } ?: run {
                Spacer(modifier = Modifier.height(20.dp))
                CardContent(sectionData, onViewBenefitButtonClicked,
                    handleClick = {
                        onButtonClicked(it)
                    })

            }
            sectionData.kyc?.let {
                KycView(
                    modifier = Modifier.padding(top = 16.dp),
                    kycData = it,
                    onButtonClicked = { onKycVerifyClicked(it) })
            }

        }

    }

}

@Composable
fun RenderBenefits(benefits: List<Benefit>) {
    Column {
        benefits.forEach {
            BenefitCardContent(
                modifier = Modifier.padding(top = 32.dp),
                benefit = it
            )
        }
    }

}

@Composable
private fun CardContent(
    section: InsuranceDataSection,
    onViewBenefitButtonClicked: () -> Unit,
    handleClick: (InsuranceCTA) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = colorResource(id = R.color.purple400),
                shape = RoundedCornerShape(12.dp)
            )

    ) {
        if (section.card?.headerText != null || section.card?.headerValue != null || section.card?.status != null) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 24.dp)
                    .height(IntrinsicSize.Min)
            ) {
                section.card?.headerIcon?.let { icon ->
                    JarImage(
                        modifier = Modifier
                            .width(30.dp)
                            .fillMaxHeight(),
                        contentScale = ContentScale.Inside,
                        imageUrl = icon,
                        contentDescription = "null"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Column {
                    section.card?.headerText?.let {
                        Text(
                            text = it,
                            style = JarTypography.body2.copy(color = colorResource(id = R.color.color_ACA1D3))
                        )
                    }
                    section.card?.headerValue?.let {
                        Text(
                            text = it,
                            style = JarTypography.h4.copy(color = Color.White)
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))

                section.card?.txnStatus?.let {
                    InsuranceTransactionStatus(it)
                }

                section.card?.status?.let {
                    InsuranceStatusView(
                        statusColor = section.card?.statusColor,
                        status = section.card?.status,
                        statusIcon = section.card?.statusIcon
                    )
                }
            }

            Divider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 1.dp,
                color = colorResource(id = R.color.color_564A7A)
            )
        }
        section.card?.subHeaderText?.let {
            Text(
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp),
                text = it,
                style = JarTypography.body2.copy(color = Color.White)
            )
        }

        section.card?.communication?.let {
            Column {
                it.forEach {
                    WayToCommunicateCtaItem(
                        modifier = Modifier,
                        ctaData = it.cta,
                        label = it.label,
                        value = it.value,
                        labelIcon = it.labelIcon,
                        handleButtonClicked = {
                            handleClick(it)
                        }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        section.card?.data?.map {
            val textStyle =
                if (it.bold.orFalse()) JarTypography.body2.copy(fontWeight = FontWeight.Bold) else JarTypography.body2.copy(
                    fontSize = 12.sp
                )

            LabelAndValueCompose(
                label = it.label.orEmpty(),
                value = it.value.orEmpty(),
                showCopyToClipBoardIconAndTruncate = false,
                valueIconLink = it.valueIcon,
                valueTextStyle = textStyle.copy(color = Color(android.graphics.Color.parseColor(it.valueColor))),
                labelTextStyle = textStyle.copy(color = Color(android.graphics.Color.parseColor(it.labelColor))),
            )
        }?.let {
            Spacer(modifier = Modifier.height(24.dp))
            LabelValueComposeView(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                list = it
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        section.card?.footer?.let { textData ->
            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                text = convertToAnnotatedString(textList = textData, separator = " ")
            )

        }


        section.card?.viewBenefits?.let { viewBenefitButton ->
            BenefitButton(viewBenefitButton, onViewBenefitButtonClicked)
        }


        section.cta?.let { insuranceCta ->
            JarButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 20.dp),
                onClick = { handleClick(insuranceCta) },
                text = insuranceCta.text,
                buttonType = ButtonType.valueOf(insuranceCta.ctaType)
            )

        }


    }
}

@Composable
fun InsuranceTransactionStatus(
    transactionStatus: InsuranceTransactionStatus
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(
                color = Color(
                    android.graphics.Color.parseColor(
                        transactionStatus.backgroundColor
                    )
                )
            )

    ) {

        Text(
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.Center),
            text = transactionStatus.text,
            style = JarTypography.h6.copy(
                color = Color(
                    android.graphics.Color.parseColor(
                        transactionStatus.textColor
                    )
                ),
                fontSize = 12.sp
            )
        )

    }

}


@Composable
fun BenefitButton(
    viewBenefitButton: ViewBenefits,
    viewBenefitsClicked: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    bottomEnd = 12.dp,
                    bottomStart = 12.dp
                )
            )
            .clipToBounds()
            .background(color = colorResource(id = com.jar.app.core_ui.R.color.color_524577))
            .padding(vertical = 14.dp)
            .debounceClickable {
                viewBenefitsClicked()
            },
        verticalAlignment = Alignment.CenterVertically

    ) {

        Text(
            modifier = Modifier.padding(start = 16.dp),
            text = viewBenefitButton.text,
            style = JarTypography.h6.copy(fontSize = 12.sp, color = Color.White)

        )
        Spacer(modifier = Modifier.weight(1f))
        JarImage(
            modifier = Modifier
                .height(18.dp)
                .padding(end = 16.dp),
            imageUrl = viewBenefitButton.icon,
            contentDescription = "Right arrow",
        )
    }
}

@Composable
@Preview
fun BenefitCardContent(
    modifier: Modifier = Modifier,
    benefit: Benefit = Benefit(
        header = "Cashless Hospitals",
        subText = "Submit health card copy and photo ID card at hospital insurance desk. ",
        id = "1",
        isExpanded = false
    ),
) {

    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.Top
        ) {

            CircularLayout(
                modifier = Modifier
            ) {
                Text(
                    text = benefit.id,
                    color = Color(0xFFACA1D3),
                    modifier = Modifier.padding(3.dp),
                    textAlign = TextAlign.Center

                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 14.dp)
            ) {
                Text(
                    text = benefit.header,
                    color = Color.White,
                    fontSize = 14.sp,
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    text = benefit.subText,
                    color = Color(0xFFACA1D3),
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight(400)

                )

            }
        }
    }
}

@Composable
fun InsuranceStatusView(
    modifier: Modifier = Modifier,
    statusColor: String?,
    status: String?,
    statusIcon: String?
) {
    val color = remember {
        if (statusColor != null) {
            Color(android.graphics.Color.parseColor(statusColor))
        } else {
            Color.Transparent
        }
    }
    Box(
        modifier = modifier
            .border(
                width = 1.dp,
                color = color,
                shape = RoundedCornerShape(4.dp)
            )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            statusIcon?.let { icon ->
                JarImage(
                    modifier = Modifier.size(12.dp),
                    imageUrl = icon,
                    contentDescription = "status icon"
                )
            }
            Spacer(modifier = Modifier.width(4.dp))

            status?.let { status ->
                Text(
                    text = status,
                    style = JarTypography.h6.copy(
                        fontSize = 12.sp,
                        lineHeight = 12.sp,
                        color = color
                    )
                )
            }
        }
    }
}