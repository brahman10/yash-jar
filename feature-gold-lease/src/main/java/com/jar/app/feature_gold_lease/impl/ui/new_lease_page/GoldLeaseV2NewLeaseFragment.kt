package com.jar.app.feature_gold_lease.impl.ui.new_lease_page

import android.animation.Animator
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.base.util.orFalse
import com.jar.app.base.util.setHtmlText
import com.jar.app.core_base.util.BaseAppDeeplink
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.feature_gold_lease.R
import com.jar.app.feature_gold_lease.databinding.FragmentGoldLeaseV2NewLeaseBinding
import com.jar.app.feature_gold_lease.shared.ui.GoldLeaseV2NewLeaseViewModel
import com.jar.app.feature_gold_lease.shared.util.GoldLeaseConstants
import com.jar.app.feature_gold_lease.shared.util.GoldLeaseEventKey
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject


@AndroidEntryPoint
internal class GoldLeaseV2NewLeaseFragment : BaseFragment<FragmentGoldLeaseV2NewLeaseBinding>(){

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    companion object {
        private const val EXTRA_FLOW_TYPE = "EXTRA_FLOW_TYPE"
        private const val EXTRA_IS_NEW_LESE_USER = "EXTRA_IS_NEW_LESE_USER"

        fun newInstance(flowType: String, isNewLeaseUser: Boolean): GoldLeaseV2NewLeaseFragment {
            val fragment = GoldLeaseV2NewLeaseFragment()
            val bundle = Bundle()
            bundle.putString(EXTRA_FLOW_TYPE, flowType)
            bundle.putBoolean(EXTRA_IS_NEW_LESE_USER, isNewLeaseUser)
            fragment.arguments = bundle
            return fragment
        }
    }

    private val flowTypeFromBundle by lazy {
        requireArguments().getString(EXTRA_FLOW_TYPE)
    }

    private val isNewLeaseUserFromBundle by lazy {
        requireArguments().getBoolean(EXTRA_IS_NEW_LESE_USER)
    }

    private val viewModelProvider by viewModels<GoldLeaseV2NewLeaseViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val spaceItemDecorationOfferings = SpaceItemDecoration(8.dp, 8.dp)

    private val spaceItemDecorationComparison = SpaceItemDecoration(0.dp, 8.dp)

    private var goldLeaseUpperComparisonAdapter: GoldLeaseComparisonAdapter? = null

    private var goldLeaseLowerComparisonAdapter: GoldLeaseComparisonAdapter? = null

    private var goldLeaseOfferingsAdapter: GoldLeaseOfferingsAdapter? = null

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentGoldLeaseV2NewLeaseBinding
        get() = FragmentGoldLeaseV2NewLeaseBinding::inflate

    private var jobScrollViewAnimation: Job? = null


    private var scrollDownObjectAnimator: ObjectAnimator? = null
    private var scrollDownAnimationListener = object: Animator.AnimatorListener{
        override fun onAnimationStart(animation: Animator) {}
        override fun onAnimationEnd(animation: Animator) {
            scrollUpObjectAnimator = ObjectAnimator.ofInt(
                binding.nsvContent,
                "scrollY", 0
            )
            scrollUpObjectAnimator?.duration = 1000
            scrollUpObjectAnimator?.interpolator = AccelerateDecelerateInterpolator()
            scrollUpObjectAnimator?.addListener(scrollUpAnimationListener)
            scrollUpObjectAnimator?.start()
        }
        override fun onAnimationCancel(animation: Animator) {
            binding.nsvContent.setScrollingEnabled(true)
        }
        override fun onAnimationRepeat(animation: Animator) {}
    }

    private var scrollUpObjectAnimator: ObjectAnimator? = null
    private var scrollUpAnimationListener = object : Animator.AnimatorListener{
        override fun onAnimationStart(animation: Animator) {}
        override fun onAnimationEnd(animation: Animator) {
            binding.nsvContent.setScrollingEnabled(true)
        }
        override fun onAnimationCancel(animation: Animator) {
            binding.nsvContent.setScrollingEnabled(true)
        }
        override fun onAnimationRepeat(animation: Animator) {}
    }

    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateAppBarEvent(
                AppBarData(
                    ToolbarNone
                )
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getData()
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        observeLiveData()
        setupListeners()

        analyticsHandler.postEvent(
            GoldLeaseEventKey.GoldLeaseNewLeaseScreen.Lease_InfoScreenShown,
            mapOf(
                GoldLeaseEventKey.Properties.FROM_FLOW to getFlowType()
            )
        )
    }

