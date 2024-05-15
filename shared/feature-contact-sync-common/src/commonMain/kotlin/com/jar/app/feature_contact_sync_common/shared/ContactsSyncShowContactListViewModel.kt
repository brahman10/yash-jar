package com.jar.app.feature_contact_sync_common.shared

import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_base.util.orZero
import com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType
import com.jar.app.feature_contact_sync_common.shared.domain.model.ServerContact
import com.jar.app.feature_contact_sync_common.shared.domain.model.ServerContactHeader
import com.jar.app.feature_contact_sync_common.shared.domain.model.ServerContactList
import com.jar.internal.library.jar_core_network.api.model.ApiResponseWrapper
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.feature_contact_sync_common.shared.domain.model.AddContactRequest
import com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListStaticDataResponse
import com.jar.app.feature_contact_sync_common.shared.domain.model.ContactsSyncPendingInvitesSentObject
import com.jar.app.feature_contact_sync_common.shared.domain.model.LocalContact
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.AddContactsUseCase
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.SendInviteUseCase
import com.jar.app.feature_contact_sync_common.shared.domain.model.MultipleInviteRequest
import com.jar.app.feature_contact_sync_common.shared.domain.model.SentInviteHeader
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.FetchContactProcessingStatusUseCase
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.FetchSentInviteListUseCase
import com.jar.app.feature_contact_sync_common.shared.domain.model.SentInviteList
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.FetchContactListUseCase
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.FetchContactListStaticDataUseCase
import com.jar.internal.library.jar_core_kmm_flow.CFlow
import com.jar.internal.library.jar_core_kmm_flow.CStateFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonFlow
import com.jar.internal.library.jar_core_kmm_flow.toCommonStateFlow
import com.kuuurt.paging.multiplatform.PagingConfig
import com.kuuurt.paging.multiplatform.PagingData
import com.kuuurt.paging.multiplatform.PagingResult
import com.kuuurt.paging.multiplatform.helpers.cachedIn
import com.kuuurt.paging.multiplatform.insertPagingSeparators
import com.kuuurt.paging.multiplatform.map
import dev.icerock.moko.resources.StringResource
import io.ktor.util.logging.KtorSimpleLogger
import io.ktor.util.logging.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ContactsSyncShowContactListViewModel constructor(
    private val sendInviteUseCase: SendInviteUseCase,
    private val addContactsUseCase: AddContactsUseCase,
    private val fetchContactListUseCase: FetchContactListUseCase,
    private val fetchContactProcessingStatusUseCase: FetchContactProcessingStatusUseCase,
    private val fetchContactListStaticDataUseCase: FetchContactListStaticDataUseCase,
    private val fetchSentInviteListUseCase: FetchSentInviteListUseCase,
    coroutineScope: CoroutineScope?
) {
    private val viewModelScope = coroutineScope ?: CoroutineScope(Dispatchers.Main)

    companion object {
        private const val NETWORK_PAGE_SIZE = 20
        public const val DEFAULT_CONTACT_NAME = "Unknown"
    }

    private var featureType: ContactListFeatureType? = ContactListFeatureType.REFERRALS
    internal val _fetchLocalContactAndUploadToServerLiveData =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<Unit?>>>()
    val fetchLocalContactAndUploadToServerLiveData: CFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _fetchLocalContactAndUploadToServerLiveData.toCommonFlow()

    private val _contactSyncedLiveData =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<String>>>()
    val contactSyncedLiveData: CFlow<RestClientResult<ApiResponseWrapper<String>>>
        get() = _contactSyncedLiveData.toCommonFlow()

    private val _contactListStaticData =
        MutableStateFlow<RestClientResult<ApiResponseWrapper<ContactListStaticDataResponse?>>>(
            RestClientResult.none()
        )
    val contactListStaticData: CStateFlow<RestClientResult<ApiResponseWrapper<ContactListStaticDataResponse?>>>
        get() = _contactListStaticData.toCommonStateFlow()

    private val _sendInviteLiveData =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<Unit?>>>()
    val sendInviteLiveData: CFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _sendInviteLiveData.toCommonFlow()

    private val _sendMultipleInviteLiveData =
        MutableSharedFlow<RestClientResult<ApiResponseWrapper<Unit?>>>()
    val sendMultipleInviteLiveData: CFlow<RestClientResult<ApiResponseWrapper<Unit?>>>
        get() = _sendMultipleInviteLiveData.toCommonFlow()

    private val selectedContactMap = mutableMapOf<String, ServerContact>()

    private val _selectedContactSize = MutableSharedFlow<Int>()

    val selectedContactSize: CFlow<Int>
        get() = _selectedContactSize.toCommonFlow()

    private val _noOfSentInvites = MutableSharedFlow<Int>()

    val noOfSentInvites: CFlow<Int>
        get() = _noOfSentInvites.toCommonFlow()

    var isMultiSelectEnabled = false

    var isSelectAllEnabled = false

    private var fetchContactJob: Job? = null

    private val _contactListFlow = MutableStateFlow<PagingData<ServerContactList>?>(null)
    val contactListFlow: CStateFlow<PagingData<ServerContactList>?>
        get() = _contactListFlow.toCommonStateFlow()

    private val _sentInviteListFlow = MutableStateFlow<PagingData<SentInviteList>?>(null)
    val sentInviteListFlow: CStateFlow<PagingData<SentInviteList>?>
        get() = _sentInviteListFlow.toCommonStateFlow()


    var contactsPager: com.kuuurt.paging.multiplatform.Pager<Int, ServerContactList>? = null

    var sentInvitePager: com.kuuurt.paging.multiplatform.Pager<Int, SentInviteList>? =
        null

    fun toggleMultiSelectIfNeeded() {
        isMultiSelectEnabled = selectedContactMap.isNotEmpty()
    }

    fun toggleSelectedState(
        serverContact: ServerContact, toggleUI: (newSelectedState: Boolean) -> Unit
    ) {
        viewModelScope.launch {
            serverContact.isSelected = serverContact.isSelected?.not()
            toggleUI.invoke(serverContact.isSelected.orFalse())

            if (selectedContactMap.containsKey(serverContact.id)) {
                selectedContactMap.remove(serverContact.id)
                _selectedContactSize.emit(selectedContactMap.size)
            } else {
                selectedContactMap[serverContact.id] = serverContact
                _selectedContactSize.emit(selectedContactMap.size)
            }
            toggleMultiSelectIfNeeded()
        }
    }

    fun getSelectedContacts(): Map<String, ServerContact> = selectedContactMap
    fun clearSelectedContacts() {
        selectedContactMap.clear()
    }

    fun updateSelectedContactSize() {
        viewModelScope.launch {
            _selectedContactSize.emit(selectedContactMap.size)
        }
    }

    fun getSelectedContactSize(): Int {
        return selectedContactMap.size
    }

    fun fetchContacts(query: String?, generateStringFunction: (id: StringResource) -> String) {
        fetchContactJob?.cancel()
        fetchContactJob = viewModelScope.launch {
            contactsPager = com.kuuurt.paging.multiplatform.Pager(
                viewModelScope,
                config = PagingConfig(
                    pageSize = NETWORK_PAGE_SIZE,
                    enablePlaceholders = false,
                    initialLoadSize = NETWORK_PAGE_SIZE
                ),
                initialKey = 0,
                getItems = { currentKey, size ->
                    val response = fetchContactListUseCase.fetchContactList(
                        currentKey, size, featureType!!, query
                    )
                    val items = response.data?.data?.duoContactsListRespList.orEmpty()
                        .map {
                            it.copy(isSelected =  isSelectAllEnabled)
                        }
                    PagingResult(items = items,
                        currentKey = currentKey,
                        prevKey = { currentKey - 1 },
                        nextKey = { currentKey + 1 })
                },
            )
            contactsPager!!.pagingData
                .cachedIn(viewModelScope)
                .collectLatest {
                    _contactListFlow.emit(it)
                }
        }
    }

    fun invalidateDataSource() {
        viewModelScope.launch {
            contactsPager?.refresh()
        }
    }

    fun fetchSentInvites(
        showHeaders: Boolean,
        featureType: ContactListFeatureType,
        generateStringFunction: (id: StringResource) -> String
    ) {
        viewModelScope.launch {
            sentInvitePager = com.kuuurt.paging.multiplatform.Pager(
                viewModelScope,
                config = PagingConfig(
                    pageSize = NETWORK_PAGE_SIZE,
                    enablePlaceholders = false,
                    initialLoadSize = NETWORK_PAGE_SIZE
                ),
                initialKey = 0,
                getItems = { currentKey, size ->
                    val response = fetchSentInviteListUseCase.fetchSentInviteList(
                        currentKey, size, featureType
                    )
                    val items = response.data?.data?.contactsSyncPendingInvitesSentObjects.orEmpty()
                        .map { it.copy() }
                    PagingResult(items = items,
                        currentKey = currentKey,
                        prevKey = { currentKey - 1 },
                        nextKey = { currentKey + 1 })
                },
            )
            sentInvitePager?.pagingData?.cachedIn(viewModelScope)?.collectLatest {
                _sentInviteListFlow.emit(it)
            }
        }
    }

    fun getSentInvitesCount() {
        viewModelScope.launch {
            val result =
                fetchSentInviteListUseCase.fetchSentInviteList(page = 0, size = 20, featureType!!)
            if (result.status == RestClientResult.Status.SUCCESS) {
                _noOfSentInvites.emit(result.data?.data?.totalInvitesSent.orZero())
            }
        }
    }

    fun fetchContactProcessingStatus(syncDelay: Long = 0) {
        viewModelScope.launch {
            delay(syncDelay)
            fetchContactProcessingStatusUseCase.fetchContactProcessingStatus().collectLatest {
                _contactSyncedLiveData.emit(it)
            }
        }
    }

    fun fetchContactListStaticData() {
        viewModelScope.launch {
            fetchContactListStaticDataUseCase.fetchContactListStaticData(featureType!!)
                .collectLatest {
                    _contactListStaticData.emit(it)
                }
        }
    }

    fun sendInvite(number: String, referralLink: String) {
        viewModelScope.launch {
            sendInviteUseCase.sendInvite(number, featureType!!, referralLink).collectLatest {
                _sendInviteLiveData.emit(it)
                if (it.status == RestClientResult.Status.SUCCESS)
                    getSentInvitesCount()
            }
        }

    }

    fun sendInviteReminder(
        number: String, referralLink: String, featureType: ContactListFeatureType
    ) {
        viewModelScope.launch {
            sendInviteUseCase.sendInviteReminder(number, referralLink, featureType).collectLatest {
                _sendInviteLiveData.emit(it)
            }
        }
        getSentInvitesCount()
    }

    fun sendMultipleInviteForIOS(
        contactNumbers: List<ServerContact>, searchText: String?, isSelectAllEnabled: Boolean, referralLink: String?
    ){
        viewModelScope.launch {
            sendInviteUseCase.sendMultipleInvite(
                MultipleInviteRequest(
                    inviteePhoneNumberList = contactNumbers.map { it.friendPhoneNumber },
                    searchText = searchText,
                    selectAllEnabled = isSelectAllEnabled,
                    referralLink = referralLink
                )
            ).collectLatest {
                _sendMultipleInviteLiveData.emit(it)
            }
        }
    }

    fun sendMultipleInvite(
        searchText: String?, isSelectAllEnabled: Boolean, referralLink: String?
    ) {
        val contactNumbers =
            selectedContactMap.values//.map { it.getNumberWithPlusSignAndCountryCodeUrlEncoded() }
        viewModelScope.launch {
            sendInviteUseCase.sendMultipleInvite(
                MultipleInviteRequest(
                    inviteePhoneNumberList = contactNumbers.map { it.friendPhoneNumber },
                    searchText = searchText,
                    selectAllEnabled = isSelectAllEnabled,
                    referralLink = referralLink
                )
            ).collectLatest {
                _sendMultipleInviteLiveData.emit(it)
            }
        }
    }

    fun setFeatureType(featureType: String) {
        this.featureType = ContactListFeatureType.valueOf(featureType)
    }

    fun shouldShowHeaders(): Boolean {
        return _contactListStaticData.value?.data?.data?.showListHeaders.orFalse()
    }

    fun fetchLocalContactsAndUploadToServer(finalList: List<LocalContact>) {
        viewModelScope.launch {
            addContactsUseCase.addContacts(AddContactRequest(finalList)).collectLatest {
                _fetchLocalContactAndUploadToServerLiveData.emit(it)
            }
        }
    }
}