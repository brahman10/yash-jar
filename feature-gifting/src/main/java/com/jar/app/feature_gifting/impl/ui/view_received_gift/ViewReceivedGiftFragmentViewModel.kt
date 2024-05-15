package com.jar.app.feature_gifting.impl.ui.view_received_gift

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_gifting.shared.domain.use_case.MarkGiftViewedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class ViewReceivedGiftFragmentViewModel @Inject constructor(
    private val markGiftViewedUseCase: MarkGiftViewedUseCase
) : ViewModel() {

    fun markGiftViewed(giftingId: String) {
        viewModelScope.launch {
            markGiftViewedUseCase.markReceivedGiftViewed(giftingId).collectLatest {

            }
        }
    }
}