    private fun setupUI() {
        binding.lottieViewArrow.cancelAnimation()
        binding.lottieViewArrow.playLottieWithUrlAndExceptionHandling(
            requireContext(), GoldLeaseConstants.LottieUrls.NEW_LEASE_ARROW
        )

        goldLeaseLowerComparisonAdapter = GoldLeaseComparisonAdapter()
        goldLeaseUpperComparisonAdapter = GoldLeaseComparisonAdapter()
        goldLeaseOfferingsAdapter = GoldLeaseOfferingsAdapter { info, position ->
            analyticsHandler.postEvent(
                GoldLeaseEventKey.GoldLeaseNewLeaseScreen.Lease_InfoScreenClicked,
                mapOf(
                    GoldLeaseEventKey.Properties.USER_TYPE to if (isNewLeaseUser()) GoldLeaseEventKey.Values.NEW else GoldLeaseEventKey.Values.REPEAT,
                    GoldLeaseEventKey.Properties.ELEMENT_TYPE_RANDOM to GoldLeaseEventKey.Values.OFFERINGS_TILE,
                    GoldLeaseEventKey.Properties.TITLE to info.description.orEmpty(),
                    GoldLeaseEventKey.Properties.POSITION to  position,
                    GoldLeaseEventKey.Properties.FROM_FLOW to getFlowType()
                )
            )
        }

        binding.rvUspIntentTiles.adapter = goldLeaseOfferingsAdapter
        binding.rvUspIntentTiles.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvUspIntentTiles.addItemDecorationIfNoneAdded(spaceItemDecorationOfferings)

        binding.layoutGoldLeaseComparison.rvLowerComparisonList.adapter = goldLeaseLowerComparisonAdapter
        binding.layoutGoldLeaseComparison.rvLowerComparisonList.layoutManager = LinearLayoutManager(requireContext())
        binding.layoutGoldLeaseComparison.rvLowerComparisonList.addItemDecorationIfNoneAdded(spaceItemDecorationComparison)

        binding.layoutGoldLeaseComparison.rvUpperComparisonList.adapter = goldLeaseUpperComparisonAdapter
        binding.layoutGoldLeaseComparison.rvUpperComparisonList.layoutManager = LinearLayoutManager(requireContext())
        binding.layoutGoldLeaseComparison.rvUpperComparisonList.addItemDecorationIfNoneAdded(spaceItemDecorationComparison)
    }

    private fun getData() {
        viewModel.fetchLandingDetails()
    }

    private fun setupListeners() {
        binding.btnGetStarted.setDebounceClickListener {
            analyticsHandler.postEvent(
                GoldLeaseEventKey.GoldLeaseNewLeaseScreen.Lease_InfoScreenClicked,
                mapOf(
                    GoldLeaseEventKey.Properties.USER_TYPE to if (isNewLeaseUser()) GoldLeaseEventKey.Values.NEW else GoldLeaseEventKey.Values.REPEAT,
                    GoldLeaseEventKey.Properties.BUTTON_TYPE to GoldLeaseEventKey.Values.GET_STARTED,
                    GoldLeaseEventKey.Properties.FROM_FLOW to getFlowType()
                )
            )
            navigateTo(
                "${BaseAppDeeplink.GoldLease.GOLD_LEASE_PLANS_SCREEN}/${getFlowType()}/${isNewLeaseUser()}"
            )
        }

        binding.layoutGoldLeaseComparison.indicatorGold.setDebounceClickListener {
            analyticsHandler.postEvent(
                GoldLeaseEventKey.GoldLeaseNewLeaseScreen.Lease_InfoScreenClicked,
                mapOf(
                    GoldLeaseEventKey.Properties.USER_TYPE to if (isNewLeaseUser()) GoldLeaseEventKey.Values.NEW else GoldLeaseEventKey.Values.REPEAT,
                    GoldLeaseEventKey.Properties.ELEMENT_TYPE_RANDOM to GoldLeaseEventKey.Values.GOLD,
                    GoldLeaseEventKey.Properties.FROM_FLOW to getFlowType()
                )
            )
        }

        binding.layoutGoldLeaseComparison.indicatorGoldX.setDebounceClickListener {
            analyticsHandler.postEvent(
                GoldLeaseEventKey.GoldLeaseNewLeaseScreen.Lease_InfoScreenClicked,
                mapOf(
                    GoldLeaseEventKey.Properties.USER_TYPE to if (isNewLeaseUser()) GoldLeaseEventKey.Values.NEW else GoldLeaseEventKey.Values.REPEAT,
                    GoldLeaseEventKey.Properties.ELEMENT_TYPE_RANDOM to GoldLeaseEventKey.Values.GOLDX,
                    GoldLeaseEventKey.Properties.FROM_FLOW to getFlowType()
                )
            )
        }
    }

