package com.jar.app.feature_spin.impl

import android.content.Context
import android.os.*
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.RefreshSpinMetaDataEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.livedata.SharedPreferencesUserLiveData
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.encodeUrl
import com.jar.app.base.util.isPresentInBackStack
import com.jar.app.core_analytics.EventKey.Screen
import com.jar.app.core_base.data.dto.QuestDialogContext
import com.jar.app.core_base.data.dto.QuestsDialogData
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_buy_gold_v2.shared.util.BuyGoldV2EventKey.Amount
import com.jar.app.feature_spin.R
import com.jar.app.feature_spin.databinding.FragmentSpinV2Binding
import com.jar.app.feature_spin.impl.custom.SpinWheelV2
import com.jar.app.feature_spin.impl.custom.listeners.SpinWheelListener
import com.jar.app.feature_spin.impl.custom.util.isFlat
import com.jar.app.feature_spin.impl.custom.util.isJackpot
import com.jar.app.feature_spin.impl.custom.util.isOhNo
import com.jar.app.feature_spin.impl.data.models.SpinsContextFlowType
import com.jar.app.feature_spin.impl.ui.GameResultViewModelAndroid
import com.jar.app.feature_spin.shared.util.SpinsEventKeys
import com.jar.app.feature_spin.shared.util.SpinsEventKeys.Back
import com.jar.app.feature_spin.shared.util.SpinsEventKeys.CTA
import com.jar.app.feature_spin.shared.util.SpinsEventKeys.ClickedButtonDailySpinsOverScreen
import com.jar.app.feature_spin.shared.util.SpinsEventKeys.Clicked_QuestSpinsPage
import com.jar.app.feature_spin.shared.util.SpinsEventKeys.FeatureTypeSpinAndWin
import com.jar.app.feature_spin.shared.util.SpinsEventKeys.PullFull
import com.jar.app.feature_spin.shared.util.SpinsEventKeys.PullHalf
import com.jar.app.feature_spin.shared.util.SpinsEventKeys.PulledSpinlever
import com.jar.app.feature_spin.shared.util.SpinsEventKeys.ScreenTypeSpinAndWin
import com.jar.app.feature_spin.shared.util.SpinsEventKeys.ScreenWinnings
import com.jar.app.feature_spin.shared.util.SpinsEventKeys.ShownDailySpinsOverScreen
import com.jar.app.feature_spin.shared.util.SpinsEventKeys.ShownSpinRewardsScreen
import com.jar.app.feature_spin.shared.util.SpinsEventKeys.ShownSpinTheWheelScreen
import com.jar.app.feature_spin.shared.util.SpinsEventKeys.daily_spin_count
import com.jar.app.feature_spin.shared.util.SpinsEventKeys.todays_winnings
import com.jar.app.feature_spin.shared.util.SpinsEventKeys.total_winnings
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class SpinGameFragmentV2 : BaseFragment<FragmentSpinV2Binding>(), SpinWheelListener {

    private val args by navArgs<SpinGameFragmentV2Args>()

    @Inject
    lateinit var serializer: Serializer

    companion object {
        const val WINNING_VIBRATION_DURATION = 500L
        const val SHORT_VIBRATION_DURATION = 50L
        const val QUEST_EXHAUSTED_SPIN = "https://cdn.myjar.app/quest/executedSpin.webp"
        const val QUEST_AVAILABLE_SPIN = "https://cdn.myjar.app/quest/notExecutedSpin.webp"
    }

    private val viewModelProvider by viewModels<GameResultViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private lateinit var spinWheelV2: SpinWheelV2

    @Inject
    lateinit var userLiveData: SharedPreferencesUserLiveData

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private var previousNavColor: Int? = null
    private val vibrator: Vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                requireContext().getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSpinV2Binding
        get() = FragmentSpinV2Binding::inflate


    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                registerQuestBottomSheetListener()
                handleBackPress()
            }
        }

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        viewModel.setFlowContext(SpinsContextFlowType.valueOf(args.context))
        registerBackPressDispatcher()
        setStatusBarColor(R.color.status_bar_color)
        showProgressBar()
        viewModel.onSetup()
        observeLiveData()
        if (SpinsContextFlowType.valueOf(args.context) != SpinsContextFlowType.QUESTS) {
            analyticsHandler
                .postEvent(
                    ShownSpinTheWheelScreen,
                    mapOf(
                        SpinsEventKeys.FeatureType to FeatureTypeSpinAndWin,
                        Screen to ScreenTypeSpinAndWin
                    )
                )
        }
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
        backPressCallback.isEnabled = true
    }


    private fun handleBackPress() {
        if (SpinsContextFlowType.valueOf(args.context) == SpinsContextFlowType.QUESTS) {
            val data = viewModel.spinToWinResponse.value?.data?.data?.spinGameBottomSheet
            val encoded = encodeUrl(
                serializer.encodeToString(
                    QuestsDialogData(
                        title =  data?.header ?: getString(R.string.feature_spin_complete_all_3_spins),
                        subtitle = data?.description ?: getString(R.string.feature_spin_you_still_have_x_spins_remaining, viewModel.spinToWinResponseData?.spinsRemainingToday.orZero()),
                        imagesList = data?.spinsRemainingIcons ?: listOf(),
                        primaryButtonText = getString(R.string.feature_spin_play_more),
                        secondaryButtonText = getString(R.string.feature_spin_go_back_to_quest),
                        context = QuestDialogContext.SPINS.name,
                        chancesLeft = viewModel.spinToWinResponseData?.spinsPerDayLimit.orZero()
                    )
                )
            )
            val internalDeeplink = "android-app://com.jar.app/quest/backBottomSheet/${encoded}"
            navigateTo(internalDeeplink)
        } else {
            EventBus.getDefault().post(RefreshSpinMetaDataEvent())
            popBackStack()
        }
    }

    private fun observeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.spinToWinResponse.collect(
                    onSuccess = {
                        viewModel.spinToWinResponseData = it
                        if (SpinsContextFlowType.valueOf(args.context) == SpinsContextFlowType.QUESTS) {
                            analyticsHandler.postEvent(
                                SpinsEventKeys.Shown_QuestSpinsPage,
                                mapOf(
                                    SpinsEventKeys.spins_left to it.spinsRemainingToday?.orZero().toString()
                                )
                            )
                        }
                        if (SpinsContextFlowType.valueOf(args.context) == SpinsContextFlowType.SPINS)
                            viewModel.fetchGameResult(it.activeGames?.getOrNull(0)?.id)
                        else
                            viewModel.fetchGameResult(null)
                    },
                    onError = { message, _ ->
                        if (::spinWheelV2.isInitialized)
                            spinWheelV2.resetNudge()
                        message.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.spinResponseAndWinningCloseLiveData.collect(
                    onSuccess = {
                        viewModel.gameResultData = it
                        if (::spinWheelV2.isInitialized) {
                            spinWheelV2
                                .setParentViewGroup(binding.root)
                                .setRemainingSpins(
                                    viewModel.spinToWinResponseData?.spinsRemainingToday ?: 0
                                )
                                .setTotalSpins(
                                    viewModel.spinToWinResponseData?.spinsPerDayLimit ?: 0
                                )
                                .setTotalWinnings(
                                    viewModel.spinToWinResponseData?.totalWinningsCta?.value ?: 0
                                )
                                .setWinner(viewModel.gameResultData?.outcome ?: 0)
                                .setGameResult(viewModel.gameResultData!!)
                                .setSpinToWinResponse(viewModel.spinToWinResponseData!!)
                                .setShowAlertNudge(false)
                                .setSpinCallbackFromActivity(this@SpinGameFragmentV2)
                                .build()
                                .apply {
                                    viewModel.gameResultData?.let {
                                        setSpinWheel(it.options, SpinsContextFlowType.valueOf(args.context))
                                    }
                                }
                        } else {
                            viewModel.isSpinResponseAlreadyFetched = true
                            spinWheelV2 = SpinWheelV2(
                                context = requireContext(),
                                attr = null,
                                parentLifecycle = this@SpinGameFragmentV2.lifecycle,
                                uiScope = uiScope
                            )
                                .setParentViewGroup(binding.root)
                                .setRemainingSpins(
                                    viewModel.spinToWinResponseData?.spinsRemainingToday ?: 0
                                )
                                .setTotalSpins(
                                    viewModel.spinToWinResponseData?.spinsPerDayLimit ?: 0
                                )
                                .setTotalWinnings(
                                    viewModel.spinToWinResponseData?.totalWinningsCta?.value ?: 0
                                )
                                .setWinner((viewModel.gameResultData?.outcome) ?: 0)
                                .setGameResult(viewModel.gameResultData!!)
                                .setSpinToWinResponse(viewModel.spinToWinResponseData!!)
                                .setSpinCallbackFromActivity(this@SpinGameFragmentV2)
                                .setIsShowIntroScreen(false)
                                .setShowAlertNudge(viewModel.showAlertNudge())
                                .setActivity(requireActivity())
                                .build()
                                .apply {
                                    viewModel.gameResultData?.let {
                                        setSpinWheel(it.options, SpinsContextFlowType.valueOf(args.context))
                                    }
                                }
                        }
                        dismissProgressBar()
                    },
                    onError = { message, _ ->
                        if (::spinWheelV2.isInitialized)
                            spinWheelV2.resetNudge()
                        message.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.spinCompleteAndExecuteJackpotResponseLiveData.collect(
                    onSuccess = {
                        if (SpinsContextFlowType.valueOf(args.context) == SpinsContextFlowType.QUESTS) {
                            val backStackId = args.backStackId.toInt()
                            if (findNavController().isPresentInBackStack(backStackId)) {
                                findNavController().getBackStackEntry(backStackId).savedStateHandle[BaseConstants.QuestFlowConstants.QUEST_BRAND_COUPON] = Pair(BaseConstants.QuestFlowConstants.QuestType.SPINS, it)
                                popBackStack()
                            }
                            return@collect
                        }
                        if (viewModel.shouldShowJackpot) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                val duration =
                                    WINNING_VIBRATION_DURATION // Longer duration in milliseconds (0.5 seconds)
                                val amplitude = 100 // Lower amplitude
                                val effect = VibrationEffect.createOneShot(duration, amplitude)
                                vibrator.vibrate(effect)
                            } else {
                                @Suppress("DEPRECATION")
                                vibrator.vibrate(500) // Longer duration in milliseconds (0.5 seconds)
                            }
                            viewModel.fetchSpinToWinResponse()
                            viewLifecycleOwner.lifecycleScope.launch {
                                viewModel.winningCloseLiveData.emit(true)
                            }
                            viewModel.shouldShowJackpot = false
                            when (it.rewardType) {
                                com.jar.app.feature_spin.shared.domain.model.CouponType.JAR_COUPON.name -> {
                                    val couponData = it.spinJarCouponOutcomeResponse
                                    navigateTo(
                                        SpinGameFragmentV2Directions.actionSpinGameFragmentV2ToResultJackpot(
                                            resultJackpot = couponData!!
                                        )
                                    )
                                }

                                com.jar.app.feature_spin.shared.domain.model.CouponType.BRAND_COUPON.name -> {
                                    val couponData = it.spinBrandCouponOutcomeResponse!!
                                    navigateTo(
                                        SpinGameFragmentV2Directions.actionSpinGameFragmentV2ToResultBrandJackpot(
                                            resultJackpot = couponData
                                        )
                                    )
                                }
                            }
                        }
                    },
                    onError = { message, _ ->
                        if (::spinWheelV2.isInitialized)
                            spinWheelV2.resetNudge()
                        message.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.showIntroPage.collect(
                    onSuccess = {
                        if (it.enabled == true) {
                            navigateTo(
                                SpinGameFragmentV2Directions.actionSpinGameFragmentV2ToSpinGameIntro(
                                    spinIntro = it
                                )
                            )
                        }
                    },
                    onError = { message, _ ->

                        message.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.useWinningData.collect(
                    onSuccess = {
                        it.popupCta?.let {
                            navigateTo(
                                SpinGameFragmentV2Directions.actionSpinGameFragmentV2ToUseWinning(
                                    useWinning = it
                                )
                            )
                        }
                    },
                    onError = { message, _ ->
                        if (::spinWheelV2.isInitialized)
                            spinWheelV2.resetNudge()
                        message.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.openUseWinnings.collect {
                    EventBus.getDefault().post(
                        HandleDeepLinkEvent(
                            it
                        )
                    )
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.spinCompleteAndExecuteFlatResponseLiveData.collect(
                    onSuccess = {
                        it?.let {
                            viewModel.fetchSpinToWinResponse()
                            if (SpinsContextFlowType.valueOf(args.context) == SpinsContextFlowType.QUESTS) {
                                uiScope.launch {
                                    getString(R.string.feature_spin_sorry_no_winnings_try_again).snackBar(binding.root)
                                }
                            }
                            if (it.outcome?.isOhNo()?.not() == true) {
                                if (viewModel.shouldShowWinningPopUp) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        val duration =
                                            WINNING_VIBRATION_DURATION // Longer duration in milliseconds (0.5 seconds)
                                        val amplitude = 100 // Lower amplitude
                                        val effect =
                                            VibrationEffect.createOneShot(duration, amplitude)
                                        vibrator.vibrate(effect)
                                    } else {
                                        @Suppress("DEPRECATION")
                                        vibrator.vibrate(WINNING_VIBRATION_DURATION) // Longer duration in milliseconds (0.5 seconds)
                                    }
                                    viewModel.shouldShowWinningPopUp = false
                                    spinWheelV2.openWinning(it)
                                }
                            } else {
                                viewLifecycleOwner.lifecycleScope.launch {
                                    viewModel.winningCloseLiveData.emit(true)
                                    onShownWinnings(amount = 0)
                                }
                            }
                        }
                    },
                    onError = { message, _ ->
                        if (::spinWheelV2.isInitialized)
                            spinWheelV2.resetNudge()
                        message.snackBar(binding.root)
                    }
                )
            }
        }
    }

    override fun onSpinComplete(outcome: Int?, spinId: String?) {
        viewModel.spinRotationCompleteLiveData.value = true
    }

    override fun onCloseWinnings() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.winningCloseLiveData.emit(true)
        }
    }

    override fun onUseWinningClicked(deeplink: String) {
        //sending the event
        analyticsHandler.postEvent(
            ClickedButtonDailySpinsOverScreen,
            mapOf(
                CTA to viewModel.spinToWinResponseData?.useWinningsCta?.text!!,
                SpinsEventKeys.FeatureType to FeatureTypeSpinAndWin,
                daily_spin_count to viewModel.spinToWinResponseData?.totalSpinsCta?.value.orZero(),
                todays_winnings to viewModel.spinToWinResponseData?.todayWinnings?.value.orZero(),
                total_winnings to viewModel.spinToWinResponseData?.totalWinningsCta?.value.orZero()
            )
        )
        viewModel.getUseThisWinningData(deeplink)
    }

    override fun onBackIconClicked() {
        if (viewModel.spinToWinResponseData?.showSpinsOverMessage == true) {
            analyticsHandler.postEvent(
                ClickedButtonDailySpinsOverScreen,
                mapOf(
                    CTA to Back,
                    SpinsEventKeys.FeatureType to FeatureTypeSpinAndWin,
                    daily_spin_count to viewModel.spinToWinResponseData?.totalSpinsCta?.value.orZero(),
                    todays_winnings to viewModel.spinToWinResponseData?.todayWinnings?.value.orZero(),
                    total_winnings to viewModel.spinToWinResponseData?.totalWinningsCta?.value.orZero(),
                )
            )
        }
        activity?.onBackPressed()
    }

    override fun onDragCancel() {
        if (SpinsContextFlowType.valueOf(args.context) == SpinsContextFlowType.QUESTS) {
            analyticsHandler.postEvent(
                Clicked_QuestSpinsPage,
                mapOf(
                    SpinsEventKeys.spins_left to viewModel.spinToWinResponseData?.spinsRemainingToday?.orZero().toString(),
                    SpinsEventKeys.button_type to SpinsEventKeys.lever
                )
            )
        }
        analyticsHandler.postEvent(
            PulledSpinlever,
            PullHalf
        )
    }

    override fun onDragComplete(outComeType: String?, outcome: Int?, spinId: String?) {
        viewModel.apply {
            viewLifecycleOwner.lifecycleScope.launch {
                winningCloseLiveData.emit(false)
            }
            spinRotationCompleteLiveData.value = false
            isSpinResponseAlreadyFetched = false
        }

        if (::spinWheelV2.isInitialized) {
            spinWheelV2.let {
                if (SpinsContextFlowType.valueOf(args.context) == SpinsContextFlowType.SPINS) {
                    if (outcome?.isJackpot() == true) {
                        viewModel.shouldShowJackpot = true
                        viewModel.getJackpotDetail(spinId)
                    } else if (outcome?.isFlat() == true) {
                        viewModel.shouldShowWinningPopUp = true
                        viewModel.getFlatWinningDetail(spinId)
                    }
                } else if (SpinsContextFlowType.valueOf(args.context) == SpinsContextFlowType.QUESTS) { // in case of quests
                    if (outComeType == "FLAT") { // Flat show oh no
                        viewModel.shouldShowWinningPopUp = true
                        viewModel.getFlatWinningDetail(spinId)
                    } else {
                        viewModel.shouldShowJackpot = true
                        viewModel.getJackpotDetail(spinId)
                    }
                }
            }
        }
        // sending the event
        if (SpinsContextFlowType.valueOf(args.context) == SpinsContextFlowType.QUESTS) {
            analyticsHandler.postEvent(
                Clicked_QuestSpinsPage,
                mapOf(
                    SpinsEventKeys.spins_left to viewModel.spinToWinResponseData?.spinsRemainingToday?.orZero().toString(),
                    SpinsEventKeys.button_type to SpinsEventKeys.lever
                )
            )
        }
        analyticsHandler.postEvent(
            PulledSpinlever,
            PullFull
        )
    }

    override fun onShownWinnings(amount: Int) {
        analyticsHandler.postEvent(
            ShownSpinRewardsScreen,
            mapOf(
                SpinsEventKeys.FeatureType to FeatureTypeSpinAndWin,
                Screen to ScreenWinnings,
                Amount to amount,
                daily_spin_count to viewModel.spinToWinResponseData?.totalSpinsCta?.value.orZero(),
                todays_winnings to viewModel.spinToWinResponseData?.todayWinnings?.value.orZero(),
                total_winnings to viewModel.spinToWinResponseData?.totalWinningsCta?.value.orZero()
            )
        )
    }

    override fun onShownTotalWinnings() {
        analyticsHandler.postEvent(
            ShownDailySpinsOverScreen,
            mapOf(
                SpinsEventKeys.FeatureType to FeatureTypeSpinAndWin,
                Screen to (viewModel.spinToWinResponseData?.useWinningsCta?.text.orEmpty()),
                daily_spin_count to viewModel.spinToWinResponseData?.totalSpinsCta?.value.orZero(),
                todays_winnings to viewModel.spinToWinResponseData?.todayWinnings?.value.orZero(),
                total_winnings to viewModel.spinToWinResponseData?.totalWinningsCta?.value.orZero()
            )
        )
    }

    override fun onLiverReachedToMax() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val duration = SHORT_VIBRATION_DURATION // Shorter duration in milliseconds
            val effect = VibrationEffect.createOneShot(duration, 200)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(20) // Shorter duration in milliseconds
        }
    }

    private fun setNavigationBarColor(color: Int) {
        previousNavColor = this.activity?.window?.navigationBarColor
        this.activity?.window?.navigationBarColor = color
    }

    override fun onResume() {
        super.onResume()
        context?.let {
            ContextCompat.getColor(it, R.color.status_bar_color)
        }?.let { setNavigationBarColor(it) }
    }

    override fun onStop() {
        super.onStop()
        previousNavColor?.let {
            this.activity?.window?.navigationBarColor = it
        }
    }

    private fun registerQuestBottomSheetListener() {
        setFragmentResultListener(
            BaseConstants.QuestFlowConstants.QUEST_DIALOG_ACTION
        ) { _, bundle ->
            when (bundle.getString(BaseConstants.QuestFlowConstants.DIALOG_ACTION_TYPE)) {
                BaseConstants.QuestFlowConstants.DIALOG_ACTION_GO_TO_QUEST -> {
                    popBackStack()
                }
                else -> {
                    /*Do Nothing*/
                }
            }
        }
    }
}