package com.jar.app.feature_lending_kyc.impl.ui.aadhaar.manual_entry

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.base.data.livedata.SingleLiveEvent
import com.jar.app.feature_lending_kyc.shared.domain.model.AadhaarCaptcha
import com.jar.app.feature_lending_kyc.shared.domain.use_case.FetchAadhaarCaptchaUseCase
import com.jar.app.feature_lending_kyc.shared.domain.use_case.RequestAadhaarOtpUseCase
import com.jar.app.feature_lending_kyc.shared.ui.aadhaar.manual.AadhaarManualEntryViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
internal class AadhaarManualEntryViewModelAndroid @Inject constructor(
    private val fetchAadhaarCaptchaUseCase: FetchAadhaarCaptchaUseCase,
    private val requestAadhaarOtpUseCase: RequestAadhaarOtpUseCase
) : ViewModel() {

    var aadhaarCaptcha: AadhaarCaptcha? = null

    private val _captchaBitmapLiveData = SingleLiveEvent<Bitmap>()
    val captchaBitmapLiveData: LiveData<Bitmap>
        get() = _captchaBitmapLiveData


    private val viewModel by lazy {
        AadhaarManualEntryViewModel(
            fetchAadhaarCaptchaUseCase = fetchAadhaarCaptchaUseCase,
            requestAadhaarOtpUseCase = requestAadhaarOtpUseCase,
            coroutineScope = viewModelScope
        )
    }

    fun getInstance() = viewModel


    private fun decodeCaptcha(captchaBase64String: String) {
        viewModelScope.launch {
            val captchaImage = withContext(Dispatchers.Default) {
                val decodedData = Base64.decode(captchaBase64String, Base64.DEFAULT)
                return@withContext BitmapFactory.decodeByteArray(
                    decodedData,
                    0,
                    decodedData.size
                )
            }
            _captchaBitmapLiveData.postValue(captchaImage)
        }

    }

    fun getCaptchaImage(aadhaarCaptcha: AadhaarCaptcha) {
        this.aadhaarCaptcha = aadhaarCaptcha
        aadhaarCaptcha.captchaImage?.let {
            decodeCaptcha(it)
        }
    }
}