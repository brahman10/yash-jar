package com.jar.app.feature_payment.impl.ui.payment_option

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.core_remote_config.RemoteConfigApi
import com.jar.app.feature_exit_survey.shared.domain.use_case.impl.FetchExitSurveyQuestionsUseCase
import com.jar.app.feature_one_time_payments.shared.domain.use_case.FetchEnabledPaymentMethodUseCase
import com.jar.app.feature_one_time_payments.shared.domain.use_case.FetchRecentlyUsedPaymentMethodsUseCase
import com.jar.app.feature_one_time_payments.shared.domain.use_case.FetchSavedUpiIdUseCase
import com.jar.app.feature_one_time_payments.shared.domain.use_case.InitiateUpiCollectUseCase
import com.jar.app.feature_one_time_payments.shared.domain.use_case.VerifyUpiAddressUseCase
import com.jar.app.feature_one_time_payments.shared.ui.PaymentOptionPageFragmentViewModel
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PaymentOptionPageFragmentViewModelAndroid @Inject constructor(
    private val fetchSavedUpiIdUseCase: FetchSavedUpiIdUseCase,
    private val verifyUpiAddressUseCase: VerifyUpiAddressUseCase,
    private val initiateUpiCollectUseCase: InitiateUpiCollectUseCase,
    private val fetchEnabledPaymentMethodUseCase: FetchEnabledPaymentMethodUseCase,
    private val fetchRecentlyUsedPaymentMethodsUseCase: FetchRecentlyUsedPaymentMethodsUseCase,
    private val serializer: Serializer,
    private val remoteConfigApi: RemoteConfigApi,
    private val analyticsApi: AnalyticsApi,
    private val fetchExitSurveyQuestionsUseCase: FetchExitSurveyQuestionsUseCase,
    ) : ViewModel() {

//    @Inject
//    lateinit var hyperServices: HyperServices
//
//    fun processS() {
//
//    }

    private val viewModel by lazy {
        PaymentOptionPageFragmentViewModel(
            fetchSavedUpiIdUseCase,
            verifyUpiAddressUseCase,
            initiateUpiCollectUseCase,
            fetchEnabledPaymentMethodUseCase,
            fetchRecentlyUsedPaymentMethodsUseCase,
            remoteConfigApi,
            serializer,
            analyticsApi,
            fetchExitSurveyQuestionsUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}