package com.jar.app.feature_contacts_sync_common.impl.ui.contact_list

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieDrawable
import com.bumptech.glide.Glide
import com.facebook.shimmer.Shimmer
import com.jar.app.base.data.event.RefreshDuoHomeCardEvent
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.livedata.SharedPreferencesUserLiveData
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.*
import com.jar.app.core_base.util.orZero
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.core_utils.data.AppsFlyerInviteUtil
import com.jar.app.feature_contacts_sync_common.R
import com.jar.app.feature_contacts_sync_common.databinding.FeatureContactsSyncShowContactListBinding
import com.jar.app.feature_contact_sync_common.shared.utils.ContactsSyncConstants
import com.jar.app.feature_contact_sync_common.shared.utils.ContactsSyncConstants.AnalyticKeys.Contact_Permission_Given
import com.jar.app.feature_contact_sync_common.shared.utils.ContactsSyncConstants.AnalyticKeys.Contact_list
import com.jar.app.feature_contact_sync_common.shared.utils.ContactsSyncConstants.AnalyticKeys.Feature_Type
import com.jar.app.feature_contact_sync_common.shared.utils.ContactsSyncConstants.AnalyticKeys.InviteContactsScreenAllowAccessClicked
import com.jar.app.feature_contact_sync_common.shared.utils.ContactsSyncConstants.AnalyticKeys.InviteContactsScreenShown
import com.jar.app.feature_contact_sync_common.shared.utils.ContactsSyncConstants.AnalyticKeys.SCREEN
import com.jar.app.feature_contact_sync_common.shared.utils.HasSearched
import com.jar.internal.library.jar_core_network.api.util.collectUnwrapped
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import contacts.core.Contacts
import contacts.core.util.phoneList
import dagger.hilt.android.AndroidEntryPoint
import dev.icerock.moko.resources.StringResource
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.greenrobot.eventbus.EventBus
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnNeverAskAgain
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
@RuntimePermissions
class ContactsSyncShowContactListFragment :
    BaseFragment<FeatureContactsSyncShowContactListBinding>() {
    private var isContactSyncSuccess: Boolean = false

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val args by navArgs<ContactsSyncShowContactListFragmentArgs>()

    companion object {
        private const val MAX_SYNC_RETRY_COUNT = 8
    }

    @Inject
    lateinit var userLiveData: SharedPreferencesUserLiveData

    @Inject
    lateinit var appsFlyerInviteUtil: AppsFlyerInviteUtil

    private var contact: com.jar.app.feature_contact_sync_common.shared.domain.model.ServerContact? = null

    private var job: Job? = null

    private var shimmerJob: Job? = null

    private val viewModelAndroid by viewModels<ContactsSyncShowContactListViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelAndroid.getInstance()
    }

    private var contactAdapter: ConcatAdapter? = null

    private var showContactAdapter: ContactsSyncShowContactListAdapter? = null

    private var contactFooterAdapter: ContactsSyncListFooterAdapter? = null

    private var layoutManager: LinearLayoutManager? = null

    private var syncRetryCount = 0

    private var syncRetryDelay = 0L

    private var noOfSentInvites: Int = 0

    private val totalContactDeferred by lazy {
        uiScope.async(Dispatchers.IO) {
            Contacts(requireContext()).query().find().size.toLong()
        }
    }
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureContactsSyncShowContactListBinding
        get() = FeatureContactsSyncShowContactListBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        viewModel.setFeatureType(args.featureType)
        refreshContactsWithPermissionCheck()
        setupUI()
        setupListeners()
        observeLiveData()
        updateViewState()
        registerBackPressDispatcher()
    }

    private fun setupUI() {

        analyticsHandler.postEvent(
            ContactsSyncConstants.AnalyticKeys.ContactsScreenShown,
            mapOf(
                Feature_Type to args.featureType,
                SCREEN to ContactsSyncConstants.AnalyticKeys.Contact_list
            )
        )

        binding.tvAllowHeader.text =
            getString(R.string.feature_contacts_sync_common_allow_access_to_contacts)

        binding.tvAllowDesc.text =
            getString(R.string.feature_contacts_sync_common_we_need_access_to_your_contacts_to_invite_your_friends)

        binding.tvNoContactFound.text =
            getString(R.string.feature_contacts_sync_common_no_result_found)

        binding.btnRefresh.setTypeface(com.jar.app.core_ui.R.font.inter)
        setupFooterLayout(binding)
        viewModel.getSentInvitesCount()
        viewModel.fetchContactListStaticData()
    }

    @Inject
    lateinit var phoneNumberUtils: PhoneNumberUtil

    @NeedsPermission(Manifest.permission.READ_CONTACTS)
    fun refreshContacts() {
        fetchLocalContactsAndUploadToServer(requireContext().applicationContext)
    }

    private fun fetchLocalContactsAndUploadToServer(mApp: Context) {
        viewModelAndroid.fetchLocalContactsAndUploadToServer(mApp.applicationContext)
    }

    private fun setupFooterLayout(binding: FeatureContactsSyncShowContactListBinding) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.selectedContactSize.collectLatest {
                    if (it <= 0) {
                        viewModel.toggleMultiSelectIfNeeded()
                        if (binding.checkBoxSelectAll.isChecked) binding.checkBoxSelectAll.isChecked = false
                        toggleInviteButton()
                        toggleContactSelectState(false)
                        hideInviteLayout()
                    }
                    if (binding.checkBoxSelectAll.isChecked.not()) {
                        binding.btnMultipleInvite.setText(
                            String.format(
                                binding.root.resources.getString(R.string.feature_contacts_sync_common_invite_text),
                                it
                            )
                        )
                    }
                }
            }
        }
    }


    private fun setupAdapter(userContact: String) {

        contactFooterAdapter = ContactsSyncListFooterAdapter() {
            openSentInviteFragment()
        }
        layoutManager = LinearLayoutManager(requireContext())
        binding.rvContacts.layoutManager = layoutManager
        showContactAdapter = ContactsSyncShowContactListAdapter(
            onClick = {
                val hasSearchedContact =
                    if (binding.etSearchBar.text.isNullOrEmpty()) HasSearched.No else HasSearched.Yes
                analyticsHandler.postEvent(
                    ContactsSyncConstants.AnalyticKeys.ContactsInviteClicked,
                    mapOf(
                        ContactsSyncConstants.AnalyticKeys.invitetype to ContactsSyncConstants.AnalyticKeys.SINGLE_INVITE,
                        ContactsSyncConstants.AnalyticKeys.searchaction to hasSearchedContact.name,
                        Feature_Type to args.featureType,
                        SCREEN to Contact_list
                    )
                )
                viewModel.sendInvite(
                    encodeUrl(it.getNumberWithPlusSignAndCountryCode()),
                    appsFlyerInviteUtil.getAppsFlyerInviteLink().toString()
                )
                removeInvitedContactFromList(listOf(it))

                contact = it
            }, userContact,
            onContactedSelected = { contact, toggleUI, position ->
                viewModel.toggleSelectedState(
                    contact, toggleUI
                )
                if (viewModel.getSelectedContacts().isEmpty()) {
                    if (!binding.checkBoxSelectAll.isChecked) {
                        toggleInviteButton()
                        hideInviteLayout()
                    }
                }
                showContactAdapter?.notifyItemChanged(position)
            },
            onLongPressed = { contact, toggleUI, position ->
                viewModel.toggleSelectedState(contact, toggleUI)
                toggleInviteButton()
                binding.checkBoxSelectAll.visibility = View.VISIBLE
                binding.etSearchBar.visibility = View.GONE
                showContactAdapter?.notifyItemChanged(position)
            },
            isMultiSelectEnabled = {
                if (!binding.checkBoxSelectAll.isChecked) {
                    if (viewModel.isMultiSelectEnabled) {
                        binding.checkBoxSelectAll.visibility = View.VISIBLE
                        binding.btnRefresh.visibility = View.GONE

                    } else {
                        binding.checkBoxSelectAll.visibility = View.GONE
                        binding.etSearchBar.visibility = View.VISIBLE
                        binding.btnRefresh.visibility = View.VISIBLE
                    }
                }
                viewModel.isMultiSelectEnabled

            },
            isAllSelected = {
                checkIfSelectAll()

            },
            isMultiInviteLayoutVisible = {
                binding.inviteLayout.isVisible
            },
            selectedContactMap = viewModel.getSelectedContacts()
        )

        contactAdapter = ConcatAdapter(showContactAdapter, contactFooterAdapter)


        binding.rvContacts.adapter = contactAdapter


    }

    private fun removeInvitedContactFromList(contact: List<com.jar.app.feature_contact_sync_common.shared.domain.model.ServerContact>) {
        lifecycleScope.launch {
            //if(contact.size>50) delay(5000)
            val modifiedList = showContactAdapter?.snapshot()?.items?.toMutableList()
            modifiedList?.removeAll(contact)
            val updatedPagingData = PagingData.from(modifiedList?.toList() ?: emptyList())
            showContactAdapter?.submitData(lifecycle, updatedPagingData)
            Log.d("SEEHERE", "removeInvitedContactFromList() called")
            viewModel.fetchContacts(binding.etSearchBar.text.toString(), generateStringFunction())
            showContactAdapter?.refresh()
        }


    }
    fun generateStringFunction(): (string: StringResource) -> String {
        return {
            getCustomString(it)
        }
    }

    private fun toggleInviteButton() {
        when {
            viewModel.getSelectedContacts().size == 1 -> {
                try {
                    binding.rvContacts.forEachVisibleHolderUntil<BaseViewHolder>(
                        0,
                        layoutManager!!.findLastVisibleItemPosition() + 1
                    ) { viewHolder ->
                        if (viewHolder is ContactsSyncShowContactListViewHolder) {
                            if (!binding.checkBoxSelectAll.isChecked) {
                                viewHolder.hideInviteButton()
                                showInviteLayout()
                            }
                        }
                    }

                } catch (e: ClassCastException) {

//                    Timber.d("LongPressed", e.message)
                }

            }

            viewModel.getSelectedContacts().isEmpty() -> {
                try {
                    binding.rvContacts.forEachVisibleHolderUntil<BaseViewHolder>(
                        0,
                        layoutManager!!.findLastVisibleItemPosition() + 1
                    ) { viewHolder ->
                        if (viewHolder is ContactsSyncShowContactListViewHolder) {

                            if (!binding.checkBoxSelectAll.isChecked) {
                                viewHolder.showInviteButton()
                                hideInviteLayout()
                            }
                        }
                    }

                } catch (e: ClassCastException) {
//                    Timber.d("Unselected")
                }

            }

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupListeners() {
        binding.btnAllowAccess.setDebounceClickListener {
            analyticsHandler.postEvent(
                InviteContactsScreenAllowAccessClicked, mapOf(
                    Feature_Type to args.featureType,
                    SCREEN to Contact_list,
                    ContactsSyncConstants.AnalyticKeys.ErrorMessage to getString(R.string.feature_contacts_sync_common_we_need_access_to_your_contacts_to_invite_your_friends)
                )
            )
            refreshContactsWithPermissionCheck()
        }

        binding.btnRefresh.setDebounceClickListener {
            analyticsHandler.postEvent(
                ContactsSyncConstants.AnalyticKeys.ContactsScreenClicked, mapOf<String, String>(
                    Feature_Type to args.featureType,
                    ContactsSyncConstants.AnalyticKeys.CTA to ContactsSyncConstants.AnalyticKeys.Refresh_contacts,
                    SCREEN to Contact_list
                )
            )
            refreshContactsWithPermissionCheck()
        }
        binding.searchCloseBtn.setOnClickListener {
            analyticsHandler.postEvent(
                ContactsSyncConstants.AnalyticKeys.ContactsScreenClicked, mapOf<String, String>(
                    Feature_Type to args.featureType,
                    ContactsSyncConstants.AnalyticKeys.CTA to ContactsSyncConstants.AnalyticKeys.Clear_search_text,
                    SCREEN to Contact_list
                )
            )
            binding.etSearchBar.setText("")
            binding.clTopStrip.isVisible = true
        }

        binding.etSearchBar.textChanges().debounce(500).onEach {
            binding.searchCloseBtn.isVisible = !it.isNullOrEmpty()
            binding.clTopStrip.isVisible = it.isNullOrEmpty()
            binding.btnRefresh.animateViewVisibility(it.isNullOrEmpty(), 200)
            Log.d("SEEHERE", "etSearchBar() called")
            getContactListFromServer(it.toString())
        }.launchIn(uiScope)

        binding.ivBack.setDebounceClickListener {
            EventBus.getDefault().postSticky(RefreshDuoHomeCardEvent())
            popBackStack()

            analyticsHandler.postEvent(
                ContactsSyncConstants.AnalyticKeys.ContactsScreenClicked,
                mapOf(
                    ContactsSyncConstants.AnalyticKeys.CTA to ContactsSyncConstants.AnalyticKeys.Back_button_clicked,
                    Feature_Type to args.featureType,
                    SCREEN to Contact_list
                )
            )
        }
        binding.checkBoxSelectAll.setOnCheckedChangeListener { _, isSelectAll ->
            analyticsHandler.postEvent(
                ContactsSyncConstants.AnalyticKeys.ContactsInviteClicked,
                mapOf(
                    ContactsSyncConstants.AnalyticKeys.CTA to ContactsSyncConstants.AnalyticKeys.SELECT_ALL,
                    Feature_Type to args.featureType,
                    SCREEN to Contact_list
                )
            )
            viewModel.isSelectAllEnabled = isSelectAll
            viewModel.clearSelectedContacts()
            toggleInviteButton()
            if (isSelectAll) {
                binding.btnMultipleInvite.setText(resources.getString(R.string.feature_contacts_sync_common_invite_all))
                toggleContactSelectState(true)
                showInviteLayout()
            } else {
                viewModel.toggleMultiSelectIfNeeded()
                toggleContactSelectState(false)

            }
        }

        binding.btnSentInvite.setDebounceClickListener {
            analyticsHandler.postEvent(
                ContactsSyncConstants.AnalyticKeys.ContactsScreenClicked,
                mapOf(
                    ContactsSyncConstants.AnalyticKeys.CTA to ContactsSyncConstants.AnalyticKeys.Sent_Invites_clicked,
                    Feature_Type to args.featureType,
                    SCREEN to Contact_list
                )
            )
            openSentInviteFragment()
        }

        binding.btnMultipleInvite.setDebounceClickListener {
            val hasSearchedContact =
                if (binding.etSearchBar.text.isNullOrEmpty()) HasSearched.No else HasSearched.Yes
            analyticsHandler.postEvent(
                ContactsSyncConstants.AnalyticKeys.ContactsInviteClicked,
                mapOf(
                    ContactsSyncConstants.AnalyticKeys.CTA to ContactsSyncConstants.AnalyticKeys.MULTIPLE_INVITE,
                    ContactsSyncConstants.AnalyticKeys.searchaction to hasSearchedContact,
                    Feature_Type to args.featureType,
                    SCREEN to Contact_list
                )
            )
            Log.d("SEEHERE", "setupListeners() called")
            showProgressBar()
            viewModel.sendMultipleInvite(
                binding.etSearchBar.text.toString(),
                binding.checkBoxSelectAll.isChecked,
                appsFlyerInviteUtil.getAppsFlyerInviteLink()
            )
            uiScope.launch {
                if (binding.checkBoxSelectAll.isChecked || viewModel.getSelectedContactSize() > 50) {
                    delay(5000)
                } else {
                    delay(2000)
                }
                dismissProgressBar()
                viewModel.getSentInvitesCount()
                removeInvitedContactFromList(viewModel.getSelectedContacts().values.toList())
            }

        }
    }

    private fun toggleContactSelectState(isSelected: Boolean) {

        showContactAdapter?.snapshot()?.items?.forEach {
            if (it is com.jar.app.feature_contact_sync_common.shared.domain.model.ServerContact) {
                it.isSelected = isSelected
            }
        }
        showContactAdapter?.notifyDataSetChanged()

    }

    private fun openSentInviteFragment() {
        if (noOfSentInvites > 0) {
            binding.etSearchBar.text?.clear()
            val infoDeeplink = args.infoDeeplink
            dismissProgressBar()
            navigateTo(
                ContactsSyncShowContactListFragmentDirections.actionShowContactListFragmentToDuoSentInvitesFragment(
                    viewModel.shouldShowHeaders(),
                    args.featureType,
                    if (infoDeeplink.isNullOrBlank()) null else decodeUrl(infoDeeplink)
                )
            )
        } else {
            getString(R.string.feature_contacts_sync_common_no_invites_sent_error).snackBar(
                binding.root,
                com.jar.app.core_ui.R.drawable.feature_duo_ic_error,
                progressColor = com.jar.app.core_ui.R.color.color_C05357
            )
        }
    }

    private fun checkIfSelectAll(): Boolean {
        return binding.checkBoxSelectAll.isChecked
    }

    private fun observeLiveData() {
        val viewRef: WeakReference<View> = WeakReference(binding.root)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                showContactAdapter?.loadStateFlow?.collectLatest { loadState ->
                    if (loadState.append.endOfPaginationReached) {
                        contactFooterAdapter?.submitList(listOf(resources.getString(R.string.didn_t_find)))
                    }

                    Log.d("SEEHERE", "observeLiveData() called with: loadState = $loadState")
                    val isListEmpty =
                        loadState.refresh is LoadState.NotLoading && showContactAdapter?.itemCount == 0
                    if (isListEmpty && binding.etSearchBar.text.isNullOrEmpty().not()) {
                        showNoResultFoundView()
                    } else {
                        showContactFetchedView()
                    }
                    when (loadState.refresh is LoadState.Loading) {
                        true -> {

                        }

                        false -> {

                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.contactListStaticData.collectUnwrapped(
                    onSuccess = {
                        val it = it.data
                        binding.tvHeaderText.text = it?.contactListText?.header
                        binding.tvAllowDesc.text = getString(R.string.feature_contacts_sync_common_we_need_access_to_your_contacts_to_invite_your_friends)
                        binding.duoListHeaderText1.text = it?.contactListText?.title
                        binding.btnSentInvite.text = it?.contactListText?.buttonText
                        it?.buttonTint?.let {
                            binding.btnSentInvite.backgroundTintList = ColorStateList.valueOf(Color.parseColor(it))
                        }
                        it?.lottieBanner?.let { url ->
                            binding.newDuoTopLottie.isVisible = true
                            binding.bannerBackView.isVisible = false
                            binding.bannerImage.isVisible = false
                            binding.newDuoTopLottie.playLottieWithUrlAndExceptionHandling(
                                requireContext(), url
                            ).apply {
                                repeatCount = LottieDrawable.INFINITE
                            }
                        } ?: run {
                            if (!it?.bannerBg.isNullOrEmpty() && !it?.bannerRightImage.isNullOrEmpty()) {
                                binding.newDuoTopLottie.isVisible = false
                                binding.bannerBackView.isVisible = true
                                binding.bannerImage.isVisible = true
                                binding.bannerBackView.setBackgroundColor(Color.parseColor(it?.bannerBg))
                                Glide.with(requireContext())
                                    .load(it?.bannerRightImage)
                                    .into(binding.bannerImage)
                            }
                        }
                    })
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.contactSyncedLiveData.collectUnwrapped(
                    onSuccess = {
                        val it = it.data
                        Log.d("SEEHERE", "observeLiveData() called" + com.jar.app.feature_contact_sync_common.shared.domain.model.ContactSyncState.valueOf(
                            it
                        ))
                        when (com.jar.app.feature_contact_sync_common.shared.domain.model.ContactSyncState.valueOf(
                            it
                        )) {
                            com.jar.app.feature_contact_sync_common.shared.domain.model.ContactSyncState.SYNC_SUCCESS -> {
                                isContactSyncSuccess = true
                                getContactListFromServer()
                            }

                            com.jar.app.feature_contact_sync_common.shared.domain.model.ContactSyncState.NOT_RECEIVED ->
                                fetchLocalContactsAndUploadToServer(requireContext().applicationContext)

                            com.jar.app.feature_contact_sync_common.shared.domain.model.ContactSyncState.SYNC_PENDING -> {
                                syncRetryDelay += 500
                                syncRetryCount++
                                if (syncRetryCount < MAX_SYNC_RETRY_COUNT) {
                                    viewModel.fetchContactProcessingStatus(syncRetryDelay)
                                }
                            }
                        }
                    })
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.fetchLocalContactAndUploadToServerLiveData.collectUnwrapped(
                    onLoading = {
                        showLoadingView()
                    },
                    onSuccessWithNullData = {
                        syncRetryCount = 0
                        syncRetryDelay = 0
                        viewModel.fetchContactProcessingStatus()
                    },
                    onSuccess = {
                        syncRetryCount = 0
                        syncRetryDelay = 0
                        viewModel.fetchContactProcessingStatus()
                    }
                )
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.sendInviteLiveData.collectUnwrapped(
                    onSuccess = {
                        contact?.invitedForDuo = true
                        showContactAdapter?.refresh()
                    },
                    onSuccessWithNullData = {
                        contact?.invitedForDuo = true
                        showContactAdapter?.refresh()
                    })
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.sendMultipleInviteLiveData.collectLatest {

                    when (it.status) {
                        RestClientResult.Status.SUCCESS -> {
                            if (binding.checkBoxSelectAll.isChecked) {
                                getString(R.string.all_contact_invite_sent_successfully).snackBar(
                                    binding.root,
                                    com.jar.app.core_ui.R.drawable.feature_duo_ic_checkmark,
                                    progressColor = com.jar.app.core_ui.R.color.color_1EA787
                                )
                            } else {
                                String.format(
                                    getString(R.string.multiple_invite_sent_successfully),
                                    viewModel.getSelectedContactSize()
                                ).snackBar(
                                    binding.root,
                                    com.jar.app.core_ui.R.drawable.feature_duo_ic_checkmark,
                                    progressColor = com.jar.app.core_ui.R.color.color_1EA787
                                )
                            }
                            uiScope.launch {
                                viewModel.clearSelectedContacts()
                                viewModel.updateSelectedContactSize()
                            }
                        }

                        RestClientResult.Status.LOADING -> {
                            if (viewModel.getSelectedContactSize() > 50) {
                                Log.d("SEEHERE", "sendMultipleInviteLiveData() called")
                                showProgressBar()
                            }
                        }

                        else -> {
                            dismissProgressBar()
                        }
                    }

                }
            }
        }

        userLiveData.observe(viewLifecycleOwner) {
            it?.let {
                setupAdapter(it.phoneNumber)
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.noOfSentInvites.collectLatest { sentInviteCount ->
                    noOfSentInvites = sentInviteCount.orZero()
                    binding.btnSentInviteLayout.isVisible = true
                    binding.btnSentInvite.text = String.format(
                        resources.getString(R.string.feature_contacts_sync_common_no_sent_invite),
                        sentInviteCount.orZero()
                    )
                    if (shimmerJob?.isActive == true) {
                        // Cancel the coroutine if the button is clicked again
                        shimmerJob?.cancel()
                        shimmerJob = null
                        stopShimmerWithDelay()
                    } else if (sentInviteCount > 0) {
                        // Start the shimmer animation
                        startShimmer()
                        stopShimmerWithDelay()
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.contactListFlow.collectLatest {
                    Log.d("SEEHERE", "observeLiveData: " + it)
                    uiScope.launch {
                        dismissProgressBar()
                    }
                    it?.let {
                        showContactAdapter?.submitData(it)
                    }
                }
            }
        }
    }

    private fun stopShimmerWithDelay() {
        // Launch a coroutine to stop the animation after 2 seconds
        shimmerJob = uiScope.launch(Dispatchers.Main) {
            delay(2000)
            stopShimmer()
        }
    }

    private fun startShimmer() {
        val shimmer = Shimmer.AlphaHighlightBuilder().setBaseAlpha(0.5f).build()
        binding.btnSentInviteLayout.setShimmer(shimmer)
        binding.btnSentInviteLayout.startShimmer()
    }

    private fun stopShimmer() {
        val shimmer = Shimmer.AlphaHighlightBuilder().setBaseAlpha(1f).build()
        binding.btnSentInviteLayout.setShimmer(shimmer)
        binding.btnSentInviteLayout.stopShimmer()
    }

    private fun getContactListFromServer(searchKey: String? = null) {
        Log.d("SEEHERE", "getContactListFromServer() called with: searchKey = $searchKey")
        showContactFetchedView()
        viewModel.fetchContacts(searchKey, generateStringFunction())
    }

    @OnPermissionDenied(Manifest.permission.READ_CONTACTS)
    fun onPermissionDenied() {
        getString(R.string.feature_contacts_sync_common_permission_denied).snackBar(binding.root)
    }

    @OnNeverAskAgain(Manifest.permission.READ_CONTACTS)
    fun onPermissionNeverAskAgain() {
        Toast.makeText(
            requireContext(),
            getString(R.string.feature_contacts_sync_common_permission_denied),
            Toast.LENGTH_SHORT
        ).show()
        requireContext().openAppInfo()
    }

    override fun onResume() {
        super.onResume()
        stopShimmer()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }


    private fun updateViewState() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("SEEHERE", "updateViewState() called")
            viewModel.fetchContactProcessingStatus()
        } else {
            showGivePermissionView()
        }
    }

    private fun showGivePermissionView() {
        binding.groupContactList.isVisible = false
        binding.btnRefresh.isVisible = false
        binding.btnRefresh.isVisible = false
        binding.groupNoResultFound.isVisible = false
        binding.clTopStrip.isVisible = false
        analyticsHandler.postEvent(
            InviteContactsScreenShown, mapOf(
                Feature_Type to args.featureType,
                Contact_Permission_Given to "true",
                SCREEN to Contact_list
            )
        )
        dismissProgressBar()
    }

    private fun showLoadingView() {
        Log.d("SEEHERE", "showLoadingView() called")
        showProgressBar()
//    binding.groupAllowAccess.isVisible = false
        binding.groupContactList.isVisible = false
        binding.btnRefresh.isVisible = false
        binding.btnRefresh.isVisible = false
        binding.groupNoResultFound.isVisible = false
        binding.clTopStrip.isVisible = false
    }

    private fun showContactFetchedView() {
        dismissProgressBar()
        binding.groupContactList.isVisible = true
        binding.btnRefresh.isVisible = true
        binding.groupNoResultFound.isVisible = false
        if (binding.etSearchBar.text.isNullOrBlank()) {
            binding.clTopStrip.isVisible = true
        } else {
            binding.clTopStrip.isVisible = false
        }
    }

    private fun showNoResultFoundView() {
        dismissProgressBar()
        binding.groupContactList.isVisible = false
        binding.btnRefresh.isVisible = true
        binding.groupNoResultFound.isVisible = true
        binding.etSearchBar.isVisible = true
        binding.tvNoContactFound.text = String.format(
            resources.getString(R.string.feature_contacts_sync_common_no_contact_found_for_search),
            binding.etSearchBar.text
        )
    }

    private fun showInviteLayout() {

        val slideUpAnimation = AnimationUtils.loadAnimation(
            requireContext(),
            com.jar.app.base.R.anim.slide_from_bottom
        )
        binding.inviteLayout.animation = slideUpAnimation
        binding.inviteLayout.visibility = View.VISIBLE
    }

    private fun hideInviteLayout() {
        val slideDownAnimation =
            AnimationUtils.loadAnimation(requireContext(), com.jar.app.base.R.anim.slide_to_bottom)
        binding.inviteLayout.animation = slideDownAnimation
        binding.inviteLayout.visibility = View.GONE
    }

    private var backPressCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                EventBus.getDefault().postSticky(RefreshDuoHomeCardEvent())
                popBackStack()
            }
        }

    private fun registerBackPressDispatcher() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            backPressCallback
        )
    }
}