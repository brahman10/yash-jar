package com.jar.gold_redemption.impl.ui.faq_screen


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.core_compose_ui.views.ExpandableCardModel
import com.jar.app.feature_gold_redemption.shared.domain.use_case.VoucherFaqsUseCase
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class FAQsScreenViewModel @Inject constructor(
    private val voucherDiscoveryUseCase: VoucherFaqsUseCase
) : ViewModel() {

    private val _showLoading = MutableLiveData<Boolean>(false)
    val showLoading: LiveData<Boolean> = _showLoading


    private fun showLoading(b: Boolean) {
        _showLoading.value = b
    }
    private val _faqList = MutableLiveData<List<ExpandableCardModel>>()
    val faqList: LiveData<List<ExpandableCardModel>> = _faqList


    fun fetchFaqs() {
        viewModelScope.launch {
            voucherDiscoveryUseCase.fetchFaqs().collect(
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
