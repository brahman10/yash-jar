package com.jar.app.feature_lending.impl.ui.realtime_flow.bottom_sheet

import androidx.lifecycle.ViewModel
import com.jar.app.feature_lending.shared.domain.ui_event.OtpCharacterEnteredEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject


@HiltViewModel
internal class OtpBottomSheetViewModel @Inject constructor(
) : ViewModel() {
    val otp = ""
    var state = MutableStateFlow(otp)


    fun uiEvent(event: OtpCharacterEnteredEvent) {

        when (event) {
            is OtpCharacterEnteredEvent.Char -> {
                state.value = event.char

            }


        }

    }

}





