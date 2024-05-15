package com.jar.app.feature_lending.shared.ui.bank.enter_account

import com.jar.app.feature_lending.shared.domain.model.v2.BankIfscResponseV2
import com.jar.app.feature_lending.shared.domain.model.v2.LoanApplicationUpdateResponseV2
import com.jar.app.feature_lending.shared.domain.model.v2.UpdateLoanDetailsBodyV2
import com.jar.app.feature_lending.shared.domain.use_case.UpdateLoanDetailsV2UseCase
import com.jar.app.feature_lending.shared.domain.use_case.ValidateIfscCodeUseCase
import com.jar.app.feature_lending.shared.util.LendingConstants
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class BankDetailsViewModel constructor(
    private val updateLoanDetailsV2UseCase: UpdateLoanDetailsV2UseCase,
    private val ifscCodeUseCase: ValidateIfscCodeUseCase,
    coroutineScope: CoroutineScope?
) {

    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    private val _ifscFlow =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<BankIfscResponseV2?>>>(RestClientResult.none())
    val ifscFlow: CStateFlow<RestClientResult<ApiResponseWrapper<BankIfscResponseV2?>>>
        get() = _ifscFlow.toCommonStateFlow()

    private val _accountVerificationFlow =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<LoanApplicationUpdateResponseV2?>>>()
    val accountVerificationFlow: CFlow<RestClientResult<ApiResponseWrapper<LoanApplicationUpdateResponseV2?>>>
        get() = _accountVerificationFlow.toCommonFlow()

    var ifscData: BankIfscResponseV2? = null

    fun verifyBankAccount(updateLoanDetailsBodyV2: UpdateLoanDetailsBodyV2) {
        viewModelScope.launch {
            updateLoanDetailsV2UseCase.updateLoanDetails(
                updateLoanDetailsBodyV2,
                LendingConstants.LendingApplicationCheckpoints.BANK_ACCOUNT_DETAILS
            ).collect {
                _accountVerificationFlow.emit(it)
            }
        }
    }

    fun verifyIfscCode(ifscCode: String) {
        viewModelScope.launch {
            ifscCodeUseCase.validateIfscCode(ifscCode).collect {
                _ifscFlow.emit(it)
            }
        }
    }
}