package com.jar.app.feature.notification_list.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.feature_in_app_notification.shared.domain.use_case.FetchNotificationUseCase
import com.jar.app.feature_in_app_notification.shared.ui.NotificationListFragmentViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class  NotificationListFragmentViewModelAndroid @Inject constructor(
    fetchNotificationUseCase: FetchNotificationUseCase,
) : ViewModel() {

    private val viewModel by lazy {
        NotificationListFragmentViewModel(fetchNotificationUseCase,viewModelScope)
    }

    fun getInstance() = viewModel

}