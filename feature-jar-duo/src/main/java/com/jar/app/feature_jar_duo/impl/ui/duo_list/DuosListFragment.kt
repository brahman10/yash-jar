package com.jar.app.feature_jar_duo.impl.ui.duo_list

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import android.widget.PopupWindow
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieDrawable
import com.jar.app.base.data.event.GoToHomeEvent
import com.jar.app.base.data.event.RefreshDuoHomeCardEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.asInitials
import com.jar.app.base.util.dp
import com.jar.app.base.util.encodeUrl
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_utils.data.AppsFlyerInviteUtil
import com.jar.app.feature_contacts_sync_common.api.ContactsSyncApi
import com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType
import com.jar.app.feature_contact_sync_common.shared.domain.model.AllowAccessData
import com.jar.app.feature_contact_sync_common.shared.domain.model.ProcessInviteRequest
import com.jar.app.feature_contact_sync_common.shared.domain.model.ServerContact
import com.jar.app.feature_jar_duo.R
import com.jar.app.feature_jar_duo.databinding.FeatureDuoDuoListBinding
import com.jar.app.feature_jar_duo.databinding.FeatureDuoPopupWindowBinding
import com.jar.app.feature_jar_duo.shared.domain.model.v2.DuoHeaderData
import com.jar.app.feature_jar_duo.impl.ui.DuoHeaderAdapter
import com.jar.app.feature_contacts_sync_common.impl.ui.contact_list.ContactsSyncShowContactListAdapter
import com.jar.app.feature_jar_duo.impl.util.DeeplinkUtils
import com.jar.app.feature_jar_duo.shared.domain.model.InvitationStage
import com.jar.app.feature_jar_duo.shared.domain.model.RefreshGroupListEvent
import com.jar.app.feature_jar_duo.shared.util.DuoConstants.SOURCE_DUO_LIST
import com.jar.app.feature_jar_duo.shared.util.DuoEventKey
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import permissions.dispatcher.NeedsPermission
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class DuosListFragment : BaseFragment<FeatureDuoDuoListBinding>() {

    private var concatAdapter: ConcatAdapter? = null
    private var requestAdapter: DuoRequestAdapter? = null
    private var groupDuoAdapter: DuoYourDuosAdapter? = null
    private var duoRequestHeaderAdapter: DuoHeaderAdapter? = null
    private var duoGroupHeaderAdapter: DuoHeaderAdapter? = null

    private var adapter: ContactsSyncShowContactListAdapter? = null

    var totalContacts: Int = -1
    var totalContactsOnJar: Int = -1
    var isContactSynced:Boolean = false


    private val viewModel by viewModels<DuosListViewModel> { defaultViewModelProviderFactory }

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var contactsSyncApi: ContactsSyncApi

    @Inject
    lateinit var appsFlyerInviteUtil: AppsFlyerInviteUtil

    @Inject
    lateinit var prefs: PrefsApi
    companion object{
        private const val DuosListFragment = "DuosListFragment"
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureDuoDuoListBinding
        get() = FeatureDuoDuoListBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        EventBus.getDefault().register(this)
        setupUI()
        setupListeners()
        observeLiveData()
        registerBackPressDispatcher()
    }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
    }

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                EventBus.getDefault().postSticky(RefreshDuoHomeCardEvent())
                EventBus.getDefault().post(
                    GoToHomeEvent(
                        DuosListFragment,
                        BaseConstants.HomeBottomNavigationScreen.HOME
                    )
                )
        }
        }
    private fun onPendingInviteViewAllAction() {
        analyticsHandler.postEvent(
            DuoEventKey.Clicked_button_Duo_Home_page,
            mapOf(
                DuoEventKey.SECTION to DuoEventKey.DUO_INVITES,
                DuoEventKey.Button to DuoEventKey.VIEW_ALL
            )
        )
        navigateTo(DuosListFragmentDirections.actionDuosListToPendingInviteListFragment())
    }

    private fun setupUI() {

        binding.masterRv.layoutManager = LinearLayoutManager(context)

        requestAdapter = DuoRequestAdapter(
            fromScreen = null,
            onViewAllClicked = { onPendingInviteViewAllAction() },
            onClick = { contact, invitationStage ->
                if (invitationStage == InvitationStage.ACCEPTED) {
                    analyticsHandler.postEvent(
                        DuoEventKey.Clicked_button_Duo_Home_page,
                        mapOf(
                            DuoEventKey.SECTION to DuoEventKey.DUO_INVITES,
                            DuoEventKey.Button to DuoEventKey.Accept_invite
                        )
                    )
                } else if (invitationStage == InvitationStage.REJECTED) {
                    analyticsHandler.postEvent(
                        DuoEventKey.Clicked_button_Duo_Home_page,
                        mapOf(
                            DuoEventKey.SECTION to DuoEventKey.DUO_INVITES,
                            DuoEventKey.Button to DuoEventKey.Reject_invite
                        )
                    )
                }

                viewModel.processInvite(
                    com.jar.app.feature_contact_sync_common.shared.domain.model.ProcessInviteRequest(
                        contact.inviterId.orEmpty(),
                        invitationStage.toString()
                    )
                )
            }
        )

        groupDuoAdapter = DuoYourDuosAdapter(
            onClick = {
                analyticsHandler.postEvent(
                    DuoEventKey.Clicked_button_Duo_Home_page,
                    mapOf(
                        DuoEventKey.SECTION to DuoEventKey.Your_Duos,
                        DuoEventKey.Button to DuoEventKey.Open
                    )
                )
                navigateTo(
                    DuosListFragmentDirections.actionDuosListToFeatureDuoDetailFragment(
                        it.groupId
                    )
                )
            },
            onRename = { groupDataID, groupName, imageView ->
                val popupBinding =
                    FeatureDuoPopupWindowBinding.inflate(
                        layoutInflater,
                        binding.root,
                        false
                    )
                popupBinding.tvRename.text = getString(R.string.feature_duo_rename)
                popupBinding.tvDelete.text =
                    getString(R.string.feature_duo_delete_small)

                val popupWindow = PopupWindow(
                    popupBinding.root, 220.dp,
                    114.dp, true
                )

                popupBinding.tvRename.setDebounceClickListener {
                    analyticsHandler.postEvent(
                        DuoEventKey.Clicked_button_Duo_Home_page,
                        mapOf(
                            DuoEventKey.SECTION to DuoEventKey.Your_Duos,
                            DuoEventKey.Button to DuoEventKey.Rename
                        )
                    )
                    popupWindow.dismiss()
                    navigateTo(
                        DuosListFragmentDirections.actionDuosListToRenameGroupFragment2(
                            groupDataID,
                            groupName
                        )
                    )
                }
                popupBinding.tvDelete.setDebounceClickListener {
                    analyticsHandler.postEvent(
                        DuoEventKey.Clicked_button_Duo_Home_page,
                        mapOf(
                            DuoEventKey.SECTION to DuoEventKey.Your_Duos,
                            DuoEventKey.Button to DuoEventKey.Delete
                        )
                    )
                    popupWindow.dismiss()
                    navigateTo(
                        DuosListFragmentDirections.actionDuosListToDeleteGroupFragment2(
                            groupDataID
                        )
                    )
                }
                popupWindow.showAsDropDown(imageView, -14, -20, Gravity.END)
            }
        )
        duoRequestHeaderAdapter = DuoHeaderAdapter(showViewAll = true) {
            onPendingInviteViewAllAction()
        }
        duoGroupHeaderAdapter = DuoHeaderAdapter(showViewAll = false)
        concatAdapter = ConcatAdapter(
            duoRequestHeaderAdapter,
            requestAdapter,
            duoGroupHeaderAdapter,
            groupDuoAdapter
        )
        binding.masterRv.adapter = concatAdapter

        analyticsHandler.postEvent(
            DuoEventKey.Shown_Screen_Duo,
            mapOf(
                DuoEventKey.SCREEN to DuoEventKey.SCREEN_DUO_HOMEPAGE,
            )
        )
        binding.tvHeaderText.text = getString(R.string.feature_duo)
        binding.newDuoTopLottie.playLottieWithUrlAndExceptionHandling(
            requireContext(), BaseConstants.LottieUrls.DUO_BIKE_SMALL
        ).apply {
            repeatCount = LottieDrawable.INFINITE
        }

        viewModel.getMergedInviteAndListData()
        viewModel.fetchContactsWithoutPaging()
    }

    private fun showPermsButtonIfNotGranted() {
        initContactAdapter()
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fetchContacts()
        } else {
            setupAllowAccess()
        }
    }

    private fun setupAllowAccess() {
        viewModel.viewModelScope.launch {
            adapter?.submitData(PagingData.from(listOf(com.jar.app.feature_contact_sync_common.shared.domain.model.AllowAccessData)))
        }
    }

    @NeedsPermission(Manifest.permission.READ_CONTACTS)
    fun fetchContacts() {
        //  showLoadingView()
        viewModel.fetchContactProcessingStatus()
    }


    private fun showContactFetchedView() {
        //  binding.groupAccessAllowed.isVisible = false
        binding.masterRv.isVisible = true
        showNewDuoButton(false)
    }


    private fun setupListeners() {
        binding.ivBack.setDebounceClickListener {
            EventBus.getDefault().postSticky(RefreshDuoHomeCardEvent())
            EventBus.getDefault().post(
                GoToHomeEvent(
                    DuosListFragment,
                    BaseConstants.HomeBottomNavigationScreen.HOME
                )
            )
        }
        binding.ivInfo.setDebounceClickListener {
            navigateTo(
                DuosListFragmentDirections.actionDuosListToDuoOnIntroStoryFragment(
                    SOURCE_DUO_LIST
                )
            )
        }

        binding.clTopStrip.setDebounceClickListener {
            if (totalContactsOnJar >= 0  && isContactSynced ) {

                analyticsHandler.postEvent(
                    DuoEventKey.Clicked_button_Duo_Home_page,
                    mapOf(
                        DuoEventKey.SECTION to DuoEventKey.Top_banner,
                        DuoEventKey.Button to DuoEventKey.Invite_friends
                    )
                )
                contactsSyncApi.initiateContactsSyncFlow(
                    com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType.DUO,
                    DeeplinkUtils.generateStoryDeeplink(
                        DuoEventKey.SCREEN_INTRO,
                        "0",
                        "1",
                        false.toString()
                    ))
            } else {

                analyticsHandler.postEvent(
                    DuoEventKey.Clicked_button_Duo_Home_page,
                    mapOf(
                        DuoEventKey.SECTION to DuoEventKey.Top_banner,
                        DuoEventKey.Button to DuoEventKey.Sync_contacts
                    )
                )
                val deeplink = DeeplinkUtils.generateStoryDeeplink(
                    DuoEventKey.SCREEN_INTRO,
                    "0",
                    "0",
                    false.toString()
                )
                contactsSyncApi.initiateContactsSyncFlow(com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType.DUO, deeplink)
            }
        }

    }

    private var job: Job? = null

    private var contact: com.jar.app.feature_contact_sync_common.shared.domain.model.ServerContact? = null

    private fun showNewDuoButton(isVisible: Boolean) {
        // groupDuoAdapter?.isNewDuoDisplayed = isVisible
        // groupDuoAdapter?.notifyItemChanged(0)
    }

    private fun initContactAdapter(userContact: String? = null) {
        adapter = ContactsSyncShowContactListAdapter(onClick = {
            viewModel.sendInvite(
                encodeUrl(it.getNumberWithPlusSignAndCountryCode()),
                com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType.DUO,
                appsFlyerInviteUtil.getAppsFlyerInviteLink().toString()
            )
        }, "", onAllowAccess = {
            analyticsHandler.postEvent(
                DuoEventKey.Clicked_button_Duo,
                DuoEventKey.GiveContactPermission
            )
        },
            { _, _,_ -> },
            { _, _ ,_-> },
            { false },
            { false },
            { false },
            emptyMap()
        ).apply {
//            concatAdapter?.addAdapter(this)
        }


        viewModel.sendInviteLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onSuccess = {
                contact?.invitedForDuo = true
                adapter?.refresh()
            },
            onSuccessWithNullData = {
                contact?.invitedForDuo = true
                adapter?.refresh()
            }
        )
    }

    private fun observeLiveData() {
        viewModel.processInviteLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onLoading = {
                showProgressBar()
            },
            onSuccessWithNullData = {
                dismissProgressBar()
                viewModel.getMergedInviteAndListData()
            },
            onError = { dismissProgressBar() }
        )

        viewModel.contactListLiveData.observe(viewLifecycleOwner) { contactListResponse ->
            if (contactListResponse.status == RestClientResult.Status.SUCCESS) {
                binding.clTopStrip.visibility = View.VISIBLE
                totalContacts = contactListResponse.data?.data?.totalContacts ?: 0
                totalContactsOnJar = contactListResponse.data?.data?.totalContactsOnJar ?: 0
                isContactSynced = contactListResponse.data?.data?.isContactSynced ?: false
                if (isContactSynced) {
                    binding.apply {
                        initialsLayout.visibility = View.VISIBLE
                        duoListHeaderText1.visibility = View.VISIBLE
                        icRoundedContact.visibility = View.GONE
                    }

                    if (totalContactsOnJar <= 0) {
                        binding.duoListHeaderText1.text =
                            getString(R.string.feature_duo_invite_your_friend_start_a_duo)
                        binding.duoListHeaderText2.visibility = View.GONE
                        binding.duoListHeaderText3.visibility = View.GONE
                        binding.initialsLayout.visibility = View.GONE
                    } else {
                        if (totalContactsOnJar == 1) {
                            binding.duoListHeaderText1.text = String.format(
                                getString(R.string.no_of_friend_on_jar),
                                "$totalContactsOnJar"
                            )
                        } else {
                            binding.duoListHeaderText1.text = String.format(
                                getString(R.string.no_of_friends_on_jar),
                                "$totalContactsOnJar"
                            )
                        }
                        binding.duoListHeaderText2.visibility = View.VISIBLE
                        binding.user1Initials.text =
                            contactListResponse.data?.data?.duoContactsListRespList?.firstOrNull()?.friendName?.asInitials()
                        if (totalContactsOnJar > 1) {
                            binding.user2Initials.text = "+${totalContactsOnJar - 1}"
                            binding.user2Initials.visibility = View.VISIBLE
                        } else {
                            binding.user2Initials.visibility = View.GONE
                        }
                    }
                } else {
                    binding.apply {
                        icRoundedContact.visibility = View.VISIBLE
                        duoListHeaderText3.visibility = View.VISIBLE
                        duoListHeaderText3.text =
                            getString(R.string.sync_your_contact_to_find_friends)
                        duoListHeaderText1.visibility = View.GONE
                        duoListHeaderText2.visibility = View.GONE
                        initialsLayout.visibility = View.GONE
                    }

                }
            }
        }
        viewModel.mergedListAndGroupsLiveData.observe(viewLifecycleOwner) {
            val groupDataList = it.first
            val pendingInvites = it.second
            val contactData = it.third
            if (groupDataList.status == RestClientResult.Status.LOADING || pendingInvites.status == RestClientResult.Status.LOADING) {
                showProgressBar()
            } else {
                if (groupDataList.status == RestClientResult.Status.SUCCESS && pendingInvites.status == RestClientResult.Status.SUCCESS) {
                    dismissProgressBar()
                    if (groupDataList.data?.data?.isEmpty().orFalse()
                        && pendingInvites.data?.data?.list?.isEmpty().orFalse()
                    ) {
                        dismissProgressBar()
                        if (contactData.data?.data?.isContactSynced == true) {
                            contactsSyncApi.initiateContactsSyncFlow(
                                com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType.DUO,
                                DeeplinkUtils.generateStoryDeeplink(
                                    DuoEventKey.SCREEN_INTRO,
                                    "0",
                                    "1",
                                    false.toString()
                                ))
                        } else {
                            EventBus.getDefault().postSticky(RefreshDuoHomeCardEvent())
                            EventBus.getDefault().post(
                                GoToHomeEvent(
                                    DuosListFragment,
                                    BaseConstants.HomeBottomNavigationScreen.HOME
                                )
                            )
                        }
                    }
                    if ((groupDataList.data?.data?.size ?: 0) >= 1) {
                        showNewDuoButton(true)
                    } else {
                        showNewDuoButton(false)
                        // showPermsButtonIfNotGranted()
                    }
                }
                if (groupDataList.status == RestClientResult.Status.ERROR || pendingInvites.status == RestClientResult.Status.ERROR) {
                    dismissProgressBar()
                }
                if (groupDataList.status == RestClientResult.Status.SUCCESS) {
                    val data = listOf(
                        DuoHeaderData(
                            headerTextResource = R.string.feature_duo_your_duos,
                            itemCount = it.first.data?.data?.size
                        )
                    )

                    if ((it.first.data?.data?.size ?: 0) > 0) {
                        duoGroupHeaderAdapter?.submitList(data)
                    } else {
                        duoGroupHeaderAdapter?.submitList(null)
                    }
                    groupDuoAdapter?.submitList(it.first.data?.data)

                }
                if (pendingInvites.status == RestClientResult.Status.SUCCESS) {

                    val data = listOf(
                        DuoHeaderData(
                            headerTextResource = R.string.feature_duo_no_pending_invite,
                            itemCount = it.second.data?.data?.list?.size
                        )
                    )
                    val pendingInvitesList = it.second.data?.data
                    if ((pendingInvitesList?.list?.size ?: 0) > 0) {
                        duoRequestHeaderAdapter?.submitList(data)
                    } else {
                        duoRequestHeaderAdapter?.submitList(null)
                    }
                    requestAdapter?.submitList(pendingInvitesList?.list?.take(3))

                }

            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        dismissProgressBar()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRefreshGroupListEvent(refreshGroupListEvent: RefreshGroupListEvent) {
        viewModel.getMergedInviteAndListData()
    }

}