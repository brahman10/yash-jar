package com.jar.app.feature_vasooli.impl.ui.intro

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.PagerSnapHelper
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.doRepeatingTask
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.extension.attachSnapHelperWithListener
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.listener.OnSnapPositionChangeListener
import com.jar.app.feature_vasooli.R
import com.jar.app.feature_vasooli.databinding.FragmentVasooliIntroBinding
import com.jar.app.feature_vasooli.impl.domain.VasooliEventKey
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class VasooliIntroFragment : BaseFragment<FragmentVasooliIntroBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val viewModel by viewModels<VasooliIntroViewModel> { defaultViewModelProviderFactory }

    private var adapter: IntroAdapter? = null

    private var layoutManager: LinearLayoutManager? = null

    private var autScrollJob: Job? = null

    companion object {
        private const val AUTO_SCROLL_INTERVAL = 5000L
        private const val MILLISECONDS_PER_INCH = 85f
    }

    private val smoothScroller by lazy {
        object : LinearSmoothScroller(requireContext()) {
            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                return MILLISECONDS_PER_INCH / displayMetrics.densityDpi
            }
        }
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentVasooliIntroBinding
        get() = FragmentVasooliIntroBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        observeLiveData()
        initClickListeners()
        analyticsHandler.postEvent(
            VasooliEventKey.Shown_Screen_Vasooli,
            mapOf(
                VasooliEventKey.Screen to VasooliEventKey.Carousal
            )
        )
    }

    private fun initClickListeners() {
        binding.btnVasoolIt.setDebounceClickListener {
            analyticsHandler.postEvent(
                VasooliEventKey.Intro.Clicked_CarousalScreen_Vasooli,
                mapOf(
                    VasooliEventKey.Button to VasooliEventKey.Start
                )
            )
            navigateToVasooliHome()
        }
    }

    private fun setupUI() {
        setupToolbar()
        adapter = IntroAdapter()
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvIntro.layoutManager = layoutManager
        binding.rvIntro.adapter = adapter
        binding.rvIntro.attachSnapHelperWithListener(
            PagerSnapHelper(),
            onSnapPositionChangeListener = object :
                OnSnapPositionChangeListener {
                override fun onSnapPositionChange(position: Int) {
                    toggleUI(position)
                }
            })
        startAutoScrollTimer()
    }

    private fun setupToolbar() {
        binding.toolbar.tvEnd.text = getString(R.string.feature_vasooli_skip)

        binding.toolbar.ivEndImage.isVisible = false
        binding.toolbar.lottieView.isVisible = false
        binding.toolbar.separator.isVisible = false
        binding.toolbar.ivEndImage.isVisible = false
        binding.toolbar.tvTitle.isVisible = false

        binding.toolbar.tvEnd.setDebounceClickListener {
            val currentPosition = layoutManager?.findFirstVisibleItemPosition() ?: 0
            analyticsHandler.postEvent(
                VasooliEventKey.Intro.Clicked_CarousalScreen_Vasooli,
                mapOf(
                    VasooliEventKey.Button to VasooliEventKey.Skip,
                    VasooliEventKey.FromScreen to (currentPosition+1).toString()
                )
            )
            navigateToVasooliHome()
        }

        binding.toolbar.btnBack.setDebounceClickListener {
            popBackStack()
        }
    }

    private fun toggleUI(position: Int) {
        binding.toolbar.tvEnd.isVisible = position != viewModel.size - 1
        binding.btnVasoolIt.isInvisible = position != viewModel.size - 1
    }

    private fun navigateToVasooliHome() {
        navigateTo(
            "android-app://com.jar.app/vasooliHomeFragment",
            popUpTo = R.id.vasooliIntroFragment,
            inclusive = true
        )
    }

    private fun observeLiveData() {
        viewModel.introListLiveData.observe(this) {
            adapter?.submitList(it)
        }
    }

    private fun startAutoScrollTimer() {
        cancelAutoScrollJob()
        autScrollJob = lifecycleScope.launch {
            doRepeatingTask(repeatInterval = AUTO_SCROLL_INTERVAL) {
                moveToItem()
            }
        }
    }

    private fun moveToItem() {
        var currentPosition = layoutManager?.findFirstVisibleItemPosition() ?: 0
        val finalPosition = ++currentPosition
        smoothScroller.targetPosition = finalPosition
        layoutManager?.startSmoothScroll(smoothScroller)

        if(currentPosition == viewModel.size - 1) {
            cancelAutoScrollJob()
        }
    }

    private fun cancelAutoScrollJob() {
        autScrollJob?.cancel()
    }

    override fun onDestroyView() {
        layoutManager = null
        cancelAutoScrollJob()
        super.onDestroyView()
    }
}