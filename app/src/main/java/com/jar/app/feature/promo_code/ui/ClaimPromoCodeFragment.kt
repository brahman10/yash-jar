package com.jar.app.feature.promo_code.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.R
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarDefault
import com.jar.app.core_ui.extension.runLayoutAnimation
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.databinding.FragmentClaimPromoCodeBinding
import com.jar.app.feature.promo_code.domain.data.PromoCodeType
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.core_base.util.orZero
import com.jar.app.base.util.textChanges
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.item_decoration.BaseItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@AndroidEntryPoint
internal class ClaimPromoCodeFragment : BaseFragment<FragmentClaimPromoCodeBinding>(),
    BaseItemDecoration.SectionCallback {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentClaimPromoCodeBinding
        get() = FragmentClaimPromoCodeBinding::inflate

    private val timeInit = System.currentTimeMillis()

    private val spaceItemDecoration =
        com.jar.app.core_ui.item_decoration.SpaceItemDecoration(16.dp, 6.dp)

    private var job: Job? = null

    private val viewModel: ClaimPromoCodeViewModel by viewModels()

    private val headerItemDecoration =
        com.jar.app.core_ui.item_decoration.HeaderItemDecoration(this)

    private val baseEdgeEffectFactory = com.jar.app.core_ui.BaseEdgeEffectFactory()

    private val hasAnimatedOnce = AtomicBoolean(false)

    private val promoCodeAdapter by lazy {
        PromoCodeAdapter(uiScope) { _, promoCode ->
            binding.etPromoCode.setText(promoCode.promoCode)
            binding.etPromoCode.setSelection(binding.etPromoCode.text?.length.orZero())
            viewModel.applyPromoCode(
                promoCode.promoCode,
                promoCode.id,
                PromoCodeType.USER_PROMO_CODE.name
            )
            analyticsHandler.postEvent(
                EventKey.ClickedApply_PromoCodeScreen,
                mapOf(
                    EventKey.PromoCode to promoCode.promoCode
                )
            )
        }
    }

    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateAppBarEvent(
            AppBarData(ToolbarDefault(getString(R.string.claim_promo_code), true))
        ))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        getData()
        setupListeners()
        observeLiveData()
        handleBackPress()

        analyticsHandler.postEvent(
            EventKey.Shown_PromoCodeScreen
        )
    }

    private fun handleBackPress() {
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                analyticsHandler.postEvent(EventKey.ClickedBackButton_PromoCodeScreen)
                popBackStack()
            }
        })
    }

    private fun setupUI() {
        toggleApplyButton(false)
        viewModel.fetchPromoCodes()
        binding.rvCoupounCodes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCoupounCodes.adapter = promoCodeAdapter
        binding.rvCoupounCodes.edgeEffectFactory = baseEdgeEffectFactory
        binding.rvCoupounCodes.addItemDecorationIfNoneAdded(
            spaceItemDecoration, headerItemDecoration
        )
    }

    private fun getData() {
        job?.cancel()
        job = viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.fetchPromoCodes().collect {
                    promoCodeAdapter.submitData(it)
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnApply.setDebounceClickListener {
            val promoCode = binding.etPromoCode.text
            analyticsHandler.postEvent(
                EventKey.ClickedApply_PromoCodeScreen,
                mapOf(
                    EventKey.PromoCode to promoCode.toString()
                )
            )
            if (!promoCode.isNullOrBlank()) {
                binding.tvHeaderInvalidCoupon.isVisible = false
                viewModel.applyPromoCode(
                    promoCode = promoCode.toString(),
                    type = PromoCodeType.USER_PROMO_CODE.name
                )
            } else {
                getString(R.string.invalid_promo_code).snackBar(binding.root)
            }
        }

        binding.etPromoCode.textChanges()
            .debounce(300)
            .onEach {
                if (binding.tvHeaderInvalidCoupon.isVisible)
                    binding.tvHeaderInvalidCoupon.isVisible = false
                toggleApplyButton((it?.length ?: 0) > 0)
            }.launchIn(uiScope)
    }

    private fun observeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                promoCodeAdapter.loadStateFlow.collect { loadState ->

                    when (loadState.refresh is LoadState.Loading) {
                        true -> {
                            binding.shimmerPlaceholder.startShimmer()
                        }
                        false -> {
                            val isListEmpty =
                                loadState.refresh is LoadState.NotLoading && promoCodeAdapter.itemCount == 0

                            binding.clEmptyPlaceHolder.isVisible = isListEmpty
                            binding.rvCoupounCodes.isVisible = !isListEmpty

                            binding.shimmerPlaceholder.isVisible = false
                            binding.shimmerPlaceholder.stopShimmer()

                            if (hasAnimatedOnce.getAndSet(true).not()) {
                                binding.rvCoupounCodes.runLayoutAnimation(com.jar.app.core_ui.R.anim.layout_animation_fall_down)
                            }
                        }
                    }
                }
            }
        }

        viewModel.applyCouponCodeLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onLoading = {
                binding.tvHeaderInvalidCoupon.isVisible = false
                showProgressBar()
            },
            onSuccess = {
                binding.tvHeaderInvalidCoupon.isVisible = false
                dismissProgressBar()
                navigateTo(
                    ClaimPromoCodeFragmentDirections.actionClaimPromoCodeFragmentToPromoCodeSuccessFragment(
                        it
                    ),
                    shouldAnimate = true,
                    popUpTo = R.id.homePagerFragment
                )
            },
            onError = {
                binding.tvHeaderInvalidCoupon.isVisible = true
                binding.tvHeaderInvalidCoupon.text = it
                dismissProgressBar()
            },
            translationY = -4.dp.toFloat(),
            suppressError = true
        )
    }

    private fun toggleApplyButton(enable: Boolean) {
        binding.btnApply.alpha = if (enable) 1f else 0.5f
        binding.btnApply.isEnabled = enable
    }

    override fun isItemDecorationSection(position: Int): Boolean {
        return position == 0
    }

    override fun getItemDecorationLayoutRes(position: Int): Int {
        return R.layout.cell_header
    }

    override fun bindItemDecorationData(view: View, position: Int) {
        view.findViewById<AppCompatTextView>(R.id.tvHeader).text =
            getString(R.string.available_promo_codes)
    }

    override fun onDestroyView() {
        analyticsHandler.postEvent(
            EventKey.Exit_PromocodeScreen,
            mapOf(EventKey.TIME_SPENT to System.currentTimeMillis() - timeInit)
        )
        super.onDestroyView()
    }
}