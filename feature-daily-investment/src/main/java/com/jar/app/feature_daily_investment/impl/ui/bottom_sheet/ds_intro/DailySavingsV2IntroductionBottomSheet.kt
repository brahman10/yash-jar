package com.jar.app.feature_daily_investment.impl.ui.bottom_sheet.ds_intro

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.CacheEvictionUtil
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.feature_daily_investment.databinding.FeatureDailyInvestmentIntroBottomsheetBinding
import com.jar.app.feature_daily_investment.impl.domain.DailySavingsEventKey
import com.jar.app.feature_daily_investment.shared.domain.model.DailyInvestmentIntroData
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class DailySavingsV2IntroductionBottomSheet :
    BaseBottomSheetDialogFragment<FeatureDailyInvestmentIntroBottomsheetBinding>() {

    private val viewModel by viewModels<DailySavingsV2IntroductionViewModel> { defaultViewModelProviderFactory }

    private var adapter: DailySavingsV2IntroductionBottomSheetAdapter? = null

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var cacheEvictionUtil: CacheEvictionUtil

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureDailyInvestmentIntroBottomsheetBinding
        get() = FeatureDailyInvestmentIntroBottomsheetBinding::inflate

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig(
            isCancellable = true,
            isDraggable = true,
        )

    override fun onResume() {
        cacheEvictionUtil.evictHomePageCache()
        super.onResume()
    }

    override fun setup() {
        viewModel.fetchBottomSheetData()
        observeData()
        setupListeners()
    }


    fun setupListeners() {
        binding.btnContinue.setDebounceClickListener {
            analyticsHandler.postEvent(
                DailySavingsEventKey.DailSavings_AbandonBSClicked,
            )
            dismiss()
        }
    }

    private fun setData(data: DailyInvestmentIntroData) {
        binding.tvHeading.text = data.dailySavingIntroScreen.header
        adapter = DailySavingsV2IntroductionBottomSheetAdapter()
        binding.rvPoints.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPoints.adapter = adapter
        adapter?.submitList(data.dailySavingIntroScreen.dailySavingsStepsList)
    }

    private fun observeData() {
        viewModel.bottomSheetLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onSuccess = {
                setData(it)
            },
        )
    }

}