package com.jar.app.feature_gold_lease.impl.ui.kyc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.feature_gold_lease.shared.ui.GoldLeaseKycViewModel
import com.jar.app.feature_kyc.shared.domain.use_case.FetchKycDetailsUseCase
import com.jar.app.feature_kyc.shared.domain.use_case.PostManualKycRequestUseCase
import com.jar.app.feature_lending_kyc.shared.api.use_case.FetchKycProgressUseCase
import com.jar.app.feature_user_api.domain.use_case.FetchUserKycStatusUseCase
import com.jar.app.feature_user_api.domain.use_case.UpdateUserUseCase
import com.jar.internal.library.jar_core_network.api.util.Serializer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class GoldLeaseKycViewModelAndroid @Inject constructor(
    fetchUserKycStatusUseCase: FetchUserKycStatusUseCase,
    fetchKycProgressUseCase: FetchKycProgressUseCase,
    fetchKycDetailsUseCase: FetchKycDetailsUseCase,
    postManualKycRequestUseCase: PostManualKycRequestUseCase,
    updateUserUseCase: UpdateUserUseCase,
    prefs: PrefsApi,
    serializer: Serializer
): ViewModel() {

    private val viewModel by lazy {
        GoldLeaseKycViewModel(
            fetchUserKycStatusUseCase,
            fetchKycProgressUseCase,
            fetchKycDetailsUseCase,
            postManualKycRequestUseCase,
            updateUserUseCase,
            prefs,
            serializer,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}