    private fun getFlowType() = flowTypeFromBundle.orEmpty()

    private fun isNewLeaseUser() = isNewLeaseUserFromBundle.orFalse()

    private fun observeLiveData() {
        val weakRef: WeakReference<View> = WeakReference(binding.root)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.goldLeaseLandingDetailsFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccessWithNullData = {
                        dismissProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it?.let {
                            setDataInUI(it)
                        }
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(weakRef.get()!!)
                    }
                )
            }
        }
    }

    private fun setDataInUI(goldLeaseLandingDetails: com.jar.app.feature_gold_lease.shared.domain.model.GoldLeaseLandingDetails) {
        binding.tvLandingTitle.setHtmlText(goldLeaseLandingDetails.primaryTitle.orEmpty())
        binding.tvLandingSubTitle.setHtmlText(goldLeaseLandingDetails.primarySubtitle.orEmpty())
        binding.tvLandingSecondaryTitle.setHtmlText(goldLeaseLandingDetails.secondaryTitle.orEmpty())
        binding.tvSocialPresence.setHtmlText(goldLeaseLandingDetails.socialProofText.orEmpty())
        binding.btnGetStarted.setText(goldLeaseLandingDetails.ctaText ?: getString(R.string.feature_gold_lease_get_started))

        goldLeaseLandingDetails.goldLeaseComparisonTable?.leaseComparisonTableRowsList?.let {
            val (upperList, lowerList) = it.partition { it.rowPlacement == com.jar.app.feature_gold_lease.shared.domain.model.LeaseComparisonRowPlacement.UPPER.name }
            goldLeaseUpperComparisonAdapter?.submitList(upperList)
            goldLeaseLowerComparisonAdapter?.submitList(lowerList)
        }

        binding.layoutGoldLeaseComparison.tvEarningsDescription.setHtmlText(goldLeaseLandingDetails.goldLeaseComparisonTable?.socialProofText.orEmpty())
        binding.layoutGoldLeaseComparison.tvEarningsValue.setHtmlText(goldLeaseLandingDetails.goldLeaseComparisonTable?.earningsText.orEmpty())

        goldLeaseLandingDetails.leaseBasicInfoTileList?.let {
            goldLeaseOfferingsAdapter?.submitList(it)
        }

        binding.nsvContent.isVisible = true
        binding.clCtaAndSocialProof.isVisible = true

        if (prefs.getIsGoldLeaseLandingAnimationShow().not()) {
            prefs.setIsGoldLeaseLandingAnimationShow(true)
            startScrollViewAnimation()
        }
    }

    private fun startScrollViewAnimation() {
        jobScrollViewAnimation?.cancel()
        jobScrollViewAnimation = uiScope.launch {
            binding.nsvContent.setScrollingEnabled(false)
            scrollDownObjectAnimator = ObjectAnimator.ofInt(
                binding.nsvContent,
                "scrollY", 80.dp
            )
            scrollDownObjectAnimator?.duration = 2000
            scrollDownObjectAnimator?.interpolator = AccelerateDecelerateInterpolator()
            scrollDownObjectAnimator?.addListener(scrollDownAnimationListener)

            scrollDownObjectAnimator?.start()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.nsvContent.setScrollingEnabled(true)
    }

    override fun onDestroyView() {
        jobScrollViewAnimation?.cancel()
        scrollDownObjectAnimator?.removeListener(scrollDownAnimationListener)
        scrollUpObjectAnimator?.removeListener(scrollUpAnimationListener)
        super.onDestroyView()
    }
}