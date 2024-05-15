package com.jar.app.feature_vasooli.impl.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.core_base.util.orZero
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_ui.BaseEdgeEffectFactory
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_vasooli.R
import com.jar.app.feature_vasooli.databinding.FragmentVasooliHomeBinding
import com.jar.app.feature_vasooli.impl.domain.VasooliEventKey
import com.jar.app.feature_vasooli.impl.ui.VasooliViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject
import kotlin.math.abs

@AndroidEntryPoint
internal class VasooliHomeFragment : BaseFragment<FragmentVasooliHomeBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var prefs: PrefsApi

    private val viewModel by viewModels<VasooliHomeViewModel> { defaultViewModelProviderFactory }

    private val vasooliViewModel by viewModels<VasooliViewModel> { defaultViewModelProviderFactory }

    private var mAppBarLastOffset = 0

    private val offsetChangedListener =
        AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            mAppBarLastOffset = verticalOffset
            val proportion =
                (abs(verticalOffset.toFloat()) / appBarLayout.totalScrollRange.toFloat())
            updateTitleBackground(proportion)
        }

    private var adapter: BorrowerAdapter? = null

    private val baseEdgeEffectFactory = BaseEdgeEffectFactory()

    private val spaceItemDecoration =
        com.jar.app.core_ui.item_decoration.SpaceItemDecoration(0.dp, 8.dp)

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentVasooliHomeBinding
        get() = FragmentVasooliHomeBinding::inflate

    private var isShownEventPosted = false

    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateAppBarEvent(AppBarData(ToolbarNone))
        )
    }

    override fun onResume() {
        super.onResume()
        binding.appBarLayout.addOnOffsetChangedListener(offsetChangedListener)
        val proportion =
            (abs(mAppBarLastOffset.toFloat()) / binding.appBarLayout.totalScrollRange.toFloat())
        updateTitleBackground(proportion)
    }

    override fun onPause() {
        super.onPause()
        binding.appBarLayout.removeOnOffsetChangedListener(offsetChangedListener)
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        observeLiveData()
        getData()
        initClickListeners()
    }

    private fun setupUI() {
        setupToolbar()
        adapter = BorrowerAdapter {
            analyticsHandler.postEvent(
                VasooliEventKey.VasooliHome.Clicked_Vasooli_Homescreen,
                mapOf(
                    VasooliEventKey.Button to VasooliEventKey.Card,
                    VasooliEventKey.PhoneNumber to it.borrowerPhoneNumber.orEmpty()
                )
            )
            navigateTo(
                VasooliHomeFragmentDirections.actionVasooliHomeFragmentToVasooliDetailsFragment(it.loanId)
            )
        }
        binding.rvBorrowers.edgeEffectFactory = baseEdgeEffectFactory
        binding.rvBorrowers.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.rvBorrowers.adapter = adapter
        binding.rvBorrowers.layoutManager = LinearLayoutManager(context)
    }

    private fun setupToolbar() {
        binding.toolbar.tvTitle.text = getString(R.string.feature_vasooli_vasool_it)

        binding.toolbar.root.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(), com.jar.app.core_ui.R.color.bgColor
            )
        )

        binding.toolbar.ivEndImage.isVisible = false
        binding.toolbar.lottieView.isVisible = false
        binding.toolbar.separator.isVisible = false
        binding.toolbar.ivEndImage.isVisible = false

        binding.toolbar.btnBack.setDebounceClickListener {
            analyticsHandler.postEvent(
                VasooliEventKey.VasooliHome.Clicked_Vasooli_Homescreen,
                mapOf(
                    VasooliEventKey.Button to VasooliEventKey.Back
                )
            )
            popBackStack()
        }
    }

    private fun initClickListeners() {
        binding.btnFab.setDebounceClickListener {
            analyticsHandler.postEvent(
                VasooliEventKey.VasooliHome.Clicked_Vasooli_Homescreen,
                mapOf(
                    VasooliEventKey.Button to VasooliEventKey.AddNew
                )
            )
            navigateTo(
                VasooliHomeFragmentDirections.actionVasooliHomeFragmentToVasooliEntryFragment(
                    null,
                    null
                ),
                shouldAnimate = true
            )
        }
    }

    private fun observeLiveData() {
        val weakReference: WeakReference<View> = WeakReference(binding.root)

        viewModel.vasooliOverviewLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            weakReference,
            onSuccess = {
                binding.tvLendDue.text =
                    getString(R.string.feature_vasooli_currency_sign_x_int, it.totalDue?.orZero())
                binding.tvLendTotal.text = getString(
                    R.string.feature_vasooli_total_udhaar,
                    it.totalLent?.orZero().toString()
                )
                binding.clDetails.isVisible = true
                binding.shimmerOverview.isVisible = false
                binding.shimmerOverview.stopShimmer()
            },
            onError = {
                binding.shimmerOverview.isVisible = false
                binding.shimmerOverview.stopShimmer()
            }
        )

        viewModel.loansListLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            weakReference,
            onSuccess = {
                sendShownEvent(it.isEmpty())
                if (it.isEmpty()) {
                    binding.shimmerRecycler.isVisible = false
                    binding.shimmerRecycler.stopShimmer()
                    binding.emptyLayout.root.isVisible = true
                } else {
                    prefs.setShouldShowVasooliIntro(value = false)
                    adapter?.submitList(it)
                    binding.shimmerRecycler.stopShimmer()
                    binding.shimmerRecycler.isVisible = false
                    binding.rvBorrowers.isVisible = true
                }
            },
            onSuccessWithNullData = {
                sendShownEvent()
                binding.shimmerRecycler.isVisible = false
                binding.shimmerRecycler.stopShimmer()
                binding.emptyLayout.root.isVisible = true
            },
            onError = {
                sendShownEvent()
                binding.shimmerRecycler.isVisible = false
                binding.shimmerRecycler.stopShimmer()
            }
        )

        vasooliViewModel.networkStateLiveData.observe(viewLifecycleOwner) {
            binding.toolbar.clNetworkContainer.isSelected = it
            binding.toolbar.tvInternetConnectionText.text =
                if (it) getString(com.jar.app.core_ui.R.string.core_ui_we_are_back_online) else getString(
                    com.jar.app.core_ui.R.string.core_ui_no_internet_available_please_try_again)
            binding.toolbar.tvInternetConnectionText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                if (it) com.jar.app.core_ui.R.drawable.ic_wifi_on else com.jar.app.core_ui.R.drawable.ic_wifi_off, 0, 0, 0
            )
            if (it) {
                if (binding.toolbar.networkExpandableLayout.isExpanded) {
                    uiScope.launch {
                        delay(500)
                        binding.toolbar.networkExpandableLayout.collapse(true)
                    }
                }
            } else {
                binding.toolbar.networkExpandableLayout.expand(true)
            }
        }
    }

    private fun getData() {
        binding.shimmerRecycler.startShimmer()
        binding.shimmerOverview.startShimmer()
        binding.clDetails.isVisible = false
        binding.rvBorrowers.isVisible = false
        binding.emptyLayout.root.isVisible = false
        binding.shimmerRecycler.isVisible = true
        binding.shimmerOverview.isVisible = true
        viewModel.fetchVasooliOverview()
        viewModel.fetchLoansList()
    }

    private fun updateTitleBackground(proportion: Float) {
        when {
            proportion >= 1 -> {

                binding.tvBorrowerTitle.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        com.jar.app.core_ui.R.color.bgColor
                    )
                )
            }
            else -> {
                binding.tvBorrowerTitle.background = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.feature_vasooli_bg_upper_rounded_dark_16dp
                )
            }
        }
    }

    private fun sendShownEvent(isEmpty: Boolean = true) {
        if (!isShownEventPosted) {
            isShownEventPosted = true
            analyticsHandler.postEvent(
                VasooliEventKey.Shown_Screen_Vasooli,
                mapOf(
                    VasooliEventKey.Screen to VasooliEventKey.Vasooli_Home,
                    VasooliEventKey.IsEmpty to if (isEmpty) VasooliEventKey.Yes else VasooliEventKey.No
                )
            )
        }
    }
}