package com.jar.feature_quests.impl.ui.complete_spins

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jar.app.base.util.decodeUrl
import com.jar.app.core_base.data.dto.QuestDialogContext
import com.jar.app.core_base.data.dto.QuestsDialogData
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_compose_ui.base.BaseComposeBottomSheetDialogFragment
import com.jar.app.core_compose_ui.component.ButtonType
import com.jar.app.core_compose_ui.component.JarButton
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.feature_quests.impl.util.QuestEventKey
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
internal class CompleteSpinsDialogFragment : BaseComposeBottomSheetDialogFragment() {

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    private val args by navArgs<CompleteSpinsDialogFragmentArgs>()

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig(
            isCancellable = false,
            isDraggable = false
        )

    private val dialogData by lazy {
        try {
            args.dialogDataString.takeIf { it.isEmpty().not() }?.let {
                return@lazy serializer.decodeFromString<QuestsDialogData>(
                    decodeUrl(it)
                )
            } ?: kotlin.run {
                return@lazy null
            }
        } catch (e: Exception) {
            return@lazy null
        }
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    override fun RenderBottomSheet() {
        Box(modifier = Modifier
            .background(Color.Black.copy(alpha = 0.4f))
            .fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(
                        colorResource(id = com.jar.app.core_ui.R.color.color_2E2942),
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 24.dp)
                    .align(Alignment.BottomCenter)
            ) {
                Image(
                    painter = painterResource(id = com.jar.app.core_ui.R.drawable.core_ui_ic_close),
                    contentDescription = "",
                    modifier = Modifier
                        .height(24.dp)
                        .width(24.dp)
                        .padding(2.dp)
                        .align(Alignment.End)
                        .clickable {
                            postClickedEvent(QuestEventKey.Values.cross)
                            dialogData?.primaryButtonText
                                ?.takeIf { it.isNotEmpty() }
                                ?.let {
                                    dismissBottomSheet()
                                } ?: kotlin.run {
                                navigateToQuestDashboard()
                            }
                        },
                    colorFilter = ColorFilter.tint(colorResource(id = com.jar.app.core_ui.R.color.color_E6F2FF))
                )
                Text(
                    dialogData?.title.orEmpty(), style = JarTypography.h1.copy(textAlign = TextAlign.Center), color = colorResource(
                        id = com.jar.app.core_ui.R.color.color_E6F2FF
                    ), modifier = Modifier
                        .padding(top = 20.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 40.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    for (i in 0 until dialogData?.imagesList?.size.orZero()) {
                        Spacer(Modifier.width(10.dp))
                        GlideImage(
                            model = dialogData?.imagesList?.getOrNull(i).orEmpty(),
                            contentDescription = "",
                            modifier = Modifier
                                .size(55.dp)
                            ,
                            contentScale = ContentScale.Inside
                        )
                    }
                }
                Text(
                    dialogData?.subtitle.orEmpty(),
                    style = JarTypography.body2.copy(textAlign = TextAlign.Center),
                    color = colorResource(
                        id = com.jar.app.core_ui.R.color.color_E6F2FF
                    ),
                    modifier = Modifier
                        .padding(top = 40.dp)
                        .align(Alignment.CenterHorizontally),
                )
                if (dialogData?.primaryButtonText.isNullOrEmpty().not()) {
                    JarButton(
                        text = dialogData?.primaryButtonText.orEmpty(),
                        onClick = {
                            postClickedEvent(dialogData?.primaryButtonText.orEmpty())
                            dismissBottomSheet()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, top = 40.dp, end = 20.dp),
                        buttonType = ButtonType.PRIMARY
                    )
                }
                JarButton(
                    text = dialogData?.secondaryButtonText.orEmpty(),
                    onClick = {
                        postClickedEvent(dialogData?.secondaryButtonText.orEmpty())
                        navigateToQuestDashboard()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 20.dp,
                            top = if (dialogData?.primaryButtonText
                                    .isNullOrEmpty()
                                    .not()
                            ) 20.dp else 40.dp,
                            end = 20.dp
                        ),
                    buttonType = ButtonType.SECONDARY
                )
            }
        }
    }

    private fun dismissBottomSheet() {
        findNavController().navigateUp()
        setFragmentResult(
            BaseConstants.QuestFlowConstants.QUEST_DIALOG_ACTION,
            bundleOf(Pair(BaseConstants.QuestFlowConstants.DIALOG_ACTION_TYPE, BaseConstants.QuestFlowConstants.DIALOG_ACTION_DISMISS))
        )
    }

    private fun navigateToQuestDashboard() {
        findNavController().navigateUp()
        setFragmentResult(
            BaseConstants.QuestFlowConstants.QUEST_DIALOG_ACTION,
            bundleOf(Pair(BaseConstants.QuestFlowConstants.DIALOG_ACTION_TYPE, BaseConstants.QuestFlowConstants.DIALOG_ACTION_GO_TO_QUEST))
        )
    }

    override fun setup() {
        val eventName = if (dialogData?.context.orEmpty() == QuestDialogContext.SPINS.name)
                            QuestEventKey.Events.Shown_QuestSpinsAbandonBS
                        else
                            QuestEventKey.Events.Shown_QuizChanceBS
        analyticsApi.postEvent(
            eventName,
            mapOf(
                getCountEventPropertyName() to getEventCountPropertyValue(),
                QuestEventKey.Properties.buttons to getButtonsShownString()
            )
        )
    }

    private fun postClickedEvent(buttonType: String) {
        val eventName = if (dialogData?.context.orEmpty() == QuestDialogContext.SPINS.name)
                            QuestEventKey.Events.Clicked_QuestSpinsAbandonBS
                        else
                            QuestEventKey.Events.Clicked_QuizChanceBS
        analyticsApi.postEvent(
            eventName,
            mapOf(
                getCountEventPropertyName() to getEventCountPropertyValue(),
                QuestEventKey.Properties.buttons to getButtonsShownString(),
                QuestEventKey.Properties.button_type to buttonType
            )
        )
    }

    private fun getEventCountPropertyValue(): String {
        return dialogData?.chancesLeft?.orZero().toString()
    }

    private fun getCountEventPropertyName(): String {
        return if (dialogData?.context.orEmpty() == QuestDialogContext.SPINS.name)
            QuestEventKey.Properties.spins_left
        else
            QuestEventKey.Properties.chances_left
    }

    private fun getButtonsShownString(): String {
        return if (dialogData?.primaryButtonText != null && dialogData?.secondaryButtonText != null)
            dialogData?.primaryButtonText.orEmpty() + "," + dialogData?.secondaryButtonText.orEmpty()
        else if (dialogData?.primaryButtonText != null)
            dialogData?.primaryButtonText.orEmpty()
        else
            dialogData?.secondaryButtonText.orEmpty()
    }
}