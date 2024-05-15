package com.jar.app.feature_contacts_sync_common.impl.ui.sent_invites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_utils.data.AppsFlyerInviteUtil
import com.jar.app.feature_contacts_sync_common.R
import com.jar.app.feature_contacts_sync_common.databinding.FeatureDuoSentInviteScreenBinding
import com.jar.app.feature_contact_sync_common.shared.ContactsSyncShowContactListViewModel
import com.jar.app.feature_contact_sync_common.shared.utils.ContactsSyncConstants.AnalyticKeys.CTA
import com.jar.app.feature_contact_sync_common.shared.utils.ContactsSyncConstants.AnalyticKeys.Feature_Type
import com.jar.app.feature_contact_sync_common.shared.utils.ContactsSyncConstants.AnalyticKeys.Remind
import com.jar.app.feature_contact_sync_common.shared.utils.ContactsSyncConstants.AnalyticKeys.SCREEN
import com.jar.app.feature_contact_sync_common.shared.utils.ContactsSyncConstants.AnalyticKeys.SentInviteRemindClicked
import com.jar.app.feature_contact_sync_common.shared.utils.ContactsSyncConstants.AnalyticKeys.SentInviteScreenLaunched
import com.jar.app.feature_contact_sync_common.shared.utils.ContactsSyncConstants.AnalyticKeys.Sent_Invites
import com.jar.app.feature_contacts_sync_common.impl.ui.contact_list.ContactsSyncShowContactListViewModelAndroid
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ContactsSyncSentInvitesFragment : BaseFragment<FeatureDuoSentInviteScreenBinding>() {

    private val viewModelAndroid by viewModels<ContactsSyncShowContactListViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelAndroid.getInstance()
    }
    private var adapter: ContactsSyncSentListAdapter? = null
    private val args by navArgs<ContactsSyncSentInvitesFragmentArgs>()

    @Inject
    lateinit var appsFlyerInviteUtil: AppsFlyerInviteUtil

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureDuoSentInviteScreenBinding
        get() = FeatureDuoSentInviteScreenBinding::inflate

    override fun setupAppBar() {

    }

    override fun setup(savedInstanceState: Bundle?) {
        setupAdapter()
        setupUI()
        setupListeners()
        observeFlow()
    }

    private fun setupListeners() {
        binding.apply {
            ivInfo.setDebounceClickListener {
                args.infoDeeplink?.let { it1 -> navigateTo(it1) }
            }
            ivBack.setDebounceClickListener {
                popBackStack()
            }
        }
    }

    private fun observeFlow(){
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sentInviteListFlow.collectLatest {
                    it?.let {
                        adapter?.submitData(it)
                    }
                }
            }
        }
    }

    private fun setupAdapter() {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvSentInvites.layoutManager = layoutManager
        adapter = ContactsSyncSentListAdapter { contact ->
            remindContactForInvite(contact)
        }
        binding.rvSentInvites.adapter = adapter
        binding.rvSentInvites.itemAnimator = null
    }

    private fun remindContactForInvite(contact: com.jar.app.feature_contact_sync_common.shared.domain.model.ContactsSyncPendingInvitesSentObject) {
        analyticsHandler.postEvent(
            SentInviteRemindClicked,
            mapOf(
                CTA to Remind,
                Feature_Type to args.featureType,
                SCREEN to Sent_Invites
            )
        )
        viewModel.sendInviteReminder(
            contact.phoneNumber,
            appsFlyerInviteUtil.getAppsFlyerInviteLink().toString(),
            com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType.valueOf(args.featureType)
        )

        getString(
            R.string.feature_contacts_sync_common_reminder_sent_toast, contact.name
        ).snackBar(
            binding.root,
            com.jar.app.core_ui.R.drawable.feature_duo_ic_checkmark,
            progressColor = com.jar.app.core_ui.R.color.color_1EA787
        )
        adapter?.refresh()

    }


    private fun setupUI() {
        analyticsHandler.postEvent(
            SentInviteScreenLaunched,
            mapOf(
                Feature_Type to args.featureType,
                SCREEN to Sent_Invites
            )
        )
        binding.tvHeaderText.text = binding.root.resources.getString(
            R.string.feature_contacts_sync_common_sent_invites
        )
        getsentInviteListFromServer()
        binding.ivInfo.isVisible = !args.infoDeeplink.isNullOrEmpty()
    }

    private fun getsentInviteListFromServer() {
        val featureType = com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType.valueOf(args.featureType)
        viewModel.fetchSentInvites(args.showHeaders, featureType) { getCustomString(it) }
    }

}