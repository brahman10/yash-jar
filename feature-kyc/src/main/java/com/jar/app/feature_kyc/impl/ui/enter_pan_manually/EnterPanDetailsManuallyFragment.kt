package com.jar.app.feature_kyc.impl.ui.enter_pan_manually

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarDefault
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.encodeUrl
import com.jar.app.base.util.epochToDate
import com.jar.app.base.util.getFormattedDate
import com.jar.app.base.util.textChanges
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_kyc.databinding.FragmentEnterPanDetailsManuallyBinding
import com.jar.app.feature_kyc.impl.util.EndDateValidatorV2
import com.jar.app.feature_kyc.shared.domain.model.KycStatus
import com.jar.app.feature_kyc.shared.domain.model.ManualKycRequest
import com.jar.app.feature_kyc.shared.util.KycConstants
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.threeten.bp.Clock
import org.threeten.bp.Instant
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
internal class EnterPanDetailsManuallyFragment : BaseFragment<FragmentEnterPanDetailsManuallyBinding>() {

    @Inject
    lateinit var serializer: Serializer

    private var dob: String = ""

    private val viewModelProvider by viewModels<EnterPanDetailsManuallyViewModelAndroid> { defaultViewModelProviderFactory }
    private val viewModel by lazy { viewModelProvider.getInstance() }


    private val args by navArgs<EnterPanDetailsManuallyFragmentArgs>()

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    companion object {
        private const val TAG = "#EnterPanDetailsManuallyFragment#"
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentEnterPanDetailsManuallyBinding
        get() = FragmentEnterPanDetailsManuallyBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateAppBarEvent(
                AppBarData(
                    ToolbarDefault(
                        getCustomString(com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_enter_your_pan_details),
                        showSeparator = true
                    )
                )
            )
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        initClickListeners()
        observeFlow()
    }

    private fun setupUI() {
        toggleMainButton(disableAnyway = true)
        analyticsHandler.postEvent(
            KycConstants.AnalyticsKeys.SHOWN_ENTER_PAN_CARD_DETAILS_SCREEN,
            emptyMap()
        )
    }

    private fun initClickListeners() {
        binding.etDob.setDebounceClickListener {
            clearFocus()
            showDobPicker()
        }

        binding.btnVerify.setDebounceClickListener {
            validate()?.let {
                analyticsHandler.postEvent(
                    KycConstants.AnalyticsKeys.CLICKED_VERIFY_PAN_BUTTON_ENTER_PAN_CARD_DETAILS_SCREEN,
                    emptyMap()
                )
                viewModel.postManualKycRequest(manualKycRequest = it)
            }
        }

        binding.etName.textChanges()
            .debounce(100)
            .onEach {
                toggleMainButton()
            }
            .launchIn(uiScope)

        binding.etPan.textChanges()
            .debounce(100)
            .onEach {
                toggleMainButton()
            }
            .launchIn(uiScope)
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.manualKycRequestFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it?.let {
                            val kycStatus = KycStatus(
                                title = it.title,
                                description = it.description,
                                shareMsg = it.shareMsg,
                                verificationStatus = it.verificationStatus
                            )
                            val encoded = encodeUrl(serializer.encodeToString(kycStatus))
                            navigateTo("android-app://com.jar.app/kycVerificationStatusFragment/$encoded/${args.fromScreen}")
                        }
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }
    }

    private fun validate(): ManualKycRequest? {
        val manualKycRequest = ManualKycRequest(
            panNumber = binding.etPan.text.toString().trim().uppercase(Locale.getDefault()),
            name = binding.etName.text.toString().trim(),
            dob = dob
        )
        if (manualKycRequest.panNumber.isEmpty()) {
            getCustomString(com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_enter_pan_number).snackBar(
                binding.root
            )
            return null
        }
        if(manualKycRequest.panNumber.length < 10){
            getCustomString(com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_pan_10_digit_msg).snackBar(binding.root)
            return null
        }
        if(manualKycRequest.panNumber.contains("\\s".toRegex())){
            getCustomString(com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_pan_no_space_msg).snackBar(binding.root)
            return null
        }
        if (manualKycRequest.name.orEmpty().isEmpty()) {
            getCustomString(com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_enter_name).snackBar(binding.root)
            return null
        }
        if (manualKycRequest.dob.orEmpty().isEmpty()) {
            getCustomString(com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_select_dob).snackBar(binding.root)
            return null
        }

        return manualKycRequest
    }

    private fun showDobPicker() {
        val constraintsBuilder = CalendarConstraints.Builder()
        val end = Instant.now(Clock.systemDefaultZone()).toEpochMilli()
        constraintsBuilder.setValidator(EndDateValidatorV2(end))
        constraintsBuilder.setEnd(end)

        val materialDatePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTheme(com.jar.app.core_ui.R.style.ThemeOverlay_App_DatePicker)
                .setCalendarConstraints(constraintsBuilder.build())
                .build()

        materialDatePicker.addOnPositiveButtonClickListener {
            dob = it.epochToDate().getFormattedDate("yyyy-MM-dd")
            binding.etDob.text = dob
            toggleMainButton()
        }
        materialDatePicker.show(childFragmentManager, TAG)
    }

    private fun toggleMainButton(disableAnyway: Boolean = false) {
        val shouldEnable =
            if (disableAnyway) false else (!binding.etPan.text.isNullOrEmpty() && !binding.etName.text.isNullOrEmpty() && !binding.etDob.text.isNullOrEmpty())
        binding.btnVerify.setDisabled(!shouldEnable)
    }

    private fun clearFocus() {
        binding.etName.clearFocus()
        binding.etPan.clearFocus()
    }
}