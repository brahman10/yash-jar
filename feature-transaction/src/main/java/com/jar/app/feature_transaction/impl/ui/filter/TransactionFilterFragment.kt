package com.jar.app.feature_transaction.impl.ui.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.feature_transaction.R
import com.jar.app.feature_transaction.databinding.FeatureTransactionFragmentTransactionFilterBinding
import com.jar.app.feature_transaction.impl.ui.TransactionFragmentViewModelAndroid
import com.jar.app.feature_transaction.impl.ui.filter.adapter.FilterKeyAdapter
import com.jar.app.feature_transaction.impl.ui.filter.adapter.FilterValueAdapter
import com.jar.app.feature_transaction.impl.ui.filter.adapter.SelectedOptionAdapter
import com.jar.app.feature_transaction.shared.util.TransactionConstants
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
class TransactionFilterFragment :
    BaseFragment<FeatureTransactionFragmentTransactionFilterBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    companion object {
        private const val TAG = "#TransactionFilterFragment#"
    }

    private val viewModelProvider by activityViewModels<TransactionFragmentViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private var spaceItemDecoration = SpaceItemDecoration(4.dp, 4.dp)

    private var keyAdapter: FilterKeyAdapter? = null
    private var valueAdapter: FilterValueAdapter? = null
    private var selectedAdapter: SelectedOptionAdapter? = null

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureTransactionFragmentTransactionFilterBinding
        get() = FeatureTransactionFragmentTransactionFilterBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
        setupObserver()
        getData()
    }

    private fun setupUI() {
        keyAdapter = FilterKeyAdapter {
            analyticsHandler.postEvent(
                TransactionConstants.AnalyticsKeys.Clicked_FilterParameter_FilterScreen,
                mapOf(EventKey.PROP_VALUE to it.displayName)
            )
            viewModel.setFilterKeySelection(it.name)
        }
        binding.rvFilterKey.adapter = keyAdapter

        valueAdapter = FilterValueAdapter {
            analyticsHandler.postEvent(
                TransactionConstants.AnalyticsKeys.Clicked_FilterValue_FilterScreen,
                mapOf(
                    EventKey.PROP_VALUE to it.displayName,
                    EventKey.TYPE to it.keyName
                )
            )
            it.let { valueData ->
                if (valueData.keyName.equals(BaseConstants.FilterValues.DATE_FILTER, true)
                    && valueData.name.equals(BaseConstants.FilterValues.DATE_FILTER_CUSTOM, true)
                )
                    showDatePicker(valueData)
                else
                    viewModel.setFilterValueSelection(it.name)
            }
        }
        binding.rvFilterValue.adapter = valueAdapter

        selectedAdapter = SelectedOptionAdapter {
            analyticsHandler.postEvent(
                TransactionConstants.AnalyticsKeys.Removed_FilterValue_FilterScreen,
                mapOf(
                    EventKey.PROP_VALUE to it.displayName
                )
            )
            viewModel.removeFilterSelection(it)
        }
        binding.rvSelectedFilters.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.rvSelectedFilters.adapter = selectedAdapter
        analyticsHandler.postEvent(TransactionConstants.AnalyticsKeys.Shown_FilterScreen_GoldTransactionScreen)
    }

    private fun setupListeners() {
        binding.ivBack.setDebounceClickListener {
            popBackStack()
        }

        binding.btnYes.setDebounceClickListener {
            viewModel.onApplyClicked()
            analyticsHandler.postEvent(
                TransactionConstants.AnalyticsKeys.Clicked_Apply_FilterScreen
            )
            popBackStack()
        }
        binding.btnNo.setDebounceClickListener {
            viewModel.onClearClicked()
            analyticsHandler.postEvent(
                TransactionConstants.AnalyticsKeys.Clicked_Clear_FilterScreen
            )
        }
    }

    private fun setupObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.filterResponseLiveData.collect(
                    onLoading = { showProgressBar() },
                    onSuccess = { dismissProgressBar() },
                    onError = { _, _ ->
                        dismissProgressBar()
                        popBackStack()
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.filterKeyLiveData.collectLatest {
                    it?.let {
                        keyAdapter?.submitList(it)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.filterValuesLiveData.collectLatest {
                    it?.let {
                        valueAdapter?.submitList(it)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.selectedFiltersLiveData.collectLatest {
                    it?.let {
                        selectedAdapter?.submitList(it)
                    }
                }
            }
        }
    }

    private fun getData() {
        viewModel.initTempList()
        viewModel.fetchFilters(
            getAllFilterString = {
                getString(R.string.feature_transaction_all)
            }
        )
    }

    private fun showDatePicker(valueData: com.jar.app.feature_transaction.shared.domain.model.FilterValueData) {
        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointBackward.now())

        val materialDatePicker =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTheme(com.jar.app.core_ui.R.style.ThemeOverlay_App_DatePicker)
                .setSelection(
                    androidx.core.util.Pair(
                        MaterialDatePicker.thisMonthInUtcMilliseconds(),
                        MaterialDatePicker.todayInUtcMilliseconds()
                    )
                )
                .setCalendarConstraints(constraintsBuilder.build())
                .build()

        materialDatePicker.addOnPositiveButtonClickListener {
            viewModel.setFilterValueSelection(valueData.name, Pair(it.first, it.second))
        }
        materialDatePicker.show(childFragmentManager, TAG)
    }
}