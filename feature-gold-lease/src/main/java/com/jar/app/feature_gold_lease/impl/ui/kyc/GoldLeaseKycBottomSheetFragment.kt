package com.jar.app.feature_gold_lease.impl.ui.kyc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.jar.app.base.data.livedata.SharedPreferencesUserLiveData
import com.jar.app.core_base.domain.model.User
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.*
import com.jar.app.core_base.util.orZero
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.slideToRevealNew
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_gold_lease.R
import com.jar.app.feature_gold_lease.databinding.FragmentGoldLeaseKycBottomSheetBinding
import com.jar.app.feature_gold_lease.shared.util.GoldLeaseConstants
import com.jar.app.feature_gold_lease.shared.util.GoldLeaseEventKey
import com.jar.app.feature_kyc.impl.util.EndDateValidatorV2
import com.jar.app.feature_kyc.shared.domain.model.KycVerificationStatus
import com.jar.app.feature_kyc.shared.domain.model.ManualKycRequest
import com.jar.app.feature_user_api.domain.model.UserKycStatusEnum
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.threeten.bp.Clock
import org.threeten.bp.Instant
import java.lang.ref.WeakReference
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
internal class GoldLeaseKycBottomSheetFragment :
    BaseBottomSheetDialogFragment<FragmentGoldLeaseKycBottomSheetBinding>() {

    @Inject
    lateinit var analyticsApi: AnalyticsApi

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var sharedPreferencesUserLiveData: SharedPreferencesUserLiveData

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var remoteConfigManager: RemoteConfigApi

    @Inject
    lateinit var appScope: CoroutineScope

    private val args by navArgs<GoldLeaseKycBottomSheetFragmentArgs>()

    private val viewModelProvider by viewModels<GoldLeaseKycViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private var user: User? = null

    companion object {
        const val GOLD_LEASE_KYC_STATUS_REQUEST_KEY = "goldLeaseKycStatusRequestKey"
        const val GOLD_LEASE_KYC_STATUS = "goldLeaseKycStatus"
        const val KYC_VERIFIED = "kycVerified"
        const val KYC_CANCELLED = "kycCancelled"
        private const val TAG = "#GoldLeaseKycBottomSheetFragment#"
    }

    private var dob: String = ""

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentGoldLeaseKycBottomSheetBinding
        get() = FragmentGoldLeaseKycBottomSheetBinding::inflate

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig(isCancellable = false, isDraggable = false)

    override fun setup() {
        getLendingKycProgress()
        observeLiveData()
        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        toggleKycEntryMainButton()
        binding.layoutKycEntry.groupEmail.isVisible = args.isEmailRequired
        binding.layoutKycConfirmation.groupEmail.isVisible = args.isEmailRequired
    }

    private fun getLendingKycProgress() {
        setLoadingUI()
        viewModel.fetchUserLendingKycProgress()
    }

    private fun setLoadingUI() {
        binding.layoutKycStatus.lottieView.cancelAnimation()
        binding.layoutKycStatus.lottieView.playLottieWithUrlAndExceptionHandling(
            requireContext(), GoldLeaseConstants.LottieUrls.VERIFYING
        )
        binding.layoutKycStatus.tvStatusTitle.text =
            getString(R.string.feature_gold_lease_fetching_your_details)
        binding.layoutKycStatus.tvStatusDescription.isInvisible = true
        binding.layoutKycStatus.btnContactSupport.isInvisible = true
        binding.layoutKycStatus.btnTryAgain.isInvisible = true
        binding.layoutKycStatus.root.isVisible = true
        binding.layoutKycStatus.ivClose.isVisible = true
    }

    private fun setSuccessUI() {
        analyticsApi.postEvent(GoldLeaseEventKey.KycVerification.Lease_PANVerifSuccess)
        binding.layoutKycStatus.lottieView.cancelAnimation()
        binding.layoutKycStatus.lottieView.playLottieWithUrlAndExceptionHandling(
            requireContext(), GoldLeaseConstants.LottieUrls.TICK_WITH_CELEBRATION
        )
        binding.layoutKycStatus.tvStatusTitle.text =
            getString(R.string.feature_gold_lease_verification_succesfull)
        binding.layoutKycStatus.tvStatusDescription.isInvisible = true
        binding.layoutKycStatus.btnContactSupport.isInvisible = true
        binding.layoutKycStatus.btnTryAgain.isInvisible = true
        binding.layoutKycStatus.root.isVisible = true
        binding.layoutKycStatus.ivClose.isVisible = false

        uiScope.launch {
            delay(1500)
            closeBottomSheet(KYC_VERIFIED)
        }
    }

    private fun setupFailureUI(title: String?, description: String?, allRetryExhausted: Boolean, remainingRetryCount: Int) {
        analyticsApi.postEvent(GoldLeaseEventKey.KycVerification.Lease_PANVeriffailed)
        binding.layoutKycStatus.lottieView.cancelAnimation()
        binding.layoutKycStatus.lottieView.playLottieWithUrlAndExceptionHandling(
            requireContext(), GoldLeaseConstants.LottieUrls.GENERIC_ERROR
        )
        binding.layoutKycStatus.tvStatusTitle.text =
            title ?: getString(R.string.feature_gold_lease_pan_verification_failed)
        binding.layoutKycStatus.tvStatusDescription.text =
            description ?: getString(com.jar.app.core_ui.R.string.something_went_wrong)
        binding.layoutKycStatus.tvStatusDescription.isInvisible = false
        binding.layoutKycStatus.btnContactSupport.isInvisible = false
        binding.layoutKycStatus.btnTryAgain.isInvisible = allRetryExhausted
        binding.layoutKycStatus.root.isVisible = true
        binding.layoutKycStatus.ivClose.isVisible = true
        binding.layoutKycStatus.tvRemainingCount.text = getString(
            R.string.feature_gold_lease_x_attempts_remaining, remainingRetryCount.orZero().toString()
        )
        binding.layoutKycStatus.tvRemainingCount.isInvisible = remainingRetryCount.orZero() == 0
        binding.layoutKycStatus.btnTryAgain.isInvisible = remainingRetryCount.orZero() == 0

        binding.layoutKycStatus.btnTryAgain.setDebounceClickListener {
            analyticsApi.postEvent(GoldLeaseEventKey.KycVerification.Lease_PANVerifTryAgainClicked)
            slideFromKycStatusToX(binding.layoutKycEntry.root)
        }

        binding.layoutKycStatus.btnContactSupport.setDebounceClickListener {
            analyticsApi.postEvent(
                GoldLeaseEventKey.CommonEvents.Lease_ContactSupport,
                mapOf(
                    GoldLeaseEventKey.Properties.SCREEN_NAME to GoldLeaseEventKey.Screens.LEASE_KYC
                )
            )
            val number = remoteConfigManager.getWhatsappNumber()
            requireContext().openWhatsapp(
                number,
                getString(R.string.feature_gold_lease_wa_copy_kyc_failed)
            )
        }
    }

    private fun observeLiveData() {
        val weakReference: WeakReference<View> = WeakReference(binding.root.rootView)

        sharedPreferencesUserLiveData.distinctUntilChanged().observe(viewLifecycleOwner) {
            user = it
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userLendingKycProgressFlow.collect(
                    onSuccess = {
                        if (it?.kycVerified.orFalse()) {
                            uiScope.launch {
                                delay(2000) //Intentional delay requested by product
                                setKYCData(
                                    name = it?.kycProgress?.PAN?.getPrintableName().orEmpty(),
                                    pan = it?.kycProgress?.PAN?.panNo.orEmpty(),
                                    dob = it?.kycProgress?.PAN?.dob.orEmpty(),
                                    email = it?.kycProgress?.EMAIL?.email.orEmpty()
                                )
                            }
                        } else {
                            viewModel.fetchUserKycStatus()
                        }
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.userKycStatusFlow.collect(
                    onSuccess = {
                        it?.let {
                            if (it.isVerified()) {
                                viewModel.fetchKycDetails()
                            } else {
                                if (it.kycStatus == UserKycStatusEnum.FAILED.name) {
                                    setupFailureUI(
                                        it.kycScreenData?.title.orEmpty(),
                                        it.kycScreenData?.desc.orEmpty(),
                                        true,
                                        0
                                    )
                                } else {
                                    uiScope.launch {
                                        delay(2000) //Intentional delay requested by product
                                        slideFromKycStatusToX(binding.layoutKycEntry.root)
                                        user?.email?.let { email ->
                                            binding.layoutKycEntry.etEmail.setText(email)
                                        }
                                    }
                                    sendShownEvent(
                                        panType = GoldLeaseEventKey.KycVerification.manual,
                                        emailType = GoldLeaseEventKey.KycVerification.manual
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.kycDetailsFlow.collect(
                    onSuccess = {
                        uiScope.launch {
                            delay(2000) //Intentional delay requested by product
                            it?.panData?.let { _ ->
                                setKYCData(
                                    name = it.panData?.name.orEmpty(),
                                    pan = it.panData?.panNumber.orEmpty(),
                                    dob = it.panData?.dob.orEmpty(),
                                    email = user?.email
                                )
                            } ?: kotlin.run {
                                slideFromKycStatusToX(binding.layoutKycEntry.root)
                                user?.email?.let { email ->
                                    binding.layoutKycEntry.etEmail.setText(email)
                                }
                            }
                        }
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.manualKycRequestFlow.collect(
                onLoading = {
                    showProgressBar()
                },
                onSuccess = {
                    dismissProgressBar()
                    it?.let {
                        if (it.verificationStatus == KycVerificationStatus.VERIFIED.name) {
                            setSuccessUI()
                        } else {
                            setupFailureUI(it.title, it.description, it.allRetryExhausted, it.remainingRetryCount.orZero())
                        }
                        slideFromKycEntryToX(binding.layoutKycStatus.root)
                    }
                },
                onError = { errorMessage, _ ->
                    dismissProgressBar()
                    errorMessage.snackBar(weakReference.get()!!)
                }
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.updateUserFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(weakReference.get()!!)
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it?.let {
                            appScope.launch {
                                prefs.setUserStringSync(serializer.encodeToString(it))
                            }
                            setSuccessUI()
                            slideFromKycConfirmationToX(binding.layoutKycStatus.root)
                        }
                    }
                )
            }
        }
    }

    private fun slideFromKycStatusToX(root: View) {
        binding.layoutKycStatus.lottieView.cancelAnimation()
        binding.layoutKycStatus.root.slideToRevealNew(root)
    }

    private fun slideFromKycConfirmationToX(root: View) {
        binding.layoutKycConfirmation.root.slideToRevealNew(root)
    }

    private fun slideFromKycEntryToX(root: View) {
        binding.layoutKycEntry.root.slideToRevealNew(root)
        binding.layoutKycEntry.etPan.text = null
        binding.layoutKycEntry.etName.text = null
        binding.layoutKycEntry.etDob.text = null
        binding.layoutKycEntry.etDob.hint = getCustomString(com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_select_a_date)
    }

    private fun setKYCData(
        pan: String,
        name: String,
        dob: String,
        email: String?
    ) {
        sendShownEvent(
            panType = GoldLeaseEventKey.KycVerification.automatic,
            emailType = if (email.isNullOrEmpty()) GoldLeaseEventKey.KycVerification.manual else GoldLeaseEventKey.KycVerification.automatic
        )
        binding.layoutKycConfirmation.etEmail.isVisible = args.isEmailRequired && email.isNullOrEmpty()
        binding.layoutKycConfirmation.tvEmail.isVisible = args.isEmailRequired && email.isNullOrEmpty().not()
        if (email.isNullOrEmpty().not()) {
            binding.layoutKycConfirmation.tvEmail.text = email
        }

        binding.layoutKycConfirmation.tvHeader.text =
            if (email.isNullOrEmpty()) getString(R.string.feature_gold_lease_enter_your_details) else getString(
                R.string.feature_gold_lease_confirm_your_details
            )
        binding.layoutKycConfirmation.identityView.setDob(dob)
        binding.layoutKycConfirmation.identityView.setIdentityHeading(getString(R.string.feature_gold_lease_pan))
        binding.layoutKycConfirmation.identityView.setIdentity(pan)
        binding.layoutKycConfirmation.identityView.setName(name)
        toggleConfirmationActionButton(email.isNullOrEmpty() && args.isEmailRequired)

        binding.layoutKycConfirmation.btnConfirmAndVerify.setDebounceClickListener {
            clearFocus()
            sendConfirmAndVerifyEvent()
            if (email.isNullOrEmpty()) {
                val userEmail = binding.layoutKycConfirmation.etEmail.text?.toString().orEmpty()
                if (userEmail.isEmpty()) {
                    getString(R.string.feature_gold_lease_enter_your_email).snackBar(
                        binding.root,
                        translationY = 0f
                    )
                } else if (userEmail.isValidEmail().not()) {
                    getString(R.string.feature_gold_lease_enter_a_valid_email).snackBar(
                        binding.root,
                        translationY = 0f
                    )
                } else {
                    viewModel.updateUserEmail(userEmail)
                }
            } else {
                setSuccessUI()
                slideFromKycConfirmationToX(binding.layoutKycStatus.root)
            }
        }

        slideFromKycStatusToX(binding.layoutKycConfirmation.root)
    }

    private fun toggleConfirmationActionButton(shouldDisable: Boolean) {
        binding.layoutKycConfirmation.btnConfirmAndVerify.setDisabled(shouldDisable)
    }

    private fun closeBottomSheet(status: String) {
        if (status != KYC_VERIFIED) {
            analyticsApi.postEvent(GoldLeaseEventKey.KycVerification.Lease_PANVerifScreenBackClicked)
        }
        findNavController().navigateUp()
        setFragmentResult(
            GOLD_LEASE_KYC_STATUS_REQUEST_KEY,
            bundleOf(
                Pair(GOLD_LEASE_KYC_STATUS, status)
            )
        )
    }

    private fun setupListeners() {
        binding.layoutKycStatus.ivClose.setOnClickListener {
            closeBottomSheet(KYC_CANCELLED)
        }

        binding.layoutKycConfirmation.ivClose.setDebounceClickListener {
            closeBottomSheet(KYC_CANCELLED)
        }

        binding.layoutKycEntry.ivClose.setDebounceClickListener {
            closeBottomSheet(KYC_CANCELLED)
        }

        binding.layoutKycEntry.etDob.setDebounceClickListener {
            clearFocus()
            showDobPicker()
        }

        binding.layoutKycConfirmation.etEmail.textChanges()
            .debounce(500)
            .onEach {
                toggleConfirmationActionButton(
                    shouldDisable = it?.toString()?.isValidEmail()?.not().orTrue() && args.isEmailRequired
                )
            }.launchIn(uiScope)

        binding.layoutKycEntry.etEmail.textChanges()
            .debounce(500)
            .onEach {
                toggleKycEntryMainButton()
                /**Added a check for 8 characters since the event needs to
                 *pushed only if user inputs something greater than or
                 *equal to 8**/
                if (it?.length.orZero() >= 8) {
                    analyticsApi.postEvent(
                        GoldLeaseEventKey.KycVerification.Lease_Verification,
                        mapOf(
                            GoldLeaseEventKey.KycVerification.valEntered to GoldLeaseEventKey.KycVerification.Email,
                            GoldLeaseEventKey.KycVerification.value to it?.toString().orEmpty()
                        )
                    )
                }
            }.launchIn(uiScope)

        binding.layoutKycEntry.etName.textChanges()
            .debounce(500)
            .onEach {
                toggleKycEntryMainButton()
                /**Added a check for 8 characters since the event needs to
                 *pushed only if user inputs something greater than or
                 *equal to 8**/
                if (it?.length.orZero() >= 8) {
                    analyticsApi.postEvent(
                        GoldLeaseEventKey.KycVerification.Lease_Verification,
                        mapOf(
                            GoldLeaseEventKey.KycVerification.valEntered to GoldLeaseEventKey.KycVerification.Name,
                            GoldLeaseEventKey.KycVerification.value to it?.toString().orEmpty()
                        )
                    )
                }
            }.launchIn(uiScope)

        binding.layoutKycEntry.etPan.textChanges()
            .debounce(500)
            .onEach {
                toggleKycEntryMainButton()
                /**Added a check for 8 characters since the event needs to
                 *pushed only if user inputs something greater than
                 *or equal to 8**/
                if (it?.length.orZero() >= 8) {
                    analyticsApi.postEvent(
                        GoldLeaseEventKey.KycVerification.Lease_Verification,
                        mapOf(
                            GoldLeaseEventKey.KycVerification.valEntered to GoldLeaseEventKey.KycVerification.PAN,
                        )
                    )
                }
                binding.layoutKycEntry.tvPanErrorMessage.isVisible = it?.length.orZero() in 6..9
            }.launchIn(uiScope)

        binding.layoutKycEntry.btnConfirmAndVerify.setDebounceClickListener {
            clearFocus()
            viewModel.manualKycRequest = validate()
            viewModel.manualKycRequest?.let {
                sendConfirmAndVerifyEvent()
                viewModel.postManualKycRequest(it)
            }
        }
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
            binding.layoutKycEntry.etDob.text = dob
            analyticsApi.postEvent(
                GoldLeaseEventKey.KycVerification.Lease_Verification,
                mapOf(
                    GoldLeaseEventKey.KycVerification.valEntered to GoldLeaseEventKey.KycVerification.DOB,
                    GoldLeaseEventKey.KycVerification.value to dob
                )
            )
            toggleKycEntryMainButton()
        }
        materialDatePicker.show(childFragmentManager, TAG)
    }

    private fun toggleKycEntryMainButton(disableAnyway: Boolean = false) {
        val shouldEnable =
            if (disableAnyway) false else (!binding.layoutKycEntry.etPan.text.isNullOrEmpty()
                    && !binding.layoutKycEntry.etName.text.isNullOrEmpty()
                    && !binding.layoutKycEntry.etDob.text.isNullOrEmpty()
                    && binding.layoutKycEntry.etPan.text?.toString().orEmpty().trim().length == 10
                    && binding.layoutKycEntry.etPan.text?.toString().orEmpty().contains("\\s".toRegex()).not())
        binding.layoutKycEntry.btnConfirmAndVerify.setDisabled(
            if (args.isEmailRequired)
                    shouldEnable.not()
                            || binding.layoutKycEntry.etEmail.text.isNullOrEmpty()
                            || binding.layoutKycEntry.etEmail.text?.toString().orEmpty().isValidEmail().not()
            else shouldEnable.not()
        )
    }

    private fun sendShownEvent(panType: String, emailType: String) {
        viewModel.emailPreFillType = emailType
        viewModel.panPreFillType = panType
        analyticsApi.postEvent(
            GoldLeaseEventKey.KycVerification.Lease_VerificationBSShown,
            mapOf(
                GoldLeaseEventKey.KycVerification.email_fill_type to viewModel.emailPreFillType,
                GoldLeaseEventKey.KycVerification.pan_fill_type to viewModel.panPreFillType
            )
        )
    }

    private fun sendConfirmAndVerifyEvent() {
        analyticsApi.postEvent(
            GoldLeaseEventKey.KycVerification.Lease_ConfirmandVerifyDetailsClicked,
            mapOf(
                GoldLeaseEventKey.KycVerification.email_fill_type to viewModel.emailPreFillType,
                GoldLeaseEventKey.KycVerification.pan_fill_type to viewModel.panPreFillType
            )
        )
    }

    private fun validate(): ManualKycRequest? {
        val manualKycRequest = ManualKycRequest(
            panNumber = binding.layoutKycEntry.etPan.text?.toString().orEmpty().trim()
                .uppercase(Locale.getDefault()),
            name = binding.layoutKycEntry.etName.text?.toString().orEmpty().trim(),
            dob = dob,
            emailId = binding.layoutKycEntry.etEmail.text?.toString().orEmpty().trim()
        )
        if (manualKycRequest.panNumber.isEmpty()) {
            getCustomString(com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_enter_pan_number).snackBar(
                getRootView()
            )
            return null
        }
        if (manualKycRequest.panNumber.length < 10) {
            getCustomString(com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_pan_10_digit_msg).snackBar(
                getRootView()
            )
            return null
        }
        if (manualKycRequest.panNumber.contains("\\s".toRegex())) {
            getCustomString(com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_pan_no_space_msg).snackBar(
                getRootView()
            )
            return null
        }
        if (manualKycRequest.name.orEmpty().isEmpty()) {
            getCustomString(com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_enter_name).snackBar(getRootView())
            return null
        }
        if (manualKycRequest.dob.orEmpty().isEmpty()) {
            getCustomString(com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_select_dob).snackBar(getRootView())
            return null
        }
        if (args.isEmailRequired && manualKycRequest.emailId.orEmpty().isEmpty()) {
            getString(R.string.feature_gold_lease_enter_your_email).snackBar(getRootView())
            return null
        }
        if (args.isEmailRequired && manualKycRequest.emailId.orEmpty().isValidEmail().not()) {
            getString(R.string.feature_gold_lease_enter_a_valid_email).snackBar(getRootView())
            return null
        }

        return manualKycRequest
    }

    private fun clearFocus() {
        binding.layoutKycEntry.etName.clearFocus()
        binding.layoutKycEntry.etPan.clearFocus()
        binding.layoutKycEntry.etEmail.clearFocus()
        binding.layoutKycConfirmation.etEmail.clearFocus()
    }
}