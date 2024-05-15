package com.jar.app.feature_contacts_sync_common.impl.ui.contact_list

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jar.app.base.util.orFalse
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.StringUtils.isValidIndianPhoneNumberWithoutExtension
import com.jar.app.feature_contact_sync_common.shared.ContactsSyncShowContactListViewModel
import com.jar.app.feature_contact_sync_common.shared.ContactsSyncShowContactListViewModel.Companion.DEFAULT_CONTACT_NAME
import com.jar.app.feature_contact_sync_common.shared.domain.model.LocalContact
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.AddContactsUseCase
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.FetchContactListStaticDataUseCase
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.FetchContactListUseCase
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.FetchContactProcessingStatusUseCase
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.FetchSentInviteListUseCase
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.SendInviteUseCase
import contacts.core.Contacts
import contacts.core.util.phoneList
import dagger.hilt.android.lifecycle.HiltViewModel
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import javax.inject.Inject

@HiltViewModel
class ContactsSyncShowContactListViewModelAndroid @Inject constructor(
    private val sendInviteUseCase: SendInviteUseCase,
    private val addContactsUseCase: AddContactsUseCase,
    private val fetchContactListUseCase: FetchContactListUseCase,
    private val fetchContactProcessingStatusUseCase: FetchContactProcessingStatusUseCase,
    private val fetchContactListStaticDataUseCase: FetchContactListStaticDataUseCase,
    private val fetchSentInviteListUseCase: FetchSentInviteListUseCase,
    private val phoneNumberUtil: PhoneNumberUtil,
) : ViewModel() {

    private val viewModel by lazy {
        ContactsSyncShowContactListViewModel(
            sendInviteUseCase,
            addContactsUseCase,
            fetchContactListUseCase,
            fetchContactProcessingStatusUseCase,
            fetchContactListStaticDataUseCase,
            fetchSentInviteListUseCase,
            viewModelScope
        )
    }

    fun getInstance() = viewModel

    fun fetchLocalContactsAndUploadToServer(mApp: Context) {
        val finalList = ArrayList<LocalContact>()
        val contacts = Contacts(mApp.applicationContext).query().find()
        contacts.forEach { contact ->
            if (contact.hasPhoneNumber.orFalse()) {
                contact.phoneList().forEachIndexed { index, phone ->
                    if (!phone.normalizedNumber.isNullOrBlank()) {
                        try {
                            val numberData = phoneNumberUtil.parse(
                                phone.normalizedNumber, BaseConstants.REGION_CODE
                            )

                            if (isValidIndianPhoneNumberWithoutExtension(numberData.nationalNumber.toString(), numberData.countryCode)) {
                                val contactData =
                                    LocalContact(
                                        name = contact.displayNamePrimary ?: DEFAULT_CONTACT_NAME,
                                        countryCode = numberData.countryCode.toString(),
                                        phoneNumber = numberData.nationalNumber.toString()
                                    )
                                finalList.add(contactData)
                            }
                        } catch (e: Exception) {
//                                Timber.d("${e.message}")
                        }
                    }
                }
            }
        }

        viewModel.fetchLocalContactsAndUploadToServer(finalList)
    }
}