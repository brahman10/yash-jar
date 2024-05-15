package com.jar.refer_earn_v2.impl.ui.refer_earn_intro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_refer_earn_v2.shared.domain.use_case.FetchReferralIntroStaticDataUseCase
import com.jar.app.feature_refer_earn_v2.shared.domain.use_case.FetchReferralsShareMessageUseCase
import com.jar.app.feature_refer_earn_v2.shared.domain.use_case.FetchReferralsUseCase
import com.jar.app.feature_refer_earn_v2.shared.ui.ReferEarnIntroViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReferEarnIntroViewModelAndroid @Inject constructor(
    private val fetchReferralIntroStaticDataUseCase: FetchReferralIntroStaticDataUseCase,
    private val fetchReferralsUseCase: FetchReferralsUseCase,
    private val fetchReferralsShareMessageUseCase: FetchReferralsShareMessageUseCase
) : ViewModel() {

    private val viewModel by lazy {
        ReferEarnIntroViewModel(
            fetchReferralIntroStaticDataUseCase = fetchReferralIntroStaticDataUseCase ,
            fetchReferralsUseCase = fetchReferralsUseCase ,
            fetchReferralsShareMessageUseCase = fetchReferralsShareMessageUseCase ,
            coroutineScope = viewModelScope
        )
    }
    fun getInstance() = viewModel

}