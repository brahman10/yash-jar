package com.jar.feature_quests.impl.ui.trivia_screen

import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.util.encodeUrl
import com.jar.app.base.util.orFalse
import com.jar.app.core_base.data.dto.QuestDialogContext
import com.jar.app.core_compose_ui.base.BaseComposeFragment
import com.jar.app.core_compose_ui.component.ErrorToastMessage
import com.jar.app.core_compose_ui.theme.JarTypography
import com.jar.app.core_compose_ui.views.RenderBaseToolBar
import com.jar.app.core_ui.R
import com.jar.app.core_base.data.dto.QuestsDialogData
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.feature_quests.impl.util.QuestEventKey
import com.jar.feature_quests.shared.domain.model.QuestionAnswersData
import com.jar.feature_quests.shared.domain.model.SubmitAnswerAction
import com.jar.feature_quests.shared.domain.model.SubmitAnswerData
import com.jar.internal.library.jar_core_network.api.util.Serializer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class QuestTriviaScreenFragment : BaseComposeFragment() {

    @Inject
    lateinit var serializer: Serializer

    private val viewModel by viewModels<QuestTriviaViewModel> { defaultViewModelProviderFactory }

    private var isShownEventSynced = false

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        registerBottomSheetListener()
        observeLiveData()
        getData()
    }

    private fun getData() {
        viewModel.fetchQuizQuestionAnswer()
    }

    @Composable
    override fun RenderScreen() {
        val uiState = viewModel.questTriviaState.collectAsStateWithLifecycle()

        uiState.value.errorMessage?.let { errorMessage ->
            ErrorToastMessage(errorMessage = errorMessage){}
        }
        val questionAnswersData = uiState.value.questionAnswersData.data?.data
        val submitAnswersData = uiState.value.submitAnswerData.data?.data
        val optionSelectedIndex = uiState.value.optionSelectedIndex
        val coroutineScope = rememberCoroutineScope()

        questionAnswersData?.let {
            if (isShownEventSynced.not()) {
                isShownEventSynced = true
                viewModel.fireShownTriviaEvent(it.chancesLeftCount.orZero())
            }
        }

        submitAnswersData?.let {
            viewModel.fireClickedTriviaEvent(
                mapOf(
                    QuestEventKey.Properties.answered to QuestEventKey.Values.yes,
                    QuestEventKey.Properties.chances_left to it.chancesLeftBottomSheet?.chancesLeftCount.orZero().toString(),
                    QuestEventKey.Properties.answer to if (it.ansMarkedCorrectly.orFalse()) QuestEventKey.Values.correct else QuestEventKey.Values.wrong
                )
            )
        }

        Scaffold(
            topBar = {
                RenderBaseToolBar(
                    modifier = Modifier.background(colorResource(id = R.color.bgDarkColor)),
                    onBackClick = {
                        viewModel.fireClickedTriviaEvent(
                            mapOf(
                                QuestEventKey.Properties.answered to QuestEventKey.Values.no,
                                QuestEventKey.Properties.button_type to QuestEventKey.Values.back_button
                            )
                        )
                        popBackStack()
                    }, title = questionAnswersData?.toolbarText.orEmpty())
            },
            backgroundColor = colorResource(id = R.color.bgColor)
        ) { contentPadding ->
            RenderTrivia(
                modifier = Modifier.padding(contentPadding),
                questionAnswersData = questionAnswersData,
                optionSelectedIndex = optionSelectedIndex,
                submitAnswerData = submitAnswersData
            )
        }

        submitAnswersData?.getAction()?.let {
            coroutineScope.launch {
                //Intentional delay to wait before redirecting
                delay(1000)
                when(it) {
                    SubmitAnswerAction.QUIZ_BOTTOM_SHEET -> {
                        val chancesLeftBottomSheet = submitAnswersData.chancesLeftBottomSheet
                        val encoded = encodeUrl(
                            serializer.encodeToString(
                                QuestsDialogData(
                                    title = chancesLeftBottomSheet?.title.orEmpty(),
                                    subtitle = chancesLeftBottomSheet?.description.orEmpty(),
                                    imagesList = chancesLeftBottomSheet?.chancesLeft.orEmpty(),
                                    primaryButtonText = chancesLeftBottomSheet?.tryAgainCta?.title.orEmpty(),
                                    secondaryButtonText = chancesLeftBottomSheet?.questHomeCta?.title.orEmpty(),
                                    context = QuestDialogContext.TRIVIA.name,
                                    chancesLeft = chancesLeftBottomSheet?.chancesLeftCount.orZero()
                                )
                            )
                        )
                        navigateTo("android-app://com.jar.app/quest/backBottomSheet/$encoded")
                    }
                    SubmitAnswerAction.REWARD_POP_UP -> {
                        submitAnswersData.couponResponse?.let { jackpotV2Response ->
                            findNavController().getBackStackEntry(com.jar.app.feature_quests.R.id.dashboardFragment).savedStateHandle[BaseConstants.QuestFlowConstants.QUEST_BRAND_COUPON] = Pair(BaseConstants.QuestFlowConstants.QuestType.QUIZ,jackpotV2Response)
                            popBackStack()
                        }
                    }
                    SubmitAnswerAction.NONE -> {}
                }
            }
        }
    }

    @Composable
    fun RenderTrivia(
        modifier: Modifier,
        questionAnswersData: QuestionAnswersData?,
        optionSelectedIndex: Int?,
        submitAnswerData: SubmitAnswerData?
    ) {
        TriviaScreenCoinBackground(Modifier, questionAnswersData?.quizViewItems) {
            Column(
                modifier = modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )
                RenderQuestion(questionAnswersData = questionAnswersData)

                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                )
                RenderOptions(
                    questionAnswersData = questionAnswersData,
                    optionSelectedIndex = optionSelectedIndex,
                    submitAnswerData = submitAnswerData
                )

                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                )
                RenderFooter(
                    footerText = questionAnswersData?.footerText.orEmpty(),
                    footerChancesLeft = submitAnswerData?.chancesLeftBottomSheet?.chancesLeft
                        ?: questionAnswersData?.footerChancesLeft
                )
            }
        }
    }

    @Composable
    fun RenderQuestion(questionAnswersData: QuestionAnswersData?) {
        Text(
            text = questionAnswersData?.questNumber.orEmpty(),
            textAlign = TextAlign.Start,
            color = Color.White,
            fontWeight = FontWeight(400),
            fontSize = 12.sp
        )
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(12.dp))
        Text(
            text = questionAnswersData?.questionText.orEmpty(),
            style = JarTypography.h1.copy(color = Color.White),
            modifier = Modifier.padding(end = 30.dp)
        )
    }

    @Composable
    fun RenderOptions(questionAnswersData: QuestionAnswersData?, optionSelectedIndex: Int?, submitAnswerData: SubmitAnswerData?) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            itemsIndexed(questionAnswersData?.answerOptions.orEmpty()) { index, option ->
                option?.let {  RenderOptionItem(it, index, optionSelectedIndex, submitAnswerData?.actualCorrectAns) }
            }
        }
    }

    @Composable
    fun RenderFooter(footerText: String, footerChancesLeft: List<String>?) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Text(
                modifier = Modifier.padding(end = 8.dp),
                text = footerText,
                style = JarTypography.body1.copy(color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                items(footerChancesLeft.orEmpty()) {
                    RenderChancesLeftItem(it)
                }
            }
        }
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun RenderChancesLeftItem(imageUrl: String) {
        Column {
            GlideImage(
                model = imageUrl,
                contentDescription = "",
                modifier = Modifier
                    .size(24.dp)
            )
        }
    }

    @Composable
    fun RenderOptionItem(option: String, index: Int, optionSelectedIndex: Int?, correctAnswer: String?) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    viewModel.selectOption(index, option)
                },
            backgroundColor = colorResource(
                id = if (option == correctAnswer.orEmpty()) R.color.color_498D5D
                else if (index == optionSelectedIndex && option != correctAnswer.orEmpty()) R.color.color_43197A
                else R.color.color_7029CC
            ),
            shape = RoundedCornerShape(7.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = option,
                    style = JarTypography.body1.copy(
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                if (index == optionSelectedIndex || option == correctAnswer.orEmpty()) {
                    Image(
                        painter = painterResource(
                            id =
                            if (option == correctAnswer.orEmpty()) R.drawable.core_ui_ic_tick_plain
                            else R.drawable.core_ui_ic_cross_outline
                        ),
                        "",
                        modifier = Modifier
                            .height(14.dp)
                            .width(14.dp),
                        colorFilter = ColorFilter.tint(Color.White),
                        contentScale = ContentScale.FillBounds
                    )
                }
            }
        }
    }

    private fun registerBottomSheetListener() {
        setFragmentResultListener(
            BaseConstants.QuestFlowConstants.QUEST_DIALOG_ACTION
        ) { _, bundle ->
            when (bundle.getString(BaseConstants.QuestFlowConstants.DIALOG_ACTION_TYPE)) {
                BaseConstants.QuestFlowConstants.DIALOG_ACTION_DISMISS -> {
                    getData()
                }
                BaseConstants.QuestFlowConstants.DIALOG_ACTION_GO_TO_QUEST -> {
                    popBackStack(com.jar.app.feature_quests.R.id.dashboardFragment, false)
                }
                else -> {
                    /*Do Nothing*/
                }
            }
        }
    }

    private fun observeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.combinedFlowLoading.collectLatest {
                    if (it) showProgressBar() else dismissProgressBar()
                }
            }
        }
    }

    override fun onDestroyView() {
        isShownEventSynced = false
        super.onDestroyView()
    }
}