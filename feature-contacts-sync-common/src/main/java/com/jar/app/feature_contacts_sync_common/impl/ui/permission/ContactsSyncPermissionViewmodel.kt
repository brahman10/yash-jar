package com.jar.app.feature_contacts_sync_common.impl.ui.permission

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jar.app.base.util.DispatcherProvider
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orFalse
import com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType
import com.jar.app.feature_contact_sync_common.shared.domain.model.AddContactRequest
import com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListStaticDataResponse
import com.jar.app.feature_contact_sync_common.shared.domain.model.LocalContact
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.AddContactsUseCase
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.FetchContactListStaticDataUseCase
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.FetchContactProcessingStatusUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import contacts.core.Contacts
import contacts.core.util.phoneList
import dagger.hilt.android.lifecycle.HiltViewModel
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class ContactsSyncPermissionViewmodel @Inject constructor(
    private val dispatcherProvider: DispatcherProvider,
    private val mApp: Application,
    private val phoneNumberUtil: PhoneNumberUtil,
    private val fetchContactProcessingStatusUseCase: FetchContactProcessingStatusUseCase,
    private val addContactsUseCase: AddContactsUseCase,
    private val fetchContactListStaticDataUseCase: FetchContactListStaticDataUseCase
) : AndroidViewModel(mApp) {

    private val _contactSyncedLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<String>>>()
    val contactSyncedLiveData: LiveData<RestClientResult<ApiResponseWrapper<String>>>
        get() = _contactSyncedLiveData

    private val _contactListStaticData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<ContactListStaticDataResponse?>>>()
    val contactListStaticData: LiveData<RestClientResult<ApiResponseWrapper<ContactListStaticDataResponse?>>>
        get() = _contactListStaticData

    private val _fetchLocalContactAndUploadToServerLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<Unit?>>>()
    val fetchLocalContactAndUploadToServerLiveData: LiveData<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _fetchLocalContactAndUploadToServerLiveData

    fun fetchContactProcessingStatus(syncDelay: Long = 0) {
        viewModelScope.launch {
            delay(syncDelay)
            fetchContactProcessingStatusUseCase.fetchContactProcessingStatus().collectLatest {
                _contactSyncedLiveData.postValue(it)
            }
        }
    }

    fun fetchLocalContactsAndUploadToServer() {
        viewModelScope.launch(dispatcherProvider.io) {
            _fetchLocalContactAndUploadToServerLiveData.postValue(RestClientResult.loading())
            val finalList = ArrayList<LocalContact>()
            val contacts = Contacts(mApp.applicationContext).query().find()
            contacts.forEach { contact ->
                if (contact.hasPhoneNumber.orFalse()) {
                    contact.phoneList().forEachIndexed { index, phone ->
                        if (!phone.normalizedNumber.isNullOrBlank()) {
                            try {
                                val numberData = phoneNumberUtil.parse(
                                    phone.normalizedNumber,
                                    BaseConstants.REGION_CODE
                                )

                                if (numberData.nationalNumber.toString().length == 10 && numberData.countryCode == 91) {
                                    val contactData =
                                        LocalContact(
                                            name = contact.displayNamePrimary ?: "Unknown",
                                            countryCode = numberData.countryCode.toString(),
                                            phoneNumber = numberData.nationalNumber.toString()

                                        )
                                    finalList.add(contactData)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace() // todo use timber
//                                Timber.d("${e.message}")
                            }
                        }
                    }
                }
            }

            addContactsUseCase.addContacts(
                AddContactRequest(
                    finalList
                )
            ).collectLatest {
                _fetchLocalContactAndUploadToServerLiveData.postValue(it)
            }
        }
    }

    fun fetchContactListStaticData(featureType: ContactListFeatureType) {
        viewModelScope.launch {
            fetchContactListStaticDataUseCase.fetchContactListStaticData(featureType).collectLatest {
                _contactListStaticData.postValue(it)
            }
        }
    }
}
