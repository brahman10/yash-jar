package com.jar.app.feature_lending.impl.ui.kyc.ckyc

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.decodeUrl
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FeatureLendingFragmentConfirmCkycDetailsBinding
import com.jar.app.feature_lending.impl.domain.event.ReadyCashNavigationEvent
import com.jar.app.feature_lending.shared.domain.LendingEventKeyV2
import com.jar.app.feature_lending.shared.domain.model.experiment.ReadyCashScreenArgs
import com.jar.app.feature_lending.shared.domain.model.temp.LoanStatus
import com.jar.app.feature_lending.shared.domain.model.v2.CkycDetail
import com.jar.app.feature_lending.shared.domain.model.v2.UpdateLoanDetailsBodyV2
import com.jar.app.feature_lending.shared.domain.model.v2.UpdateStatus
import com.jar.app.feature_lending_kyc.shared.util.LendingKycConstants
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class ConfirmCkycDetailsFragment :
    BaseFragment<FeatureLendingFragmentConfirmCkycDetailsBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var serializer: Serializer

    private var isCkycDenied = false

    private val viewModelProvider: ConfirmCkycDetailsViewModelAndroid by viewModels { defaultViewModelProviderFactory }
    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val arguments by navArgs<ConfirmCkycDetailsFragmentArgs>()
    private val args by lazy {
        serializer.decodeFromString<ReadyCashScreenArgs>(
            decodeUrl(arguments.screenArgs)
        )
    }

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackNavigation()
            }
        }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureLendingFragmentConfirmCkycDetailsBinding
        get() = FeatureLendingFragmentConfirmCkycDetailsBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchKycInfo(args.loanId.orEmpty())
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        observeFlow()
        initClickListener()
        registerBackPressDispatcher()
    }

    private fun setupUI() {
        binding.btnNotItsNotMyDetails.paintFlags =
            binding.btnNotItsNotMyDetails.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        Glide.with(requireContext())
            .load(BaseConstants.CDN_BASE_URL + LendingKycConstants.IllustrationUrls.AADHAAR_PLACEHOLDER_URL)
            .into(binding.ivAadhaarImage)
        analyticsHandler.postEvent(
            event = LendingEventKeyV2.Lending_ConfirmCkycDetailsLaunched,
            values = mapOf(
                LendingEventKeyV2.action to LendingEventKeyV2.shown,
                LendingEventKeyV2.lender to args.lender.orEmpty()
            )
        )
    }

    private fun initClickListener() {
        binding.btnConfirm.setDebounceClickListener {
            updateButtonClick(LoanStatus.VERIFIED.name)
        }
        binding.btnNotItsNotMyDetails.setDebounceClickListener {
            isCkycDenied = true
            updateButtonClick(LoanStatus.FAILED.name)
        }
    }

    private fun updateButtonClick(status:String) {
        viewModel.updateCkycConsent(
            UpdateLoanDetailsBodyV2(
                applicationId = args.loanId,
                ckycDetails = UpdateStatus(status = status)
            )
        )
        analyticsHandler.postEvent(
            LendingEventKeyV2.Lending_ConfirmCkycDetailsLaunched,
            mapOf(
                LendingEventKeyV2.lender to args.lender.orEmpty(),
                LendingEventKeyV2.action to if (status == LoanStatus.VERIFIED.name)
                    LendingEventKeyV2.details_confirmed else LendingEventKeyV2.details_denied,
                )
        )
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.ckycInfoFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it?.applicationDetails?.ckycDetails?.let {
                            setInfoOnUi(it)
                        }
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.ckycConsentUpdateFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        goToNextScreen()
                    },
                    onError = {errorMessage, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }
    }

    private fun setInfoOnUi(ckyc: CkycDetail) {
            binding.tvNameValue.text = ckyc.name.orEmpty()
            binding.tvDobValue.text = ckyc.dob.orEmpty()
            binding.tvAddressValue.text = ckyc.address.orEmpty()
    }

    private fun handleBackNavigation() {
        args.screenData?.let {
            EventBus.getDefault().postSticky(
                ReadyCashNavigationEvent(
                    whichScreen = it.backScreen,
                    source = args.screenName,
                    popupToId = R.id.confirmCkycDetailsFragment,
                    isBackFlow = true
                )
            )
        }
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
    }

    private fun goToNextScreen() {
        args.screenData?.let {
            EventBus.getDefault().postSticky(
                ReadyCashNavigationEvent(
                    whichScreen = it.nextScreen,
                    source = args.screenName,
                    launchType = if (isCkycDenied) "ckyc_denied" else null,
                    popupToId = R.id.confirmCkycDetailsFragment,
                    shouldCacheThisEvent = false
                )
            )
        }
    }

    override fun onDestroyView() {
        backPressCallback.isEnabled = false
        super.onDestroyView()
    }
}