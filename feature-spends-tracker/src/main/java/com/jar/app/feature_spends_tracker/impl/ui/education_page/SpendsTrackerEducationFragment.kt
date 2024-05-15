package com.jar.app.feature_spends_tracker.impl.ui.education_page

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.dp
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.feature_spends_tracker.databinding.FragmentSpendsTrackerEducationScreenBinding
import com.jar.app.feature_spends_tracker.shared.domain.model.spends_education.SpendsEducationData
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import com.jar.app.feature_spends_tracker.shared.domain.events.SpendsTrackerEvent
import com.jar.app.feature_spends_tracker.shared.MR

import javax.inject.Inject

@AndroidEntryPoint
internal class SpendsTrackerEducationFragment :
    BaseFragment<FragmentSpendsTrackerEducationScreenBinding>() {

    companion object {
        private const val SPAN_COUNT = 2
    }

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val viewModel by viewModels<SpendsEducationViewModel> {
        defaultViewModelProviderFactory
    }
    private var educationInfoAdapter: EducationInfoAdapter? = null
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSpendsTrackerEducationScreenBinding
        get() = FragmentSpendsTrackerEducationScreenBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        analyticsHandler.postEvent(
            SpendsTrackerEvent.ST_educationscreenshown,
        )
        setupUI()
        observeLiveData()
        setupListeners()

    }

    private fun setupListeners() {
        binding.btnViewReport.setDebounceClickListener {
            analyticsHandler.postEvent(
                SpendsTrackerEvent.ST_educationscreenclicked,
            )
            navigateTo(
                SpendsTrackerEducationFragmentDirections.actionSpendsTrackerEducationFragmentToSpendsTrackerMainPage()
            )
        }
        binding.ivBack.setDebounceClickListener {
            popBackStack()
        }

    }

    private fun observeLiveData() {

        viewModel.spendsEducationDataLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onSuccess = { info ->
                info?.let {
                    setupEducationScreen(it)
                }
            },
            onSuccessWithNullData = {
                navigateTo(SpendsTrackerEducationFragmentDirections.actionSpendsTrackerEducationFragmentToSpendsTrackerMainPage())
            })
    }

    private fun setupEducationScreen(educationData: SpendsEducationData) {
        educationInfoAdapter?.submitList(educationData.spendsTrackerEducationInfo)
        binding.apply {
            tvIntoText.text = educationData.introducingText
            tvIntoTitle.text = educationData.header
            Glide.with(binding.root)
                .load(educationData.privacyInfoIcon)
                .override(16.dp)
                .into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        tvdisclaimerText.setCompoundDrawablesWithIntrinsicBounds(
                            resource,
                            null,
                            null,
                            null
                        )
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
            tvdisclaimerText.text = educationData.privacyInfoString
            privacyInfoLayout.setBackgroundColor(Color.parseColor(educationData.infoBGColor))

            Glide.with(binding.root)
                .asDrawable()
                .load(educationData.spendsTrackerIcon)
                .into(bgImage)
        }
    }

    private fun setupUI() {

        viewModel.fetchSpendsEducationData()

        educationInfoAdapter = EducationInfoAdapter()
        binding.tvHeaderText.text = getCustomString(MR.strings.spent_tracker)
        binding.rvInfo.apply {
            layoutManager =
                StaggeredGridLayoutManager(SPAN_COUNT, StaggeredGridLayoutManager.VERTICAL)
            adapter = educationInfoAdapter
        }
    }
}



