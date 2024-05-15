package com.jar.app.feature_lending_kyc.impl.ui.steps

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDirections
import com.jar.app.core_base.domain.model.KycProgressResponse
import com.jar.app.core_base.util.orFalse
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.base.data.livedata.SingleLiveEvent
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.app.base.util.encodeUrl
import com.jar.app.core_base.domain.mapper.toKycProgressResponse
import com.jar.app.core_base.domain.model.KycEmailAndAadhaarProgressStatus
import com.jar.app.feature_lending_kyc.FeatureLendingKycNavigationDirections
import com.jar.app.feature_lending_kyc.FeatureLendingKycStepsNavigationDirections
import com.jar.app.feature_lending_kyc.R
import com.jar.app.feature_lending_kyc.shared.api.use_case.FetchKycProgressUseCase
import com.jar.app.feature_lending_kyc.impl.data.KycStep
import com.jar.app.feature_lending_kyc.impl.data.KycStepStatus
import com.jar.app.feature_lending_kyc.shared.domain.arguments.CreditReportScreenArguments
import com.jar.app.feature_lending_kyc.shared.domain.model.*
import com.jar.app.feature_lending_kyc.shared.domain.model.CreditReportPAN
import com.jar.app.feature_lending_kyc.shared.domain.model.KycAadhaar
import com.jar.app.feature_lending_kyc.shared.domain.model.PanErrorScreenPrimaryButtonAction
import com.jar.app.feature_lending_kyc.shared.domain.model.PanErrorScreenSecondaryButtonAction
import com.jar.app.feature_lending_kyc.shared.util.LendingKycConstants
import com.jar.app.feature_lending_kyc.shared.util.LendingKycFlowType
import com.jar.internal.library.jar_core_network.api.util.mapToDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference
import javax.inject.Inject

