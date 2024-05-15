package com.jar.app.feature_lending_kyc.impl.ui.loading

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.whenResumed
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.bumptech.glide.Glide
import com.jar.app.base.ui.fragment.BaseDialogFragment
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.feature_lending_kyc.R
import com.jar.app.feature_lending_kyc.databinding.FeatureLendingKycDialogGenericLoadingBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
internal class GenericLendingKycLoadingDialog :
    BaseDialogFragment<FeatureLendingKycDialogGenericLoadingBinding>() {

    private val args by navArgs<GenericLendingKycLoadingDialogArgs>()

    private val viewModel by navGraphViewModels<GenericLendingKycLoadingViewModel>(R.id.feature_lending_kyc_steps_navigation)

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingKycDialogGenericLoadingBinding
        get() = FeatureLendingKycDialogGenericLoadingBinding::inflate

    override val dialogConfig: DialogFragmentConfig
        get() = DEFAULT_CONFIG

    private var isDismissingOnSuccess = false
    private var from = ""

    override fun setup() {
        setupUI()
        observeLiveData()
    }

    private fun setupUI() {
        binding.tvDescription.isVisible = args.genericLoadingArguments.description != null
        viewModel.updateGenericLoadingTitle(args.genericLoadingArguments.title)
        viewModel.updateGenericLoadingDescription(args.genericLoadingArguments.description.orEmpty())
        args.genericLoadingArguments.assetsUrl?.let {
            if (args.genericLoadingArguments.isIllustrationUrl) {
                binding.ivIllustration.isVisible = true
                binding.lottieView.isVisible = false
                Glide.with(requireContext())
                    .load(it)
                    .placeholder(com.jar.app.core_ui.R.drawable.ic_placeholder)
                    .error(com.jar.app.core_ui.R.drawable.core_ui_ic_error)
                    .into(binding.ivIllustration)
            } else {
                binding.ivIllustration.isInvisible = true
                binding.lottieView.isVisible = true
                binding.lottieView.playLottieWithUrlAndExceptionHandling(requireContext(),it)
            }
        }
        args.genericLoadingArguments.illustrationResourceId?.let {
            binding.ivIllustration.isVisible = true
            binding.lottieView.isVisible = false
            Glide.with(requireContext())
                .load(it)
                .placeholder(com.jar.app.core_ui.R.drawable.ic_placeholder)
                .error(com.jar.app.core_ui.R.drawable.core_ui_ic_error)
                .into(binding.ivIllustration)
        }
        binding.tvPoweredBy.isVisible = args.genericLoadingArguments.shouldShowPoweredBy
        binding.tvThisWillNotAffectYourCreditScore.isVisible =
            args.genericLoadingArguments.shouldShowWarningMessage
    }

    private fun observeLiveData() {
        viewModel.genericLoadingTitleViewModel.observe(viewLifecycleOwner) {
            uiScope.launch {
                whenResumed {
                    binding.clPrimaryLoadingContainer.isVisible = true
                    binding.progressDoneContainer.isVisible = false
                    binding.tvTitle.text = it
                }
            }
        }
        viewModel.genericLoadingDescriptionViewModel.observe(viewLifecycleOwner) {
            uiScope.launch {
                whenResumed {
                    binding.tvDescription.isVisible = true
                    binding.tvDescription.text = it
                }
            }
        }
        viewModel.genericLoadingAutoDismissAfterMillisViewModel.observe(viewLifecycleOwner) {
            uiScope.launch {
                delay(it.dismissTime)
                isDismissingOnSuccess = it.isDismissingAfterSuccess
                from = it.from
                dismissAllowingStateLoss()
            }
        }
        viewModel.genericLoadingShowSuccessViewModel.observe(viewLifecycleOwner) {
            uiScope.launch {
                whenResumed {
                    binding.clPrimaryLoadingContainer.isVisible = false
                    binding.progressDoneContainer.isVisible = true
                    binding.tvProgressDone.text = it.title
                    if (it.lottieUrl.isNotEmpty()) {
                        binding.lottieViewProgressDone.playLottieWithUrlAndExceptionHandling(requireContext(),it.lottieUrl)
                    }
                }
            }
        }
        viewModel.assetUrlViewModel.observe(viewLifecycleOwner) {
            uiScope.launch {
                whenResumed {
                    if (it.isIllustrationUrl) {
                        binding.ivIllustration.isVisible = true
                        binding.lottieView.isVisible = false
                        Glide.with(requireContext())
                            .load(it.assetUrl)
                            .placeholder(com.jar.app.core_ui.R.drawable.ic_placeholder)
                            .error(com.jar.app.core_ui.R.drawable.core_ui_ic_error)
                            .into(binding.ivIllustration)
                    } else {
                        binding.ivIllustration.isInvisible = true
                        binding.lottieView.isVisible = true
                        binding.lottieView.playLottieWithUrlAndExceptionHandling(requireContext(),it.assetUrl)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onAutoDismiss(isDismissingOnSuccess, from)
    }
}