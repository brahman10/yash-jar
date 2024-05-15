package com.jar.app.feature_jar_duo.impl.ui.duo_list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jar.app.base.data.event.RefreshDuoHomeCardEvent
import com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType
import com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListResponse
import com.jar.app.feature_contact_sync_common.shared.domain.model.PendingInviteResponse
import com.jar.app.feature_contact_sync_common.shared.domain.model.ProcessInviteRequest
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.FetchContactListUseCase
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.FetchContactProcessingStatusUseCase
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.FetchPendingInvitesUseCase
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.ProcessInviteUseCase
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.SendInviteUseCase
import com.jar.app.feature_jar_duo.shared.domain.use_case.FetchGroupListUseCase
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@HiltViewModel
internal class DuosListViewModel @Inject constructor(
    private val sendInviteUseCase: SendInviteUseCase,
    private val processInviteUseCase: ProcessInviteUseCase,
    private val fetchGroupListUseCase: FetchGroupListUseCase,
    private val fetchContactListUseCase: FetchContactListUseCase,
    private val fetchPendingInvitesUseCase: FetchPendingInvitesUseCase,
    private val fetchContactProcessingStatusUseCase: FetchContactProcessingStatusUseCase,
    private val mApp: Application,
) : AndroidViewModel(mApp) {

    companion object {
        private const val NETWORK_PAGE_SIZE = 20
    }

    private val _sendInviteLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<Unit?>>>()
    val sendInviteLiveData: LiveData<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _sendInviteLiveData

    private val _mergedInviteAndGroupsLiveData =
        MutableLiveData<Triple<RestClientResult<ApiResponseWrapper<List<com.jar.app.feature_jar_duo.shared.domain.model.DuoGroupData>>>, RestClientResult<ApiResponseWrapper<com.jar.app.feature_contact_sync_common.shared.domain.model.PendingInviteResponse>>, RestClientResult<ApiResponseWrapper<com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListResponse?>>>>()
    val mergedListAndGroupsLiveData: LiveData<Triple<RestClientResult<ApiResponseWrapper<List<com.jar.app.feature_jar_duo.shared.domain.model.DuoGroupData>>>, RestClientResult<ApiResponseWrapper<com.jar.app.feature_contact_sync_common.shared.domain.model.PendingInviteResponse>>, RestClientResult<ApiResponseWrapper<com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListResponse?>>>>
        get() = _mergedInviteAndGroupsLiveData

    private val _pendingInviteLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<com.jar.app.feature_contact_sync_common.shared.domain.model.PendingInviteResponse>>>()
    val pendingInviteDataLiveData: LiveData<RestClientResult<ApiResponseWrapper<com.jar.app.feature_contact_sync_common.shared.domain.model.PendingInviteResponse>>>
        get() = _pendingInviteLiveData

    private val _contactSyncedLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<String>>>()
    val contactSyncedLiveData: LiveData<RestClientResult<ApiResponseWrapper<String>>>
        get() = _contactSyncedLiveData

    private val _processInviteLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<Unit?>>>()
    val processInviteLiveData: LiveData<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _processInviteLiveData


    private val _contactListLiveData =
        MutableLiveData<RestClientResult<ApiResponseWrapper<com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListResponse?>>>()
    val contactListLiveData: LiveData<RestClientResult<ApiResponseWrapper<com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListResponse?>>>
        get() = _contactListLiveData

    fun processInvite(processInviteRequest: com.jar.app.feature_contact_sync_common.shared.domain.model.ProcessInviteRequest) {
        viewModelScope.launch {
            EventBus.getDefault().post(RefreshDuoHomeCardEvent())
            processInviteUseCase.processInvite(processInviteRequest).collectLatest {
                _processInviteLiveData.postValue(it)
            }
        }
    }

    fun getMergedInviteAndListData() {
        viewModelScope.launch {
            fetchPendingInvitesUseCase.fetchPendingInvites(com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType.DUO).zip(
                fetchGroupListUseCase.fetchGroupList()
            ) { f1, f2 -> Pair(f1, f2) }
                .zip(fetchContactListUseCase.fetchContactListFlow(0, 20, com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType.DUO,null)) { f1, f2 ->
                    Pair(
                        f1,
                        f2
                    )
                }.collectLatest {
                    val pendingInvites = it.first.first
                    val groupData = it.first.second
                    val contactData = it.second
                    _mergedInviteAndGroupsLiveData.postValue(
                        Triple(
                            groupData,
                            pendingInvites,
                            contactData
                        )
                    )
                }
        }
    }

    fun getPendingInviteListData() {
        viewModelScope.launch {
            fetchPendingInvitesUseCase.fetchPendingInvites(com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType.DUO).collectLatest {
                _pendingInviteLiveData.postValue(it)
            }
        }
    }

    fun sendInvite(number: String, featureType: com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType, referralLink: String) {
        viewModelScope.launch {
            sendInviteUseCase.sendInvite(number, featureType, referralLink).collectLatest {
                _sendInviteLiveData.postValue(it)
            }
        }
    }

    fun fetchContactProcessingStatus(syncDelay: Long = 0) {
        viewModelScope.launch {
            delay(syncDelay)
            fetchContactProcessingStatusUseCase.fetchContactProcessingStatus().collectLatest {
                _contactSyncedLiveData.postValue(it)
            }
        }
    }

    fun fetchContactsWithoutPaging() {
        viewModelScope.launch {
            fetchContactListUseCase.fetchContactListFlow(0, 20, com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType.DUO, null).collectLatest {
                _contactListLiveData.postValue(it)
            }
        }
    }
}