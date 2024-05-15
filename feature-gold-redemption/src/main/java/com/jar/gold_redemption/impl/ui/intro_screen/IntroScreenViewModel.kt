package com.jar.gold_redemption.impl.ui.intro_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.core_compose_ui.views.ExpandableCardModel
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherGoldRedemptionIntroPart2UseCase
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherGoldRedemptionIntroUseCase
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.app.feature_gold_redemption.shared.data.network.model.IntroScreenAPIDataPart2
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherFaqsUseCase
import com.jar.gold_redemption.impl.ui.faq_screen.curateToExpandableList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
internal class IntroScreenViewModel @Inject constructor(
    private val voucherGoldRedemptionIntroUseCase: VoucherGoldRedemptionIntroUseCase,
    private val voucherGoldRedemptionIntroPart2UseCase: VoucherGoldRedemptionIntroPart2UseCase,
    private val voucherFaqsUseCase: VoucherFaqsUseCase,
) : ViewModel() {

    private val _isScreenExpanded = MutableLiveData<Boolean>(true)
    val isScreenExpanded: LiveData<Boolean> = _isScreenExpanded

    private val _isLoadingShown = MutableLiveData<Boolean>(false)
    val isLoadingShown: LiveData<Boolean> = _isLoadingShown

    private val _faqList = MutableLiveData<List<ExpandableCardModel>>()
    val faqList: LiveData<List<ExpandableCardModel>> = _faqList

    private val _brandPartnersList = MutableLiveData<List<String?>>()
    val brandPartnersList: LiveData<List<String?>> = _brandPartnersList

    private val _goldDiamondTableImageLink = MutableLiveData<String?>()
    val goldDiamondTableImageLink: LiveData<String?> = _goldDiamondTableImageLink

    private val _myOrdersText = MutableLiveData<String?>()
    val myOrdersText: LiveData<String?> = _myOrdersText

    private val _expandadedContentData = MutableLiveData<IntroScreenAPIDataPart2?>()
    val expandadedContentData: LiveData<IntroScreenAPIDataPart2?> = _expandadedContentData

    private val _showLoading = MutableLiveData<Boolean>(false)
    val showLoading: LiveData<Boolean> = _showLoading
    var apiResponseCount = 0

    private fun showLoading(b: Boolean) {
        _showLoading.value = b
    }
    fun fetchIntroScreen() {
        viewModelScope.launch {
            voucherGoldRedemptionIntroUseCase.fetchGoldRedemptionIntro().collect(
                onLoading = {
                    showLoading(true)
                },
                onSuccess = {
                    showLoading(false)
                    it?.brandPartnersImageList?.let {
                        _brandPartnersList.postValue(it)
                    }
                    it?.goldDiamondTableImage?.let {
                        _goldDiamondTableImageLink.postValue(it)
                    }
                    it?.myVouchersText?.let {
                        _myOrdersText.value = it
                    }
                }
            )
        }
    }

    fun fetchIntroScreen2() {
        viewModelScope.launch {
            voucherGoldRedemptionIntroPart2UseCase.fetchGoldRedemptionIntroPart2().collect(
                onLoading = {
                    showLoading(true)
                },
                onSuccess = {
                    showLoading(false)
                    _expandadedContentData.value = it
                }
            )
        }
    }

    fun fetchFaqs() {
        viewModelScope.launch {
            voucherFaqsUseCase.fetchFaqs().collect(
                onLoading = {
                    showLoading(true)
                },
                onSuccess = {
                    showLoading(false)
                    it?.let {
                        _faqList.value = curateToExpandableList(it)
                    }
                }
            )
        }
    }
}
