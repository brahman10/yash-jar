package com.jar.app.feature.survey.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.databinding.FragmentSubmitSurveyBinding
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference

@AndroidEntryPoint
internal class SubmitSurveyFragment : BaseFragment<FragmentSubmitSurveyBinding>() {

    private val viewModel by activityViewModels<SurveyViewModel> { defaultViewModelProviderFactory }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSubmitSurveyBinding
        get() = FragmentSubmitSurveyBinding::inflate

    override fun setupAppBar() {
    }

    override fun setup(savedInstanceState: Bundle?) {
        observeLiveData()
    }

    private fun observeLiveData() {
        viewModel.submitSurveyLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onSuccess = {
                binding.tvThankYouHeading.text = it.title
                binding.tvThankYouSubheading.text = it.subTitle
            }
        )
    }
}