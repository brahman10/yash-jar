package com.jar.app.feature_lending.impl.ui.common

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDirections
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.core_utils.data.NetworkFlow
import com.jar.app.base.data.livedata.SingleLiveEvent
import com.jar.app.core_base.util.orZero
import com.jar.app.core_base.domain.mapper.toGoldBalance
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.shared.domain.model.temp.LoanApplicationItem
import com.jar.app.feature_lending.shared.domain.model.temp.LoanApplications
import com.jar.app.feature_lending.shared.domain.use_case.FetchLoanApplicationsUseCase
import com.jar.app.feature_lending.shared.ui.step_view.LendingProgressStep
import com.jar.app.feature_lending.shared.ui.step_view.LendingStepsProgressGenerator
import com.jar.app.feature_lending.shared.util.LendingFlowType
import com.jar.app.feature_user_api.domain.use_case.FetchUserGoldBalanceUseCase
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jar_core_network.api.util.mapToDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import java.util.*
import javax.inject.Inject

@HiltViewModel
internal class LendingViewModel @Inject constructor(
    private val networkFlow: NetworkFlow,
    private val fetchLoanApplicationsUseCase: FetchLoanApplicationsUseCase,
    private val lendingStepsProgressGenerator: LendingStepsProgressGenerator,
    private val fetchUserCurrentGoldBalanceUseCase: FetchUserGoldBalanceUseCase
) : ViewModel() {

    private val _lendingNavDirectionsLiveData = SingleLiveEvent<RestClientResult<NavDirections>>()
    val lendingNavDirectionsLiveData: LiveData<RestClientResult<NavDirections>>
        get() = _lendingNavDirectionsLiveData

    private val _lendingBackNavDirectionsLiveData = SingleLiveEvent<LendingBackOrViewOnlyNavigation>()
    val lendingBackNavDirectionsLiveData: LiveData<LendingBackOrViewOnlyNavigation>
        get() = _lendingBackNavDirectionsLiveData

    private val _networkStateLiveData = MutableLiveData<Boolean>()
    val networkStateLiveData: LiveData<Boolean>
        get() = _networkStateLiveData

    private val _loanApplicationsLiveData = SingleLiveEvent<RestClientResult<ApiResponseWrapper<LoanApplications?>>>()
    val loanApplicationsLiveData: LiveData<RestClientResult<ApiResponseWrapper<LoanApplications?>>>
        get() = _loanApplicationsLiveData

    private val _lendingStepLiveData = SingleLiveEvent<List<LendingProgressStep>>()
    val lendingStepLiveData: LiveData<List<LendingProgressStep>>
        get() = _lendingStepLiveData

    private var networkJob: Job? = null

    var loanApplications: LoanApplications? = null

    var userGoldBalance: Float? = null

    var currentStep: LendingProgressStep? = null

    var shouldSuppressRedirection: Boolean = false

    fun fetchUserGoldBalance() {
        viewModelScope.launch {
            fetchUserCurrentGoldBalanceUseCase.fetchUserGoldBalance()
                .mapToDTO {
                    it?.toGoldBalance()
                }
                .collect(
                onSuccess = {
                    userGoldBalance = it?.volume.orZero()
                }
            )
        }
    }

    init {
        fetchUserGoldBalance()
        observeNetwork()
    }

    fun observeNetwork() {
        networkJob?.cancel()
        networkJob = viewModelScope.launch {
            networkFlow.networkStatus.collect {
                _networkStateLiveData.postValue(it)
            }
        }
    }

    fun fetchLendingProgress(suppressRedirection: Boolean = false) {
        viewModelScope.launch {
            fetchLoanApplicationsUseCase.fetchLoanApplications().collect {
                loanApplications = it.data?.data
                shouldSuppressRedirection = suppressRedirection
                _loanApplicationsLiveData.postValue(it)
            }
        }
    }

    //Used in LendingStepsFragment to navigate to respective step according to the progress
    fun fetchLendingStepNavigation(loanApplications: LoanApplications, currentDestination: Int?, flowType: String) {

    }

    fun fetchLendingStepList(loanApplications: LoanApplications, activityRef: WeakReference<FragmentActivity>, currentDestination: Int?, isBackFlow: Boolean) {
        viewModelScope.launch {

        }
    }

    fun viewOnlyNavigationRedirectTo(
        flowType: LendingFlowType,
        isGoToNextStepFlow: Boolean = false,
        currentDestination: Int?,
        contextRef: WeakReference<FragmentActivity>,
        fromFlow: String
    ) {


    }

    fun getLoanId(): String {
        return loanApplications?.applications?.getOrNull(0)?.applicationId.orEmpty()
    }

    fun getLoanApplication(): LoanApplicationItem? {
        return loanApplications?.applications?.firstOrNull()
    }

    fun toolbarBackNavigation(
        navBackStackEntry: NavBackStackEntry?,
        contextRef: WeakReference<FragmentActivity>,
        flowType: String
    ) {
        viewModelScope.launch {
            navBackStackEntry?.let {
                when (it.destination.id) {
                    R.id.lendingEmploymentDetailsFragment,
                    R.id.lendingSelectAddressFragment,
                    R.id.selectLoanAmountFragment,
                    R.id.selectEMIFragment,
                    R.id.lendingPersonalDetailsViewOnlyFragment,
                    R.id.lendingSummaryViewOnlyFragment,
                    R.id.lendingKycViewOnlyFragment -> {
                        viewOnlyNavigationRedirectTo(
                            LendingFlowType.PERSONAL_DETAILS,
                            isGoToNextStepFlow = false,
                            currentDestination = it.destination.id,
                            contextRef,
                            flowType
                        )
                    }
                    R.id.bankDetailsFragment,
                    R.id.mandateStatusFragment,
                    R.id.automateEmiFragment -> {
                        viewOnlyNavigationRedirectTo(
                            LendingFlowType.LOAN_APPLICATION,
                            isGoToNextStepFlow = false,
                            currentDestination = it.destination.id,
                            contextRef,
                            flowType
                        )
                    }
                    R.id.loanFinalDetailsFragment,
                    R.id.loanReasonFragment,
                    R.id.loanAgreementFragment -> {
                        viewOnlyNavigationRedirectTo(
                            LendingFlowType.AGREEMENT,
                            isGoToNextStepFlow = false,
                            currentDestination = it.destination.id,
                            contextRef,
                            flowType
                        )
                    }
                }
            }
        }
    }

    fun getFirstEmiDate(createdAtEpoch: Long?): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = createdAtEpoch ?: System.currentTimeMillis()
        val todayDate = calendar.get(Calendar.DAY_OF_MONTH)
        if (todayDate > 25)
            calendar.add(Calendar.MONTH, 2)
        else
            calendar.add(Calendar.MONTH, 1)
        calendar.set(Calendar.DAY_OF_MONTH, 3)
        return calendar.timeInMillis
    }
}

data class LendingBackOrViewOnlyNavigation(
    val navDirections: NavDirections,
    val isBackNavigation: Boolean = true
)