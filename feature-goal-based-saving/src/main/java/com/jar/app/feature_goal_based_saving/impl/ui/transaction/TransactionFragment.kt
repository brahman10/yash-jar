package com.jar.app.feature_goal_based_saving.impl.ui.transaction

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.*
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.openWhatsapp
import com.jar.app.core_base.data.event.RefreshDailySavingEvent
import com.jar.app.core_web_pdf_viewer.api.WebPdfViewerApi
import com.jar.app.feature_goal_based_saving.R
import com.jar.app.feature_goal_based_saving.databinding.TransactionFragmentLayoutBinding
import com.jar.app.feature_goal_based_saving.impl.model.GoalBasedSavingActions
import com.jar.app.feature_goal_based_saving.impl.ui.compose.PaymentStatusScreen
import com.jar.app.feature_goal_based_saving.impl.ui.compose.RenderCelebratationLottie
import com.jar.app.feature_goal_based_saving.impl.ui.viewmodels.SubSharedViewModel
import com.jar.app.feature_goal_based_saving.impl.ui.viewmodels.SuperSharedViewModel
import com.jar.app.feature_goal_based_saving.shared.data.model.GoalRecommendedItem
import com.jar.app.feature_goal_based_saving.shared.data.model.ManualPaymentStatus
import dagger.hilt.android.AndroidEntryPoint
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class TransactionFragment : BaseFragment<TransactionFragmentLayoutBinding>() {
    private val args by navArgs<TransactionFragmentArgs>()
    private val viewModel by viewModels<TransactionFragmentViewModel> { defaultViewModelProviderFactory }
    private val sharedViewModel by activityViewModels<SuperSharedViewModel> { defaultViewModelProviderFactory }
    private val subSharedViewModel by activityViewModels<SubSharedViewModel> { defaultViewModelProviderFactory }
    @Inject
    lateinit var webPdfViewerApi: WebPdfViewerApi
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> TransactionFragmentLayoutBinding
        get() = TransactionFragmentLayoutBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        viewModel.handleActions(TransactionFragmentAction.Init(args.goalId))
        EventBus.getDefault().post(
            RefreshGoalBasedSavingEvent()
        )
        sharedViewModel.handleActions(GoalBasedSavingActions.HideAppBar(true))
        subSharedViewModel.handleActions(GoalBasedSavingActions.OnGoalTitleChange(""))
        subSharedViewModel.handleActions(
            GoalBasedSavingActions.OnGoalSelectedFromList(
                GoalRecommendedItem("-1","-1","")
            ))
        subSharedViewModel.handleActions(GoalBasedSavingActions.OnDurationChanged(-1))
        subSharedViewModel.handleActions(GoalBasedSavingActions.OnAmountChanged(""))
        observeState()
        sharedViewModel.state.value.isCallHomeFeedApi.set(true)
    }

    private fun observeState() {
        uiScope.launch {
            launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    viewModel.state.collect() {
                        it.OnData?.let {
                            setupView()
                        }
                    }
                }
            }
            launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    viewModel.loading.collect {
                        if (it) {
                            showProgressBar()
                        } else {
                            dismissProgressBar()
                        }
                    }
                }
            }
            launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    viewModel.onContactUs.collect() {
                        it?.let {
                            activity?.openWhatsapp(
                                it.first,
                                getCustomStringFormatted(
                                    requireContext(),
                                    if (it.second.first)
                                        StringResource(R.string.feature_gbs_im_having_issues_buying_gold_for_x_orderId)
                                    else
                                        StringResource(R.string.feature_gbs_im_having_issues_buying_gold_for_x_goalId),
                                    it.second.second
                                )
                            )
                        }
                    }
                }
            }
            launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    viewModel.onGoToHome?.collect {
                        it?.let {
                            EventBus.getDefault().post(GoToHomeEvent("TransactionFragment"))
                        }
                    }
                }
            }
            launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    viewModel.downloadInvoice?.collect() {
                        it?.let {
                            webPdfViewerApi.openPdf(it)
                        }
                    }
                }
            }
            launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    viewModel.onTrackMyGoal.collect {
                        it?.let {
                            EventBus.getDefault().post(RefreshDailySavingEvent())
                            EventBus.getDefault().post(
                                HandleDeepLinkEvent(it)
                            )
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("StateFlowValueCalledInComposition")
    private fun setupView() {
        with(binding) {
            composeView.setContent {
                Box(Modifier.fillMaxSize()) {
                    PaymentStatusScreen(viewModel)
                    if (ManualPaymentStatus.fromString(viewModel.state.value.OnData?.status) == ManualPaymentStatus.SUCCESS)
                        RenderCelebratationLottie()
                }
            }
        }
    }
}