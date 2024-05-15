package com.jar.health_insurance.impl.ui.select_insurance_plan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.jar.app.base.util.decodeUrl
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_compose_ui.base.BaseComposeBottomSheetDialogFragment
import com.jar.app.core_compose_ui.component.JarImage
import com.jar.app.core_compose_ui.component.JarPrimaryButton
import com.jar.app.core_compose_ui.component.JarSecondaryButton
import com.jar.app.core_compose_ui.component.debounceClickable
import com.jar.app.core_compose_ui.utils.convertToAnnotatedString
import com.jar.app.feature_health_insurance.shared.data.models.landing1.AbandonScreenBenefits
import com.jar.app.feature_health_insurance.shared.data.models.select_premium.SelectPlanReturnScreen
import com.jar.internal.library.jar_core_network.api.util.Serializer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SelectPlanAbandonBottomSheet : BaseComposeBottomSheetDialogFragment() {

    @Inject
    lateinit var serializer: Serializer

    private val args by navArgs<SelectPlanAbandonBottomSheetArgs>()

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig()

    @Composable
    override fun RenderBottomSheet() {
        args.returnScreenData?.let {encodedUrl ->
            decodeUrl(encodedUrl).let {
                serializer.decodeFromString<SelectPlanReturnScreen>(
                    it
                )
            }
        }?.let {
            AbandonScreenBottomSheetView(
                returnScreen = it
            )
        }
    }

    override fun setup() {

    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun AbandonScreenBottomSheetView(
        modifier: Modifier = Modifier,
        returnScreen: SelectPlanReturnScreen
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                    color = Color(0xff2D2940)
                )
        ) {
            returnScreen.crossIcon?.let {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    JarImage(
                        imageUrl = it,
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .debounceClickable {
                                popBackStack()
                            }
                    )
                }
            }
            Spacer(modifier = Modifier.padding(top = 25.dp))
            returnScreen.headerText?.let {
                Text(
                    text = convertToAnnotatedString(it, " "),
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp)
                )
            }
            returnScreen.subText?.let {
                Text(
                    text = convertToAnnotatedString(it, " "),
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp)
                )
            }
            Spacer(modifier = Modifier.height(36.dp))

            returnScreen.benefitsList?.let { benefitList ->
                benefitList.forEachIndexed { index, benefit ->
                    BenefitView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp),
                        benefit = benefit
                    )
                    if(index != benefitList.size - 1){
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))
            returnScreen.getQuoteCTAText?.let {
                JarPrimaryButton(
                    text = it,
                    isAllCaps = false,
                    onClick = {
                        popBackStack()
                    },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
                )
            }
            returnScreen.exitCTAText?.let {
                JarSecondaryButton(
                    text = it,
                    isAllCaps = false,
                    onClick = {
                        popBackStack(com.jar.app.feature_health_insurance.R.id.selectHealthInsurancePlanScreen, true)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }


    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun BenefitView(
        modifier: Modifier = Modifier,
        benefit: AbandonScreenBenefits? = null
    ) {
        benefit?.let { benefitItem ->
            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                benefitItem.icon?.let {
                    JarImage(
                        imageUrl = it,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 20.dp)
                            .size(32.dp)
                    )
                }
                benefitItem.text?.let {
                    Text(
                        text = convertToAnnotatedString(it, " ")
                    )
                }
            }
        }
    }
}