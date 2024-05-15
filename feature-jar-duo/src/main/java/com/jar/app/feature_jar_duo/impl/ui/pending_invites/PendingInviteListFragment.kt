package com.jar.app.feature_jar_duo.impl.ui.pending_invites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.internal.library.jar_core_network.api.model.RestClientResult
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_jar_duo.R
import com.jar.app.feature_jar_duo.databinding.FeatureDuoFragmentPendingInviteListBinding
import com.jar.app.feature_jar_duo.shared.domain.model.InvitationStage
import com.jar.app.feature_contact_sync_common.shared.domain.model.ProcessInviteRequest
import com.jar.app.feature_jar_duo.impl.ui.duo_list.DuoRequestAdapter
import com.jar.app.feature_jar_duo.impl.ui.duo_list.DuosListViewModel
import com.jar.app.feature_jar_duo.shared.util.DuoConstants.SOURCE_DUO_PENDING_INVITE
import com.jar.app.feature_jar_duo.shared.util.DuoConstants.SOURCE_PENDING_INVITE
import com.jar.app.feature_jar_duo.shared.util.DuoEventKey
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject


@AndroidEntryPoint
internal class PendingInviteListFragment : BaseFragment<FeatureDuoFragmentPendingInviteListBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi
    private val viewModel by viewModels<DuosListViewModel> { defaultViewModelProviderFactory }
    private var requestAdapter: DuoRequestAdapter? = null

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureDuoFragmentPendingInviteListBinding
        get() = FeatureDuoFragmentPendingInviteListBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }


    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        observeLiveData()
        setupListeners()
    }

    private fun setupListeners() {
        binding.apply {
            ivInfo.setDebounceClickListener {
                navigateTo(PendingInviteListFragmentDirections.actionPendingInviteListFragmentToDuoOnIntroStoryFragment(SOURCE_PENDING_INVITE))
            }
            ivBack.setDebounceClickListener {
                popBackStack()
            }
        }
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
                viewModel.getPendingInviteListData()
            },
            onError = { dismissProgressBar() }
        )

        viewModel.pendingInviteDataLiveData.observe(viewLifecycleOwner) { pendingInvites ->

            if (pendingInvites.status == RestClientResult.Status.LOADING) {
                showProgressBar()
            } else {
                if (pendingInvites.status == RestClientResult.Status.SUCCESS) {
                    dismissProgressBar()
                    binding.rvPendingInviteList.adapter = requestAdapter
                    binding.rvPendingInviteList.layoutManager = LinearLayoutManager(context)


                }
                if (pendingInvites.status == RestClientResult.Status.SUCCESS) {
                    binding.tvHeaderText.text = String.format(
                        getString(R.string.pending_invite_list_toolbar_text),
                        pendingInvites.data?.data?.list?.size
                    )
                    requestAdapter?.submitList(pendingInvites.data?.data?.list)
                }

            }
        }
    }

    private fun setupUI() {
        analyticsHandler.postEvent(
            DuoEventKey.Shown_Screen_Duo,
            mapOf(
                DuoEventKey.SCREEN to DuoEventKey.SCREEN_DUO_PENDING_INVITE,
            )
        )

        requestAdapter = DuoRequestAdapter(fromScreen = SOURCE_DUO_PENDING_INVITE) { contact, invitationStage ->
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

        viewModel.getPendingInviteListData()
    }

}