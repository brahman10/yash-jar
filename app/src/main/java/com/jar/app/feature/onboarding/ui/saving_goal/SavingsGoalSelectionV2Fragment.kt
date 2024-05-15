package com.jar.app.feature.onboarding.ui.saving_goal

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_base.util.orZero
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_remote_config.RemoteConfigParam
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.databinding.FragmentsSavingGoalSelectionV2Binding
import com.jar.app.feature.onboarding.NewOnboardingViewModelAndroid
import com.jar.app.feature_mandate_payment_common.impl.util.PackageManagerUtil
import com.jar.app.feature_onboarding.shared.data.state_machine.OnboardingStateMachine
import com.jar.app.feature_onboarding.shared.domain.model.GoalsV2
import com.jar.app.feature_onboarding.shared.domain.model.SavingGoalsV2Response
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class SavingsGoalSelectionV2Fragment : BaseFragment<FragmentsSavingGoalSelectionV2Binding>() {

    @Inject
    lateinit var onboardingStateMachine: OnboardingStateMachine

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var packageManagerUtil: PackageManagerUtil

    private var isRequiredUpiAppsInstalledAndUpiReady = false

    private var isUpiAppsInstalled = false

    private val upiList = ArrayList<String>()

    private val timeInit = System.currentTimeMillis()

    private val viewModelProvider by viewModels<SavingGoalSelectionViewModelAndroid> { defaultViewModelProviderFactory }
    private var customOnboardingDeeplink: String? = null

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val onboardingViewModelProvider by activityViewModels<NewOnboardingViewModelAndroid>()

    private val onBoardingViewModel by lazy {
        onboardingViewModelProvider.getInstance()
    }

    private val adapter by lazy {
        SavingsGoalSelectionV2Adapter(object : SavingsGoalSelectionV2Adapter.OnItemClickListener {
            override fun onItemClick(goal: GoalsV2) {
               viewModel.updateGoalList(goal)
            }
        })
    }


    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentsSavingGoalSelectionV2Binding
        get() = FragmentsSavingGoalSelectionV2Binding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        getData()
        setupUI()
        setupListeners()
        observeLiveData()
    }

    private fun setupUI() {
        binding.rvGoals.adapter = adapter
        binding.rvGoals.addItemDecorationIfNoneAdded(
            SpaceItemDecoration(
                0.dp,
                8.dp,
                escapeEdges = false,
                orientation = RecyclerView.HORIZONTAL
            )
        )
        binding.rvGoals.layoutManager =
            object :
                GridLayoutManager(requireContext(), 4, RecyclerView.VERTICAL, false) {
                override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
                    lp?.width = width / 4
                    return super.checkLayoutParams(lp)
                }
            }
        shouldEnableNextButton(false)
        analyticsApi.postEvent(
            EventKey.Shown_selectGoalScreen_Onboarding,
            mapOf(EventKey.Action to EventKey.Shown)
        )
        customOnboardingDeeplink = onboardingStateMachine.customOnboardingData?.customOnboardingLink
    }

    private fun setupListeners() {
        binding.btnNext.setDebounceClickListener {
            val goals = adapter.getSelectedGoals()
            viewModel.postSavingGoals(goals)
            analyticsApi.postEvent(
                EventKey.ClickedNext_SelectGoalScreen_Onboarding,
                mapOf(
                    EventKey.Goals to goals.joinToString(),
                    EventKey.number_of_goals_selected to goals.size,
                    EventKey.isRequiredUpiAppsInstalled to isRequiredUpiAppsInstalledAndUpiReady,
                    RemoteConfigParam.SHOULD_BY_PASS_CUSTOM_ONBOARDING_BASED_ON_UPI_APPS.name to remoteConfigApi.shouldByPassCustomOnboardingBasedOnUpiApps(),
                    EventKey.AvailableUpiApps to upiList.joinToString(","),
                    EventKey.customOnboardingDeeplink to customOnboardingDeeplink.orEmpty()
                )
            )
        }
    }

    private fun observeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.savingGoalsV2Flow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        shouldEnableNextButton(false)
                        setDataOnUi(it)
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.savingGoalsPostFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        goToNextScreen()
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.userSavingPrefFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = { it ->
                        dismissProgressBar()
                        it?.savingsGoals?.let { it ->
                            shouldEnableNextButton(it.isNotEmpty())
                        }
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(binding.root)
                    },
                    onSuccessWithNullData = {
                        dismissProgressBar()
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED){
                viewModel.goalsV2ListFlow.collect(){goals->
                    val selectedGoalList = goals?.filter { it.isSelected == true }
                    if(selectedGoalList?.isEmpty() == true){
                        shouldEnableNextButton(false)
                    }else{
                        shouldEnableNextButton(true)
                    }
                    adapter.submitList(goals)
                }
            }
        }
    }

    private fun setDataOnUi(savingsGoalsData: SavingGoalsV2Response) {
        binding.tvTitle.text = savingsGoalsData.savingsGoalsV2Data.question
        adapter.submitList(savingsGoalsData.savingsGoalsV2Data.savingsGoalList)
    }

    private fun goToNextScreen() {
        onboardingStateMachine.navigateAhead()
    }

    private fun getData() {
        viewModel.fetchSavingGoalsV2()
    }

    private fun shouldEnableNextButton(shouldEnable: Boolean = false) {
        binding.btnNext.setDisabled(!shouldEnable)
    }

    override fun onDestroyView() {
        val endTime = System.currentTimeMillis()
        onBoardingViewModel.updateScreenTime(
            screenName = OnboardingStateMachine.State.EnterName,
            timeSpentOnScreen = timeInit.orZero().minus(endTime)
        )
        super.onDestroyView()
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun getInstalledUPIApps(){
        val uri = Uri.parse(String.format("%s://%s", "upi", "mandate"))
        val upiUriIntent = Intent()
        upiUriIntent.data = uri
        val resolveInfoList =
            requireContext().packageManager.queryIntentActivities(
                upiUriIntent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
        val upiPackageNameList = ArrayList<String>()
        for (resolveInfo in resolveInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            upiPackageNameList.add(packageName)
            upiList.add(packageManagerUtil.getAppNameFromPkgName(packageName) ?: packageName)
        }

        isRequiredUpiAppsInstalledAndUpiReady = upiList.isNotEmpty()

        if (isRequiredUpiAppsInstalledAndUpiReady.not() && remoteConfigApi.shouldByPassCustomOnboardingBasedOnUpiApps()) {
            onboardingStateMachine.customOnboardingData = null
        }
    }

}