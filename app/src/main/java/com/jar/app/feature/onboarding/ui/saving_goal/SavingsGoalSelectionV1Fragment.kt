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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.R
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.dp
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_base.util.orZero
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_remote_config.RemoteConfigParam
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.databinding.FragmentSavingGoalSelectionV1Binding
import com.jar.app.feature.onboarding.NewOnboardingViewModelAndroid
import com.jar.app.feature_mandate_payment_common.impl.util.PackageManagerUtil
import com.jar.app.feature_onboarding.shared.data.state_machine.OnboardingStateMachine
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class SavingsGoalSelectionV1Fragment : BaseFragment<FragmentSavingGoalSelectionV1Binding>() {

    @Inject
    lateinit var onboardingStateMachine: OnboardingStateMachine

    private var isRequiredUpiAppsInstalledAndUpiReady = false

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    @Inject
    lateinit var remoteConfigApi: RemoteConfigApi

    @Inject
    lateinit var packageManagerUtil: PackageManagerUtil

    @Inject
    lateinit var prefsApi: PrefsApi

    private val timeInit = System.currentTimeMillis()

    private val viewModelProvider by viewModels<SavingGoalSelectionViewModelAndroid> { defaultViewModelProviderFactory }
    private val upiList = ArrayList<String>()
    private var customOnboardingDeeplink: String? = null

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val onboardingViewModelProvider by activityViewModels<NewOnboardingViewModelAndroid>()

    private val onBoardingViewModel by lazy {
        onboardingViewModelProvider.getInstance()
    }

    private val adapter by lazy { SavingGoalsRecyclerAdapter() }


    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSavingGoalSelectionV1Binding
        get() = FragmentSavingGoalSelectionV1Binding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
        observeLiveData()
        getData()
    }
    private fun setupUI() {
        shouldEnableNextButton(false)
        binding.stepsTv.text = getString(
            R.string.x_y_steps_to_complete,
            onboardingStateMachine.getTotalOnboardingSteps(),
            onboardingStateMachine.getTotalOnboardingSteps()
        )
        binding.rvSavingGoal.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSavingGoal.adapter = adapter
        binding.rvSavingGoal.addItemDecoration(
            SpaceItemDecoration(0.dp, 6.dp, RecyclerView.VERTICAL, true)
        )
        analyticsApi.postEvent(
            EventKey.Shown_selectGoalScreen_Onboarding,
            mapOf(EventKey.Action to EventKey.Shown)
        )
        customOnboardingDeeplink = onboardingStateMachine.customOnboardingData?.customOnboardingLink
        getInstalledUPIApps()
    }

    private fun setupListeners() {
        adapter.setGoalsClickListener {
            val selectedSize = adapter.getSelectedGoals().size
            shouldEnableNextButton(selectedSize > 0)
            analyticsApi.postEvent(EventKey.Click_GoalScreen_Onboarding)
        }
        binding.btnNext.setDebounceClickListener {
            val goals = adapter.getSelectedGoals()
            val selectedGoals = goals.map { it.title }
            viewModel.postSavingGoals(selectedGoals)
            analyticsApi.postEvent(
                EventKey.ClickedNext_SelectGoalScreen_Onboarding,
                mapOf(
                    EventKey.Goals to selectedGoals.joinToString(),
                    EventKey.number_of_goals_selected to selectedGoals.size,
                    EventKey.isRequiredUpiAppsInstalled to isRequiredUpiAppsInstalledAndUpiReady,
                    RemoteConfigParam.SHOULD_BY_PASS_CUSTOM_ONBOARDING_BASED_ON_UPI_APPS.name to remoteConfigApi.shouldByPassCustomOnboardingBasedOnUpiApps(),
                    EventKey.AvailableUpiApps to upiList.joinToString(","),
                    EventKey.customOnboardingDeeplink to customOnboardingDeeplink.orEmpty()
                )
            )
        }
        binding.btnBack.setDebounceClickListener {
            analyticsApi.postEvent(
                EventKey.BackButtonPressed,
                mapOf(EventKey.FromScreen to EventKey.SavingGoalOnboarding)
            )
            onboardingStateMachine.navigateBack()
        }
    }
    private fun observeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.savingGoalsFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        setDataOnUi(it.savingsGoalsData)
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
                    onSuccess = {
                        dismissProgressBar()
                        it?.savingsGoals?.let {
                            val selectedGoals = it.map { it.title }
                            adapter.updateSelectedGoals(selectedGoals)
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


    }

    private fun setDataOnUi(savingsGoalsData: com.jar.app.feature_onboarding.shared.domain.model.SavingsGoalsData) {
        binding.tvQuestion.text = getString(
            R.string.hey_user_saving_for_message,
            getFirstName().orEmpty(),
            savingsGoalsData.question
        )
        binding.tvAnswer.text = savingsGoalsData.answer
        adapter.submitList(savingsGoalsData.savingsGoalList)
    }

    private fun goToNextScreen() {
        onboardingStateMachine.navigateAhead()
    }

    private fun getData() {
        viewModel.fetchSavingGoals()
    }

    private fun shouldEnableNextButton(shouldEnable: Boolean = false) {
        binding.btnNext.setDisabled(!shouldEnable)
    }

    private fun getFirstName(): String? {
        return prefsApi.getUserName()?.split(" ")?.firstOrNull()
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