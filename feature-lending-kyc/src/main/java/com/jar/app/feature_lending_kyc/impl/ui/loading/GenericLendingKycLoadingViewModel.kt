package com.jar.app.feature_lending_kyc.impl.ui.loading

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.jar.app.base.data.livedata.SingleLiveEvent
import com.jar.app.feature_lending_kyc.shared.domain.model.AssetUrl
import com.jar.app.feature_lending_kyc.shared.domain.model.ProgressDismiss
import com.jar.app.feature_lending_kyc.shared.domain.model.ProgressDismissResult
import com.jar.app.feature_lending_kyc.shared.domain.model.ProgressSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class GenericLendingKycLoadingViewModel @Inject constructor() : ViewModel() {

    private val _genericLoadingTitleViewModel = SingleLiveEvent<String>()
    val genericLoadingTitleViewModel: LiveData<String>
        get() = _genericLoadingTitleViewModel

    private val _genericLoadingDescriptionViewModel = SingleLiveEvent<String>()
    val genericLoadingDescriptionViewModel: LiveData<String>
        get() = _genericLoadingDescriptionViewModel

    private val _genericLoadingAutoDismissAfterMillisViewModel = SingleLiveEvent<ProgressDismiss>()
    val genericLoadingAutoDismissAfterMillisViewModel: LiveData<ProgressDismiss>
        get() = _genericLoadingAutoDismissAfterMillisViewModel

    private val _genericLoadingOnAutoDismissViewModel = SingleLiveEvent<ProgressDismissResult>()
    val genericLoadingOnAutoDismissViewModel: LiveData<ProgressDismissResult>
        get() = _genericLoadingOnAutoDismissViewModel

    private val _genericLoadingShowSuccessViewModel = SingleLiveEvent<ProgressSuccess>()
    val genericLoadingShowSuccessViewModel: LiveData<ProgressSuccess>
        get() = _genericLoadingShowSuccessViewModel

    private val _assetUrlViewModel = SingleLiveEvent<AssetUrl>()
    val assetUrlViewModel: LiveData<AssetUrl>
        get() = _assetUrlViewModel

    fun updateAssetUrl(assetUrl: String, isIllustrationUrl: Boolean = false) {
        _assetUrlViewModel.postValue(AssetUrl(assetUrl, isIllustrationUrl))
    }

    fun updateGenericLoadingTitle(title: String) {
        _genericLoadingTitleViewModel.postValue(title)
    }

    fun updateGenericLoadingDescription(description: String) {
        _genericLoadingDescriptionViewModel.postValue(description)
    }

    fun dismissGenericLoadingAfterMillis(
        autoDismissMillis: Long,
        isDismissingAfterSuccess: Boolean = false,
        from: String = ""
    ) {
        _genericLoadingAutoDismissAfterMillisViewModel.postValue(
            ProgressDismiss(autoDismissMillis, isDismissingAfterSuccess, from)
        )
    }

    fun onAutoDismiss(isDismissed: Boolean, from: String) {
        _genericLoadingOnAutoDismissViewModel.postValue(ProgressDismissResult(isDismissed, from))
    }

    fun showProgressSuccess(successTitle: String, successLottieUrl: String) {
        _genericLoadingShowSuccessViewModel.postValue(
            ProgressSuccess(
                successTitle,
                successLottieUrl
            )
        )
    }

}