@HiltViewModel
internal class LendingKycStepsViewModel @Inject constructor(
    private val fetchKycProgressUseCase: FetchKycProgressUseCase,
    private val lendingKycStepsProgressGenerator: LendingKycStepsProgressGenerator,
    private val lendingKycNavigationGenerator: LendingKycNavigationGenerator,
    private val serializer: Serializer
) : ViewModel() {

    companion object {
        const val BACK_NAVIGATION = "Back Navigation"
    }

    private val _kycStepsLiveData = MutableLiveData<RestClientResult<List<KycStep>>>()
    val kycStepsLiveData: LiveData<RestClientResult<List<KycStep>>>
        get() = _kycStepsLiveData

    private val _kycNavDirectionsLiveData = SingleLiveEvent<RestClientResult<NavDirections>>()
    val kycNavDirectionsLiveData: LiveData<RestClientResult<NavDirections>>
        get() = _kycNavDirectionsLiveData

    private val _kycBackNavDirectionsLiveData = SingleLiveEvent<BackOrViewOnlyNavigation>()
    val kycBackNavDirectionsLiveData: LiveData<BackOrViewOnlyNavigation>
        get() = _kycBackNavDirectionsLiveData

    private val _stepsRemainingLiveData = SingleLiveEvent<Int>()
    val stepsRemainingLiveData: LiveData<Int>
        get() = _stepsRemainingLiveData

    private val _toolbarInteractionLiveData = SingleLiveEvent<String>()
    val toolbarInteractionLiveData: LiveData<String>
        get() = _toolbarInteractionLiveData

    var kycProgressResponse: KycProgressResponse? = null
    var stepsRemaining: Int = 4
    var flowType: String = ""

    fun notifyToolbarInteraction(type: String) {
        _toolbarInteractionLiveData.postValue(type)
    }

    fun fetchKycProgress(
        activity: WeakReference<FragmentActivity>,
        isLendingOnboardingFlow: Boolean,
        shouldNavigate: Boolean = true
    ) {
        if (shouldNavigate) {
            _kycNavDirectionsLiveData.postValue(RestClientResult.loading())
            _kycStepsLiveData.postValue(RestClientResult.loading())
        }
        viewModelScope.launch {
            fetchKycProgressUseCase.fetchKycProgress()
                .mapToDTO {
                    it?.toKycProgressResponse()
                }
                .collect {
                it.data?.data?.let {
                    kycProgressResponse = it
                    val steps = lendingKycStepsProgressGenerator.getKycProgressList(
                        activity, it
                    )
                    calculateRemainingSteps(steps)
                    _kycStepsLiveData.postValue(
                        RestClientResult.success(steps)
                    )
                    if (isLendingOnboardingFlow.not() && shouldNavigate)
                        _kycNavDirectionsLiveData.postValue(
                            RestClientResult.success(
                                lendingKycNavigationGenerator.getKycCurrentStepNavigationDirection(
                                    activity, it
                                )
                            )
                        )
                }
            }
        }
    }

    private fun calculateRemainingSteps(step: List<KycStep>) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                var totalStepsRemaining = 4
                step.forEach {
                    if (it.status == KycStepStatus.COMPLETED)
                        totalStepsRemaining--
                }
                stepsRemaining = totalStepsRemaining
                _stepsRemainingLiveData.postValue(totalStepsRemaining)
            }
        }
    }

    fun viewOnlyNavigationRedirectTo(
        flowType: LendingKycFlowType,
        isGoToNextStepFlow: Boolean = false,
        contextRef: WeakReference<FragmentActivity>
    ) {
        viewModelScope.launch {
            kycProgressResponse?.kycProgress?.let {
                val navDirections: NavDirections =
                    when (flowType) {
                        LendingKycFlowType.EMAIL -> {
                            if (isGoToNextStepFlow) {
                                if (it.EMAIL?.status!! == KycEmailAndAadhaarProgressStatus.VERIFIED.name) {
                                    lendingKycNavigationGenerator.panVerificationProgress(
                                        it.PAN,
                                        true,
                                        contextRef = contextRef
                                    )!!
                                } else
                                    lendingKycNavigationGenerator.emailVerificationProgress(
                                        it.EMAIL,
                                        isViewOnlyFlow = false
                                    )
                            } else
                                FeatureLendingKycNavigationDirections.actionToExitLendingFlowBottomSheet(
                                    1
                                )
                        }
                        LendingKycFlowType.PAN -> {
                            if (isGoToNextStepFlow)
                                lendingKycNavigationGenerator.aadhaarVerificationProgress(
                                    it.AADHAAR,
                                    isViewOnlyFlow = it.AADHAAR?.status == KycEmailAndAadhaarProgressStatus.VERIFIED.name,
                                    contextRef
                                )
                            else
                                FeatureLendingKycStepsNavigationDirections.actionToEmailVerificationFragment(
                                    it.EMAIL?.email,
                                    isBackNavOrViewOnlyFlow = true
                                )
                        }
                        LendingKycFlowType.AADHAAR -> {
                            if (isGoToNextStepFlow)
                                lendingKycNavigationGenerator.aadhaarVerificationProgress(
                                    it.AADHAAR,
                                    isViewOnlyFlow = true,
                                    contextRef
                                )
                            else {
                                val args = encodeUrl(serializer.encodeToString(
                                    CreditReportScreenArguments(
                                    CreditReportPAN(
                                        it.PAN?.panNo.orEmpty(),
                                        it.PAN?.firstName.orEmpty(),
                                        it.PAN?.lastName.orEmpty(),
                                        it.PAN?.dob.orEmpty()
                                    ),
                                    it.PAN?.jarVerifiedPAN.orFalse(),
                                    LendingKycConstants.PanFlowType.BACK_FLOW,
                                    isBackNavOrViewOnlyFlow = true,
                                    primaryAction = PanErrorScreenPrimaryButtonAction.NONE,
                                    secondaryAction = PanErrorScreenSecondaryButtonAction.NONE,
                                    fromScreen = BACK_NAVIGATION,
                                    description = ""
                                )
                                ))
                                FeatureLendingKycStepsNavigationDirections.actionToCreditReportFetchedStep(
                                    args
                                )
                            }
                        }
                        LendingKycFlowType.SELFIE -> {
                            if (isGoToNextStepFlow)
                                lendingKycNavigationGenerator.selfieVerificationProgress(
                                    it.SELFIE,
                                    contextRef
                                )!!
                            else
                                FeatureLendingKycStepsNavigationDirections.actionToVerifiedAadhaarFragment(
                                    KycAadhaar(
                                        it.AADHAAR?.aadhaarNo,
                                        it.AADHAAR?.dob,
                                        it.AADHAAR?.name
                                    )
                                )
                        }
                    }
                _kycBackNavDirectionsLiveData.postValue(
                    BackOrViewOnlyNavigation(navDirections, isGoToNextStepFlow.not())
                )
            } ?: run {
                _kycBackNavDirectionsLiveData.postValue(
                    BackOrViewOnlyNavigation(
                        FeatureLendingKycNavigationDirections.actionToExitLendingFlowBottomSheet(
                            1
                        ), isGoToNextStepFlow.not()
                    )
                )
            }
        }
    }

    fun toolbarBackNavigation(
        navBackStackEntry: NavBackStackEntry?,
        contextRef: WeakReference<FragmentActivity>
    ) {
        viewModelScope.launch {
            navBackStackEntry?.let {
                when (it.destination.id) {
                    R.id.emailVerificationFragment -> {
                        viewOnlyNavigationRedirectTo(
                            LendingKycFlowType.EMAIL,
                            false,
                            contextRef
                        )
                    }
                    R.id.panVerificationFragment, R.id.creditReportFetchedFragment, R.id.creditReportNotAvailableFragment -> {
                        viewOnlyNavigationRedirectTo(
                            LendingKycFlowType.PAN,
                            false,
                            contextRef
                        )
                    }
                    R.id.aadhaarConsentPromptFragment, R.id.aadhaarConfirmationFragment, R.id.verifiedAadhaarFragment -> {
                        viewOnlyNavigationRedirectTo(
                            LendingKycFlowType.AADHAAR,
                            false,
                            contextRef
                        )
                    }
                    R.id.selfieCheckFragment -> {
                        viewOnlyNavigationRedirectTo(
                            LendingKycFlowType.SELFIE,
                            false,
                            contextRef
                        )
                    }
                }

            }
        }

    }
}

data class BackOrViewOnlyNavigation(
    val navDirections: NavDirections,
    val isBackNavigation: Boolean = true
)