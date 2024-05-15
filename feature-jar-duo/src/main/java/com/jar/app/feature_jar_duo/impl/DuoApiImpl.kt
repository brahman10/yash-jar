package com.jar.app.feature_jar_duo.impl

import android.net.Uri
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import com.jar.app.base.ui.BaseNavigation
import com.jar.app.base.util.DispatcherProvider
import com.jar.app.base.util.orFalse
import com.jar.app.core_base.util.orZero
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.feature_contacts_sync_common.api.ContactsSyncApi
import com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType
import com.jar.app.feature_jar_duo.api.DuoApi
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.FetchContactListUseCase
import com.jar.app.feature_jar_duo.shared.domain.use_case.FetchGroupListUseCase
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.app.feature_contact_sync_common.shared.domain.usecases.FetchPendingInvitesUseCase
import com.jar.app.feature_jar_duo.impl.util.DeeplinkUtils
import com.jar.app.feature_jar_duo.shared.util.DuoConstants.SOURCE_HOME
import com.jar.app.feature_jar_duo.shared.util.DuoEventKey
import dagger.Lazy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class DuoApiImpl @Inject constructor(
    private val activity: FragmentActivity,
    private val navControllerRef: Lazy<NavController>,
    private val prefs: PrefsApi,
    private val contactsSyncApi: ContactsSyncApi,
    private val fetchPendingInvitesUseCase: FetchPendingInvitesUseCase,
    private val groupsUseCase: FetchGroupListUseCase,
    private val fetchContactListUseCase: FetchContactListUseCase,
    private val appScope: CoroutineScope,
    private val dispatcherProvider: DispatcherProvider
) : DuoApi, BaseNavigation {

    private val navController by lazy {
        navControllerRef.get()
    }

    private var openDuoJob: Job? = null

    private var openInvitationDuoJob: Job? = null

    override fun openDuoFeature(fromScreen: String, hasContactPermission: Boolean) {
        openDuoJob?.cancel()
        openDuoJob = appScope.launch(dispatcherProvider.io) {
            fetchPendingInvitesUseCase.fetchPendingInvites(com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType.DUO).zip(
                groupsUseCase.fetchGroupList()
            ) { f1, f2 -> Pair(f1, f2) }
                .zip(fetchContactListUseCase.fetchContactListFlow(0, 20, com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType.DUO, null)) { f1, f2 ->
                    Pair(
                        f1,
                        f2
                    )
                }.collectLatest {
                    val pendingInvites = it.first.first
                    val groupData = it.first.second
                    val contactData = it.second
                    if (pendingInvites.status == RestClientResult.Status.SUCCESS && groupData.status == RestClientResult.Status.SUCCESS && contactData.status == RestClientResult.Status.SUCCESS) {
                        if (activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                            withContext(dispatcherProvider.main) {
                                val hasPendingInvites = !pendingInvites.data?.data?.list.isNullOrEmpty()
                                val hasGroups = !groupData.data?.data.isNullOrEmpty()
                                val hasDuoContacts = !contactData.data?.data?.duoContactsListRespList.isNullOrEmpty()
                                val hasSyncedContacts = contactData.data?.data?.isContactSynced ?: false

                                when {
                                    hasSyncedContacts -> {
                                        when {
                                            hasPendingInvites || hasGroups -> openDuoList()
                                            else -> openDuoContactList(pendingInvites.data?.data?.list?.size.orZero(),
                                                    groupData.data?.data?.size.orZero())
                                        }
                                    }
                                    else -> {
                                        when {
                                            hasGroups  -> openDuoList()
                                            else -> {
                                                when {
                                                    prefs.getDuoStoryViewCount() < 2 -> {
                                                        openDuoIntroStory(
                                                            SOURCE_HOME,
                                                            pendingInvites.data?.data?.list?.size.orZero(),
                                                            groupData.data?.data?.size.orZero(),
                                                            contactData.data?.data?.isContactSynced.orFalse()
                                                        )
                                                    }

                                                    else -> {
                                                        contactsSyncApi.openPermissionFlow(
                                                            com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType.DUO,
                                                            "android-app://com.jar.app/duoList"
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
        }
    }

    override fun openInvitationPopupIfPendingInvites() {
        openInvitationDuoJob?.cancel()
        openInvitationDuoJob = appScope.launch {
            fetchPendingInvitesUseCase.fetchPendingInvites(com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType.DUO).collect(
                onSuccess = {
                    if (!it.list.isNullOrEmpty()) {
                        if (activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                            withContext(dispatcherProvider.main) {
                                navController.navigate(
                                    Uri.parse("android-app://com.jar.app/duoPendingInvitation"),
                                    getNavOptions(shouldAnimate = true)
                                )
                            }
                        }
                    }
                }
            )
        }
    }

    override fun openDuoContactList(pendingInvitesCount: Int, duoCount: Int) {
        contactsSyncApi.initiateContactsSyncFlow(
            com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType.DUO,
            DeeplinkUtils.generateStoryDeeplink(
                DuoEventKey.SCREEN_INTRO,
                pendingInvitesCount.toString(),
                duoCount.toString(),
                false.toString()
            )
        )
    }

    override fun openPendingInviteList() {
        navController.navigate(
            Uri.parse("android-app://com.jar.app/duoPendingInviteList"),
            getNavOptions(shouldAnimate = true)
        )
    }

    override fun openDuoIntroStory(fromScreen: String, pendingInvites: Int, duoGroups: Int,hasContactSynced: Boolean) {
        val deepLink = NavDeepLinkRequest.Builder
            .fromUri(Uri.parse("android-app://com.jar.app/duoIntroStory?fromScreen=$fromScreen&pendingInvites=${pendingInvites}&duoGroups=${duoGroups}&hasContactSynced=${hasContactSynced}"))
            .build()
        navController.navigate(
            deepLink,
            getNavOptions(shouldAnimate = true)
        )
    }

    override fun openDuoList() {
        navController.navigate(
            Uri.parse("android-app://com.jar.app/duoList"),
            getNavOptions(shouldAnimate = true)
        )
    }
}