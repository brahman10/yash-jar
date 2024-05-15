package com.jar.app.feature_settings.impl.ui.security_shield

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.core_preferences.api.PrefsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class SecurityShieldViewModel @Inject constructor(
    private val prefs: PrefsApi
) : ViewModel() {

    private val _updateSecurityShieldLiveData = MutableLiveData<Boolean>()
    val updateSecurityShieldLiveData: LiveData<Boolean>
        get() = _updateSecurityShieldLiveData

    fun toggleJarShieldStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            prefs.setJarShieldStatus(prefs.isJarShieldEnabled().not())
            _updateSecurityShieldLiveData.postValue(prefs.isJarShieldEnabled())
        }
    }

}