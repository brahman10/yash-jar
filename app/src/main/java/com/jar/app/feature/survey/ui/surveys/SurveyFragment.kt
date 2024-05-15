package com.jar.app.feature.survey.ui.surveys

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.jar.app.R
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.databinding.FragmentSurveyBinding
import com.jar.app.feature.survey.domain.model.SurveyQuestion
import com.jar.app.feature.survey.ui.SubmitSurveyFragment
import com.jar.app.feature.survey.ui.SurveyViewModel
import com.jar.app.feature.survey.ui.mcq.McqFragment
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_base.util.orZero
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class SurveyFragment : BaseBottomSheetDialogFragment<FragmentSurveyBinding>() {

    override val bottomSheetConfig = BottomSheetConfig(
        isHideable = false,
        shouldShowFullHeight = true,
        isCancellable = true
    )

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSurveyBinding
        get() = FragmentSurveyBinding::inflate

    private var surveySize = 0
    private var surveyPagerAdapter: SurveyPagerAdapter? = null
    private val viewModel by activityViewModels<SurveyViewModel> { defaultViewModelProviderFactory }

    private var selectedPagePosition = 0

    private var viewPagerPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            selectedPagePosition = position
            updateViewsOnPageChange(position)
            super.onPageSelected(position)
        }
    }

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private var surveyQuestions: List<SurveyQuestion>? = null

    override fun setup() {
        getData()
        setupUI()
        setupListeners()
        observeLiveData()
    }

    private fun getData() {
        viewModel.fetchSurvey()
    }

    private fun setupUI() {
        surveyPagerAdapter = SurveyPagerAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
        binding.viewPager.adapter = surveyPagerAdapter
        TabLayoutMediator(binding.tabIndicator, binding.viewPager)
        { tab, _ ->
            tab.view.isClickable = false
        }.attach()
        binding.viewPager.isUserInputEnabled = false
    }

    private fun setupListeners() {
        binding.btnSubmit.setDebounceClickListener {
            if (selectedPagePosition < surveyQuestions?.size.orZero()) {
                when {
                    viewModel.getSurveyListSize() == selectedPagePosition.plus(1) -> {
                        viewModel.submitQuestion(selectedPagePosition, true)
                        binding.viewPager.setCurrentItem(selectedPagePosition + 1, true)
                    }
                    selectedPagePosition == surveyPagerAdapter?.itemCount?.minus(1) -> {
                        dismiss()
                    }
                    else -> {
                        viewModel.submitQuestion(selectedPagePosition)
                        if (viewModel.userChoicesMap.containsKey(
                                surveyQuestions?.get(selectedPagePosition)?.id
                            )
                        ) {
                            analyticsHandler.postEvent(
                                EventKey.CLICKED_SURVEY_OPTION,
                                mapOf(
                                    EventKey.SurveyQuestion to surveyQuestions?.get(
                                        selectedPagePosition
                                    )?.question as Any,
                                    EventKey.SurveyAnswer to viewModel.userChoicesMap[surveyQuestions?.get(
                                        selectedPagePosition
                                    )?.id] as Any
                                )
                            )
                        }
                        viewModel.getSurveyDataByPosition(selectedPagePosition + 1)
                        binding.viewPager.setCurrentItem(selectedPagePosition + 1, true)
                    }
                }
            } else {
                dismiss()
            }
        }

        binding.viewPager.registerOnPageChangeCallback(viewPagerPageChangeCallback)

        binding.ivClose.setDebounceClickListener {
            dismiss()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeLiveData() {
        viewModel.surveyLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onSuccess = {
                binding.shimmerPlaceholder.stopShimmer()
                binding.shimmerPlaceholder.isVisible = false
                surveySize = it?.surveyQuestions?.size ?: 0
                it?.surveyQuestions?.forEachIndexed { index, _ ->
                    surveyPagerAdapter?.fragments?.add(McqFragment.newInstance(position = index))
                }
                surveyPagerAdapter?.fragments?.add(SubmitSurveyFragment())
                surveyPagerAdapter?.notifyDataSetChanged()
                viewModel.getSurveyDataByPosition(0)

                surveyQuestions = it?.surveyQuestions

                analyticsHandler.postEvent(
                    EventKey.SHOWN_SURVEY,
                    mapOf(EventKey.SurveyQuestion to it?.surveyQuestions?.toString().orEmpty())
                )
            }
        )

        viewModel.submitSurveyLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onSuccess = {
                analyticsHandler.postEvent(
                    EventKey.ClICKED_SUBMIT_SURVEY,
                    mapOf(
                        EventKey.SurveyQuestion to (surveyQuestions?.map { it.question }) as Any,
                        EventKey.SurveyAnswer to viewModel.userChoicesMap.map { it.value } as Any
                    )
                )
            }
        )
    }

    private fun updateViewsOnPageChange(position: Int) {
        binding.viewPager.setCurrentItem(position, true)
        surveyPagerAdapter?.let { adapter ->
            binding.tvQuestionNumber.text =
                getString(R.string.question_d_by_d, position + 1, viewModel.getSurveyListSize())
            when (position) {
                adapter.itemCount - 1 -> {
                    binding.btnSubmit.setText(getString(R.string.okay))
                    binding.tvQuestionNumber.isVisible = false
                }
                adapter.itemCount - 2 -> {
                    binding.btnSubmit.setText(getString(R.string.submit))
                }
                else -> {
                    binding.btnSubmit.setText(getString(R.string.next))
                }
            }
        }
    }

    override fun onDestroyView() {
        binding.viewPager.unregisterOnPageChangeCallback(viewPagerPageChangeCallback)
        super.onDestroyView()
    }

}