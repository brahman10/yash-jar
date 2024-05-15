package com.jar.app.feature_daily_investment.impl.ui.pause_dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_daily_investment.R
import com.jar.app.feature_daily_investment.databinding.FeatureDailySavingsPauseDailySavingsDialogBinding
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey.days
import com.jar.app.feature_daily_investment.impl.domain.data.IntermediateTransitionScreenArgs
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
internal class PauseDailySavingsBottomSheet :
    BaseBottomSheetDialogFragment<FeatureDailySavingsPauseDailySavingsDialogBinding>() {

    private var adapter: PauseOptionAdapter? = null


    private val viewModelProvider by viewModels<PauseDailySavingsViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }
    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureDailySavingsPauseDailySavingsDialogBinding
        get() = FeatureDailySavingsPauseDailySavingsDialogBinding::inflate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getPauseDailySavingOptions()
    }

    override fun setup() {
        analyticsHandler.postEvent(DailySavingsEventKey.Shown_PauseDailySavingsPopUp)
        setupUI()
        setupListeners()
        observeLiveData()
    }

    private fun setupUI() {
        binding.rvPauseDays.layoutManager =
            GridLayoutManager(context, 4, RecyclerView.VERTICAL, false)
        adapter = PauseOptionAdapter { pauseOption ->
            viewModel.toggleSelection(pauseOption, adapter?.currentList.orEmpty())
        }
        binding.rvPauseDays.adapter = adapter

        binding.lottieView.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            "${BaseConstants.CDN_BASE_URL}/LottieFiles/Generic/tick.json"
        )
    }

    private fun setupListeners() {
        binding.btnCancel.setDebounceClickListener {
            analyticsHandler.postEvent(DailySavingsEventKey.Clicked_Cancel_PauseDailySavingsPopUp)
            dismissAllowingStateLoss()
        }
        binding.ivCross.setDebounceClickListener {
            analyticsHandler.postEvent(DailySavingsEventKey.Clicked_Cancel_PauseDailySavingsPopUp)
            dismissAllowingStateLoss()
        }

        binding.btnPause.setDebounceClickListener {
            if (viewModel.pauseDailySavingsData != null) {
                analyticsHandler.postEvent(
                    DailySavingsEventKey.Clicked_Pause_PauseDailySavingsPopUp,
                    mapOf(days to viewModel.pauseDailySavingsData?.pauseDailySavingsOption?.name.toString())
                )
                viewModel.updateAutoInvestPauseDuration(true, viewModel.pauseDailySavingsData!!)
            }
        }

        binding.root.setDebounceClickListener {
            dismissAllowingStateLoss()
        }
    }

    private fun observeLiveData() {

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.updatePauseDurationFlow.collect(
                    onLoading = { showProgressBar() },
                    onSuccess = {
                        dismissProgressBar()
                        analyticsHandler.postEvent(DailySavingsEventKey.Shown_Success_PauseDailySavingsPopUp)
                        dismissAllowingStateLoss()
                        navigateTo(
                            PauseDailySavingsBottomSheetDirections.toIntermediateTransitionFragment(
                                IntermediateTransitionScreenArgs(
                                    R.drawable.feature_daily_investment_ic_ds_paused,
                                    getString(R.string.feature_daily_investment_daily_saving_paused),
                                    getString(
                                        R.string.feature_daily_investment_we_will_not_save_next_s,
                                        it.getPauseSavingNumericValue(it.pausedFor)
                                    ),
                                    false,
                                )
                            )
                        )
                    },
                    onError = { errorMessage,_->
                        dismissProgressBar()
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.pauseOptionsFlow.collect {
                    adapter?.submitList(it.data)
                }
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.isSelectedFlow.collect {
                    binding.btnPause.setDisabled(it == null)
                    viewModel.pauseDailySavingsData = it
                }
            }
        }
    }

    override val bottomSheetConfig: BottomSheetConfig
        get() = DEFAULT_CONFIG
}