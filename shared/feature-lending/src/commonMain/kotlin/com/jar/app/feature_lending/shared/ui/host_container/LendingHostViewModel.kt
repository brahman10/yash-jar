package com.jar.app.feature_lending.shared.ui.host_container


import com.jar.app.feature_lending.shared.api.usecase.FetchLendingV2PreApprovedDataUseCase
import com.jar.app.feature_lending.shared.api.usecase.FetchLoanApplicationListUseCase
import com.jar.app.feature_lending.shared.api.usecase.FetchLoanProgressStatusV2UseCase
import com.jar.app.feature_lending.shared.domain.model.CheckpointStatus
import com.jar.app.feature_lending.shared.domain.model.LendingFlowStatusResponse
import com.jar.app.feature_lending.shared.domain.model.experiment.ReadyCashJourney
import com.jar.app.feature_lending.shared.domain.model.experiment.ReadyCashProgressBar
import com.jar.app.feature_lending.shared.domain.model.v2.LoanApplicationItemV2
import com.jar.app.feature_lending.shared.domain.model.v2.LoanApplicationStatusV2
import com.jar.app.feature_lending.shared.domain.model.v2.LoanApplicationUpdateResponseV2
import com.jar.app.feature_lending.shared.domain.model.v2.LoanDetailsV2
import com.jar.app.feature_lending.shared.domain.model.v2.PreApprovedData
import com.jar.app.feature_lending.shared.domain.model.v2.StaticContentResponse
import com.jar.app.feature_lending.shared.domain.model.v2.UpdateLoanDetailsBodyV2
import com.jar.app.feature_lending.shared.domain.use_case.FetchLoanDetailsV2UseCase
import com.jar.app.feature_lending.shared.domain.use_case.FetchReadyCashJourneyUseCase
import com.jar.app.feature_lending.shared.domain.use_case.FetchStaticContentUseCase
import com.jar.app.feature_lending.shared.domain.use_case.UpdateLoanDetailsV2UseCase
import com.jar.app.feature_lending.shared.ui.step_view.LendingProgressStep
import com.jar.app.feature_lending.shared.ui.step_view.LendingStepsProgressGenerator
import com.jar.app.feature_lending.shared.util.LendingConstants
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.internal.library.jar_core_network.api.util.collect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LendingHostViewModel constructor(
    private val stepsProgressGenerator: LendingStepsProgressGenerator,
    private val fetchLendingV2PreApprovedDataUseCase: FetchLendingV2PreApprovedDataUseCase,
    private val fetchLoanApplicationListUseCase: FetchLoanApplicationListUseCase,
    private val fetchLoanDetailsV2UseCase: FetchLoanDetailsV2UseCase,
    private val fetchStaticContentUseCase: FetchStaticContentUseCase,
    private val fetchLoanProgressStatusV2UseCase: FetchLoanProgressStatusV2UseCase,
    private val fetchReadyCashJourneyUseCase: FetchReadyCashJourneyUseCase,
    private val updateLoanDetailsV2UseCase: UpdateLoanDetailsV2UseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _toolbarItemsFlow = MutableStateFlow<List<LendingProgressStep>>(emptyList())
    val toolbarItemsFlow: CStateFlow<List<LendingProgressStep>>
        get() = _toolbarItemsFlow.toCommonStateFlow()

    private val _preApprovedDataFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<PreApprovedData?>>>()
    val preApprovedDataFlow: CFlow<RestClientResult<ApiResponseWrapper<PreApprovedData?>>>
        get() = _preApprovedDataFlow.toCommonFlow()

    private val _loanDetailsFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<LoanDetailsV2?>>>(RestClientResult.none())
    val loanDetailsFlow: CStateFlow<RestClientResult<ApiResponseWrapper<LoanDetailsV2?>>>
        get() = _loanDetailsFlow.toCommonStateFlow()

    private val _loanProgressStatusFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<LendingFlowStatusResponse?>>>()
    val loanProgressStatusFlow: CFlow<RestClientResult<ApiResponseWrapper<LendingFlowStatusResponse?>>>
        get() = _loanProgressStatusFlow.toCommonFlow()

    private val _loanApplicationFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<List<LoanApplicationItemV2>?>>>()
    val loanApplicationFlow: CFlow<RestClientResult<ApiResponseWrapper<List<LoanApplicationItemV2>?>>>
        get() = _loanApplicationFlow.toCommonFlow()

    private val _staticContentFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<StaticContentResponse?>>>()
    val staticContentFlow: CFlow<RestClientResult<ApiResponseWrapper<StaticContentResponse?>>>
        get() = _staticContentFlow.toCommonFlow()

    private val _readyCashJourneyFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<ReadyCashJourney?>>>()
    val readyCashJourneyFlow: CFlow<RestClientResult<ApiResponseWrapper<ReadyCashJourney?>>>
        get() = _readyCashJourneyFlow.toCommonFlow()

    private val _updateLoanSummaryFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<LoanApplicationUpdateResponseV2?>>>()
    val updateLoanSummaryFlow: CFlow<RestClientResult<ApiResponseWrapper<LoanApplicationUpdateResponseV2?>>>
        get() = _updateLoanSummaryFlow.toCommonFlow()

    var loanApplicationItem: LoanApplicationItemV2? = null
    var preApprovedData: PreApprovedData? = null
    var loanDetailsV2: LoanDetailsV2? = null
    var checkpointStatus: CheckpointStatus? = null
    var mLendingFlowStatusResponse: LendingFlowStatusResponse? = null
    var staticContent: StaticContentResponse? = null

    var selectedAmount: Float = 0f
    var currentScreen: String? = null
    var readyCashJourney: ReadyCashJourney? = null
    private var journeyJob: Job? = null
    fun getScreenDataByScreenName(screenName: String) =
        readyCashJourney?.screenData?.get(screenName)


    fun isLoanDisbursed() =
        mLendingFlowStatusResponse?.status == LoanApplicationStatusV2.DISBURSED.name

    fun isLoanForeclosed() =
        mLendingFlowStatusResponse?.status == LoanApplicationStatusV2.FORECLOSED.name

    fun fetchReadyCashJourney() {
        journeyJob?.cancel()
        journeyJob = viewModelScope.launch {
            fetchReadyCashJourneyUseCase.getReadyCashJourney().collect {
                readyCashJourney = it.data?.data
                _readyCashJourneyFlow.emit(it)
            }
        }
    }

    fun fetchAllRequiredData(shouldNavigate: Boolean = false) {
        viewModelScope.launch {
            loanApplicationItem?.applicationId?.let {
                fetchProgressAndLoanDetail(it, shouldNavigate)
            } ?: run {
                fetchLoanApplicationListUseCase.fetchLoanApplicationList().collect(
                    onSuccess = {
                        loanApplicationItem = it?.getOrNull(0)
                        loanApplicationItem?.applicationId?.let { loanId ->
                            fetchProgressAndLoanDetail(loanId, shouldNavigate)
                        }
                    })
            }
        }
    }

    private fun fetchProgressAndLoanDetail(loanId: String, shouldNavigate: Boolean = false) {
        viewModelScope.launch {
            launch {
                fetchLoanProgressStatusV2UseCase.getLoanProgressStatus(loanId).collect(
                    onSuccess = {
                        it?.let { lendingFlowStatusResponse ->
                            mLendingFlowStatusResponse = lendingFlowStatusResponse
                            checkpointStatus = lendingFlowStatusResponse?.checkpoints
                        }
                    }
                )
            }
            launch {
                fetchLoanDetailsV2UseCase.getLoanDetails(loanId).collect(
                    onSuccess = {
                        loanDetailsV2 = it

                    }
                )
            }
        }
    }

    fun fetchLendingProgress(loanId: String) {
        viewModelScope.launch {
            fetchLoanProgressStatusV2UseCase.getLoanProgressStatus(loanId).collect {
                it?.data?.data?.let {
                    mLendingFlowStatusResponse = it
                }
                _loanProgressStatusFlow.emit(it)
            }
        }
    }

    fun createLendingProgress(progressBar: List<ReadyCashProgressBar>) {
        viewModelScope.launch {
            val steps = stepsProgressGenerator.getLendingProgress(progressBar)
            _toolbarItemsFlow.emit(steps)
        }
    }

    fun fetchPreApprovedData() {
        viewModelScope.launch {
            fetchLendingV2PreApprovedDataUseCase.fetchPreApprovedData().collect {
                it?.data?.data?.let { data ->
                    preApprovedData = data
                }
                _preApprovedDataFlow.emit(it)
            }
        }
    }

    fun fetchLoanList() {
        viewModelScope.launch {
            fetchLoanApplicationListUseCase.fetchLoanApplicationList().collect {
                _loanApplicationFlow.emit(it)
            }
        }
    }

    fun fetchLoanDetails(
        checkPoint: String,
        shouldPassCheckpoint: Boolean = false,
        loanId: String = getLoanId()
    ) {
        viewModelScope.launch {
            val cp = if (shouldPassCheckpoint) checkPoint else null
            fetchLoanDetailsV2UseCase.getLoanDetails(loanId, cp).collect {
                _loanDetailsFlow.emit(it)
            }
        }
    }

    fun fetchStaticContent(contentType: String, loanId: String = getLoanId()) {
        viewModelScope.launch {
            fetchStaticContentUseCase.fetchLendingStaticContent(loanId, contentType).collect {
                it.data?.data?.let { content ->
                    staticContent = content
                }
                _staticContentFlow.emit(it)
            }
        }
    }

    fun updateLoanSummaryVisited(updateLoanDetailsBodyV2: UpdateLoanDetailsBodyV2) {
        viewModelScope.launch {
            updateLoanDetailsV2UseCase.updateLoanDetails(
                updateLoanDetailsBodyV2,
                LendingConstants.LendingApplicationCheckpoints.LOAN_SUMMARY
            ).collect {
                _updateLoanSummaryFlow.emit(it)
            }
        }
    }

    fun getLoanId(): String {
        return readyCashJourney?.applicationId.orEmpty()
    }
}