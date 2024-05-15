package com.jar.app.feature_homepage.impl.ui.detected_spends_info.steps

import android.content.Context
import androidx.lifecycle.*
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.decodeUrl
import com.jar.app.feature_homepage.R
import com.jar.app.feature_homepage.shared.domain.model.detected_spends.DetectedSpendPaymentInfoStep
import com.jar.app.feature_round_off.shared.domain.model.InitiateDetectedRoundOffsPaymentRequest
import com.jar.internal.library.jar_core_network.api.util.Serializer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject

@HiltViewModel
internal class DetectedSpendStepsFragmentViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val serializer: Serializer
) : ViewModel() {

    private val _stepsLiveData = MutableLiveData<DetectedSpendPaymentInfoStep>()
    val stepsLiveData: LiveData<DetectedSpendPaymentInfoStep>
        get() = _stepsLiveData

    fun getSteps(contextRef: WeakReference<Context>) {
        val initiateManualPaymentRequest =
            serializer.decodeFromString<com.jar.app.feature_round_off.shared.domain.model.InitiateDetectedRoundOffsPaymentRequest>(
                decodeUrl(
                    savedStateHandle.get<String>("initiateManualPaymentRequest")!!
                )
            )
        viewModelScope.launch {
            flow {
                delay(300)
                emit(
                    DetectedSpendPaymentInfoStep(
                        contextRef.get()!!.getString(
                            R.string.feature_homepage_rs_n_double_will_be_deducted_from_your_account,
                            initiateManualPaymentRequest.txnAmt
                        ),
                        "${BaseConstants.CDN_BASE_URL}/DetectedSpendPaymentStepInfo/ic_upi.png"
                    )
                )
                delay(600)
                emit(
                    DetectedSpendPaymentInfoStep(
                        contextRef.get()!!
                            .getString(R.string.feature_homepage_saved_to_your_own_savings_jar),
                        "${BaseConstants.CDN_BASE_URL}/DetectedSpendPaymentStepInfo/ic_coin_jar.png"
                    )
                )
                delay(600)
                emit(
                    DetectedSpendPaymentInfoStep(
                        contextRef.get()!!
                            .getString(R.string.feature_homepage_invested_in_100_perc_pure_gold),
                        "${BaseConstants.CDN_BASE_URL}/DetectedSpendPaymentStepInfo/ic_gold_brick.png"
                    )
                )
            }.flowOn(Dispatchers.IO)
                .collect {
                    _stepsLiveData.postValue(DetectedSpendPaymentInfoStep(it.title, it.icon))
                }
        }
    }
}