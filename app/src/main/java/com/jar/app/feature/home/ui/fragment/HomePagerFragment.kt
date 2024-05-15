package com.jar.app.feature.home.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.jar.app.R
import com.jar.app.base.data.event.OpenSetupBuyGoldV2BottomSheetEvent
import com.jar.app.base.data.event.OpenSetupDailyInvestmentBottomSheetV2Event
import com.jar.app.base.data.event.PaymentPageFragmentBackPressEvent
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.databinding.FragmentHomePagerBinding
import com.jar.app.feature.home.ui.activity.HomeActivity
import com.jar.app.feature_homepage.api.data.HomePageApi
import com.jar.app.feature_homepage.shared.domain.event.BottomNavItemChangedEvent
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

@AndroidEntryPoint
class HomePagerFragment : BaseFragment<FragmentHomePagerBinding>() {

    @Inject
    lateinit var homePageApi: HomePageApi

    private var adapter: HomePagerAdapter? = null

    private val list = mutableListOf<Int>()

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (list.isNotEmpty()) {
                    list.removeLast()
                    val top = list.lastOrNull()
                    binding.homeViewPager.currentItem = top ?: HomePagerAdapter.POSITION_HOME
                } else {
                    binding.homeViewPager.currentItem = HomePagerAdapter.POSITION_HOME
                }
            }
        }

    private val pageChangeListener = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            setSelectedTabPosition(position)
            backPressCallback.isEnabled = position != 0

            val elementIndex = list.indexOf(position)
            if (elementIndex != -1) {
                list.removeAt(elementIndex)
                list.add(list.size, position)
            } else {
                list.add(position)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPaymentPageFragmentBackPressEvent(onPaymentPageFragmentBackPressEvent: PaymentPageFragmentBackPressEvent) {
        if (onPaymentPageFragmentBackPressEvent.featureFlow == BaseConstants.SinglePageHomeFeed) {
            if (onPaymentPageFragmentBackPressEvent.whichBottomSheet == BaseConstants.RightBottomSheet) {
                EventBus.getDefault().post(
                    OpenSetupDailyInvestmentBottomSheetV2Event(featureFlow = onPaymentPageFragmentBackPressEvent.featureFlow)
                )
            } else if (onPaymentPageFragmentBackPressEvent.whichBottomSheet == BaseConstants.LeftBottomSheet) {
                EventBus.getDefault().post(
                    OpenSetupBuyGoldV2BottomSheetEvent(featureFlow = onPaymentPageFragmentBackPressEvent.featureFlow)
                )
            }
        }
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentHomePagerBinding
        get() = FragmentHomePagerBinding::inflate

    override fun setupAppBar() {
        //Leave this empty.. Child fragment will handle app bar based on type..
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
        observeLiveData()
        registerBackPressDispatcher()
        checkAppsFlyerIdSync()
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
    }

    private fun setupListeners() {
        binding.homeViewPager.registerOnPageChangeCallback(pageChangeListener)
    }

    private fun setupUI() {
        adapter = HomePagerAdapter(childFragmentManager, viewLifecycleOwner.lifecycle, homePageApi)
        binding.homeViewPager.adapter = adapter
        binding.homeViewPager.isUserInputEnabled = false
        binding.homeViewPager.offscreenPageLimit = 3
        binding.homeViewPager.isSaveEnabled = true
    }

    private fun observeLiveData() {
        findNavController().currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Int>(BaseConstants.TAB_SELECTED,R.id.newHomeFragment)?.observe(viewLifecycleOwner) {
                when (it) {
                    R.id.newHomeFragment -> {
                        binding.homeViewPager.currentItem = HomePagerAdapter.POSITION_HOME
                    }
                    R.id.transactionFragment -> {
                        binding.homeViewPager.currentItem = HomePagerAdapter.POSITION_TRANSACTION
                    }
                    R.id.accountFragment -> {
                        binding.homeViewPager.currentItem = HomePagerAdapter.POSITION_ACCOUNT
                    }
                }
            }
    }

    private fun setSelectedTabPosition(position: Int) {
        EventBus.getDefault().post(
            com.jar.app.feature_homepage.shared.domain.event.BottomNavItemChangedEvent(
                position
            )
        )
    }

    private fun checkAppsFlyerIdSync() {
        (requireActivity() as HomeActivity).syncAppsFlyerUID()
    }

    override fun onDestroyView() {
        binding.homeViewPager.unregisterOnPageChangeCallback(pageChangeListener)
        binding.homeViewPager.adapter = null
        adapter = null
        super.onDestroyView()
    }
}