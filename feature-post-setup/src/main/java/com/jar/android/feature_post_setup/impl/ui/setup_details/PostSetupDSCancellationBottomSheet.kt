package com.jar.android.feature_post_setup.impl.ui.setup_details

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jar.android.feature_post_setup.databinding.FeatureStopDsBottomsheetBinding
import com.jar.android.feature_post_setup.impl.ui.setup_details.adapter.PostSetupDSBottomSheetAdapter
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.core_base.data.event.RefreshDailySavingEvent
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_daily_investment.shared.domain.model.PauseDailySavingData
import com.jar.app.feature_daily_investment_cancellation.shared.domain.model.DailyInvestmentPauseDateList
import com.jar.app.feature_daily_investment_cancellation.shared.domain.model.DailyInvestmentPauseDetails
import com.jar.app.feature_post_setup.shared.PostSetupMR
import com.jar.app.feature_user_api.domain.model.PauseSavingOption
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
internal class PostSetupDSCancellationBottomSheet :
    BaseBottomSheetDialogFragment<FeatureStopDsBottomsheetBinding>() {

    private val viewModel: PostSetupDetailsViewModel by viewModels { defaultViewModelProviderFactory }
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureStopDsBottomsheetBinding
        get() = FeatureStopDsBottomsheetBinding::inflate
    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig(
            isHideable = false,
            skipCollapsed = true,
            isCancellable = false,
            isDraggable = false
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchPauseDetailsDataFlow()
    }

    override fun setup() {
        observeFlow()
    }

    var adapter: PostSetupDSBottomSheetAdapter? = null
    @SuppressLint("SetTextI18n")
    private fun setupUi(dailyInvestmentPauseDetails: DailyInvestmentPauseDetails) {
        binding.tvHeader.text = dailyInvestmentPauseDetails.title
        viewModel.totalSelectedDays.value = 1
        adapter = PostSetupDSBottomSheetAdapter(uiScope, onItemClick = {
            viewModel.totalSelectedDays.value = it.noOfDay
        })
        val listOfObjects =
            dailyInvestmentPauseDetails.pauseDaysMap?.entries?.map { (noOfDay, tillDate) ->
                DailyInvestmentPauseDateList(noOfDay, tillDate, false)
            }

        adapter!!.submitList(listOfObjects?.map {
            it.copy(isSelected = it.noOfDay == 1)
        })

        binding.rvBottomSheet.layoutManager =
            LinearLayoutManager(binding.root.context, RecyclerView.VERTICAL, false)
        binding.rvBottomSheet.adapter = adapter
        binding.ivClose.setDebounceClickListener {
            dismiss()
        }

        adapter!!.submitList(listOfObjects?.map {
            it.copy(isSelected = it.noOfDay == viewModel.totalSelectedDays.value)
        })
        if (viewModel.totalSelectedDays.value.orZero() == 1000) {
            binding.tvBottomText.visibility = View.GONE
        } else {
            binding.tvBottomText.visibility = View.VISIBLE
            binding.tvBottomText.text = getCustomString(PostSetupMR.strings.your_savings_will_resume_on) + convertDaysDifferenceToDate(
                viewModel.totalSelectedDays.value.orZero()
            )
        }

        binding.cbStopNow.setDebounceClickListener {
            val pauseDailySavingData = viewModel.totalSelectedDays.value?.let { amount ->
                getPauseSavingData(
                    amount
                )
            }
            if (viewModel.totalSelectedDays.value == 1000) {
                viewModel.disableDailySavings()
            } else {
                viewModel.updateAutoInvestPauseDurationFlow(
                    true,
                    pauseDailySavingData
                )
            }
        }
    }

    private fun convertDaysDifferenceToDate(numberOfDays: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, numberOfDays)

        val date = calendar.time
        return SimpleDateFormat("dd MMM''yy", Locale.getDefault()).format(date).toString()
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.pauseDetailsFlow.collectUnwrapped(
                    onSuccess = {
                        setupUi(it.data)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.updatePauseDurationFlow.collectUnwrapped(
                    onSuccess = {
                        EventBus.getDefault().post(RefreshDailySavingEvent())
                        navigateTo("android-app://com.jar.app/dailySavingPauseProgressScreen/${"pause"}/${viewModel.totalSelectedDays.value}")
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.disableDailySavingFlow.collectUnwrapped(
                    onSuccess = {
                        EventBus.getDefault().post(RefreshDailySavingEvent())
                        navigateTo("android-app://com.jar.app/dailySavingPauseProgressScreen/${"stop"}/${viewModel.totalSelectedDays.value}")
                    }
                )
            }
        }
    }

    private fun getPauseSavingData(numberOfDays: Int): PauseDailySavingData {
        return when (numberOfDays) {
            1 -> PauseDailySavingData(PauseSavingOption.ONE)

            7 -> {
                PauseDailySavingData(PauseSavingOption.WEEK)
            }

            else -> {
                PauseDailySavingData(PauseSavingOption.TWO_WEEKS)
            }
        }
    }
}