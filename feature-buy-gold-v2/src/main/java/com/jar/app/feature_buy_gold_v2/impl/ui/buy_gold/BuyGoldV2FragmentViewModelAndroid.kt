package com.jar.app.feature_buy_gold_v2.impl.ui.buy_gold

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.BuyGoldUseCase
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.FetchAuspiciousTimeUseCase
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.FetchContextBannerUseCase
import com.jar.app.feature_buy_gold_v2.shared.domain.use_case.FetchSuggestedAmountUseCase
import com.jar.app.feature_buy_gold_v2.shared.ui.BuyGoldV2FragmentViewModel
import com.jar.app.feature_coupon_api.domain.use_case.ApplyCouponUseCase
import com.jar.app.feature_coupon_api.domain.use_case.FetchCouponCodeUseCase
import com.jar.app.feature_exit_survey.shared.domain.use_case.impl.FetchExitSurveyQuestionsUseCase
import com.jar.app.feature_gold_price.shared.domain.use_case.FetchCurrentGoldPriceUseCase
import com.jar.internal.library.jar_core_network.api.util.Serializer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class BuyGoldV2FragmentViewModelAndroid @Inject constructor(
    private val fetchAuspiciousTimeUseCase: FetchAuspiciousTimeUseCase,
    private val fetchCurrentGoldPriceUseCase: FetchCurrentGoldPriceUseCase,
    private val buyGoldUseCase: BuyGoldUseCase,
    private val fetchSuggestedAmountUseCase: FetchSuggestedAmountUseCase,
    private val fetchCouponCodeUseCase: FetchCouponCodeUseCase,
    private val applyCouponUseCase: ApplyCouponUseCase,
    private val fetchContextBannerUseCase: FetchContextBannerUseCase,
    private val fetchExitSurveyQuestionsUseCase: FetchExitSurveyQuestionsUseCase,
    private val serializer: Serializer
    ) : ViewModel() {

    private val viewModel by lazy {
        BuyGoldV2FragmentViewModel(
            fetchAuspiciousTimeUseCase,
            fetchCurrentGoldPriceUseCase,
            buyGoldUseCase,
            fetchSuggestedAmountUseCase,
            fetchCouponCodeUseCase,
            applyCouponUseCase,
            fetchContextBannerUseCase,
            fetchExitSurveyQuestionsUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel
}