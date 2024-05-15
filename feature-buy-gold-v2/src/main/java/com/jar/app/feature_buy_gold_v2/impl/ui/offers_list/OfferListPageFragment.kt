package com.jar.app.feature_buy_gold_v2.impl.ui.offers_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_buy_gold_v2.R
import com.jar.app.feature_buy_gold_v2.databinding.FragmentOfferListPageBinding
import com.jar.app.feature_buy_gold_v2.shared.MR
import com.jar.app.feature_buy_gold_v2.shared.domain.event.RewardsEventKey
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class OfferListPageFragment : BaseFragment<FragmentOfferListPageBinding>() {
    companion object {
        private const val OfferListPageFragment = "OfferListPageFragment"
    }

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private lateinit var adapter: CouponsViewPagerAdapter

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                popBackStack(R.id.offerListPageFragment, true)
            }
        }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentOfferListPageBinding
        get() = FragmentOfferListPageBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
        registerBackPressDispatcher()
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
    }

    private fun setupListeners() {
        binding.ivBack.setDebounceClickListener {
            popBackStack(R.id.offerListPageFragment, true)
        }

    }
    private fun setupUI() {
        binding.tvHeaderText.text = getCustomString(MR.strings.offers)
        adapter = CouponsViewPagerAdapter(requireActivity())
        binding.couponsViewPager.adapter = adapter

        binding.couponsViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> {
                        analyticsHandler.postEvent(
                            RewardsEventKey.Shown_rewards_Section,
                            mapOf(
                                RewardsEventKey.Tab to RewardsEventKey.Jar,
                            )
                        )
                    }

                    1 -> {
                        analyticsHandler.postEvent(
                            RewardsEventKey.Shown_rewards_Section,
                            mapOf(
                                RewardsEventKey.Tab to RewardsEventKey.Other_Brand,
                            )
                        )
                    }
                }

                val radioButton = binding.couponTypeRadioGroup.getChildAt(position) as RadioButton
                radioButton.isChecked = true
            }
        })

        binding.couponTypeRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = group.findViewById<RadioButton>(checkedId)
            val index = group.indexOfChild(radioButton)
            binding.couponsViewPager.currentItem = index
        }
        /*
         Experiment by @Samiksha to set the default page selected as the Brand Page,to be removed in later release if experiment does not workout
         */
        binding.couponsViewPager.post {
            binding.couponsViewPager.currentItem = BRAND_COUPON_INDEX // Set the default selection to position 1
            binding.couponTypeRadioGroup.check(binding.couponTypeRadioGroup.getChildAt(BRAND_COUPON_INDEX).id)
        }

    }
}
private const val BRAND_COUPON_INDEX = 1