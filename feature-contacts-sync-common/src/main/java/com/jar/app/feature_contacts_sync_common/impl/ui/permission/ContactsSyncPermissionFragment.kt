package com.jar.app.feature_contacts_sync_common.impl.ui.permission

import android.Manifest
import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.ObjectAnimator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.hasContactPermission
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_contacts_sync_common.databinding.FeatureContactsSyncPermissionBinding
import com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType
import com.jar.app.feature_contact_sync_common.shared.domain.model.ContactSyncState
import com.jar.app.feature_contact_sync_common.shared.utils.ContactsSyncConstants
import com.jar.app.feature_contact_sync_common.shared.utils.ContactsSyncConstants.AnalyticKeys.InviteContactsScreenAllowAccessClicked
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import contacts.core.Contacts
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import permissions.dispatcher.*
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
@RuntimePermissions
class ContactsSyncPermissionFragment : BaseFragment<FeatureContactsSyncPermissionBinding>() {
    companion object {
        private const val MAX_SYNC_RETRY_COUNT = 8
        private const val DuoContactPermissionFragment = "DuoContactPermissionFragment"
    }

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private var syncRetryDelay = 0L
    private var syncRetryCount = 0
    private var job: Job? = null

    private var isContactSyncSuccess = false
    private val viewModel by viewModels<ContactsSyncPermissionViewmodel> { defaultViewModelProviderFactory }
    private val args by navArgs<ContactsSyncPermissionFragmentArgs>()

    private var animation: ObjectAnimator? = null

    private val totalContactDeferred by lazy {
        uiScope.async(Dispatchers.IO) {
            Contacts(requireContext()).query().find().size.toLong()
        }
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureContactsSyncPermissionBinding
        get() = FeatureContactsSyncPermissionBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        observeLiveData()
        getData()
    }

    private fun getFeatureType(): com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType? {
        return com.jar.app.feature_contact_sync_common.shared.domain.model.ContactListFeatureType.values().find { it.name == args.featureType }
    }
    private fun getData() {
        getFeatureType()?.let {
            viewModel.fetchContactListStaticData(it)
        }
    }

    private fun setupUI() {
        analyticsHandler.postEvent(
            ContactsSyncConstants.AnalyticKeys.InviteContactsScreenShown, mapOf(
            ContactsSyncConstants.AnalyticKeys.Feature_Type to args.featureType,
            ContactsSyncConstants.AnalyticKeys.Contact_Permission_Given to "false",
            ContactsSyncConstants.AnalyticKeys.SCREEN to ContactsSyncConstants.AnalyticKeys.Permission_screen
        ))
        showContactAccessView()
        setupListeners()
    }

    private fun setupListeners() {
        binding.apply {
            btnAllowAccess.setDebounceClickListener {
                analyticsHandler.postEvent(
                    InviteContactsScreenAllowAccessClicked,
                    mapOf(
                        ContactsSyncConstants.AnalyticKeys.Feature_Type to args.featureType,
                        ContactsSyncConstants.AnalyticKeys.CTA to ContactsSyncConstants.AnalyticKeys.ALLOW_ACCESS,
                        ContactsSyncConstants.AnalyticKeys.SCREEN to ContactsSyncConstants.AnalyticKeys.Permission_screen
                    )
                )
                showSyncLayoutAndStartSyncTimerWithPermissionCheck()

            }
            ivBack.setDebounceClickListener {
                analyticsHandler.postEvent(
                    ContactsSyncConstants.AnalyticKeys.InviteContactsScreenAllowAccessClicked,
                    mapOf(
                        ContactsSyncConstants.AnalyticKeys.Feature_Type to args.featureType,
                        ContactsSyncConstants.AnalyticKeys.CTA to ContactsSyncConstants.AnalyticKeys.back,
                        ContactsSyncConstants.AnalyticKeys.SCREEN to ContactsSyncConstants.AnalyticKeys.Permission_screen
                    )
                )
                popBackStack()
            }
        }
    }

    private fun showContactAccessView() {
       binding.contactAccessLayout.visibility = View.VISIBLE
    }

    private fun hideContactAccessView() {
        binding.contactAccessLayout.visibility = View.GONE
    }

    private fun showContactSyncProgressLayout() {
       binding.contactSyncProgressLayout.visibility = View.VISIBLE
        uiScope.launch {
            val totalContact = totalContactDeferred.await()
            withContext(Dispatchers.Main){
                setupSyncProgressIndication(totalContact)
            }
        }


    }

    private fun hideContactSyncProgressLayout() {
        if (isBindingInitialized() && lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED))
            binding.contactSyncProgressLayout.visibility = View.GONE
    }

    @NeedsPermission(Manifest.permission.READ_CONTACTS)
    fun showSyncLayoutAndStartSyncTimer() {
        job?.cancel()
        job = uiScope.launch {
            val totalContacts = totalContactDeferred.await()
            if (totalContacts > 0) {
                withContext(Dispatchers.Main) {
                    hideContactAccessView()
                    showContactSyncProgressLayout()
                    fetchContacts()
                }
            } else {
                withContext(Dispatchers.Main) {
                    delay(500)
                    getString(com.jar.app.feature_contacts_sync_common.R.string.feature_contacts_sync_common_no_contacts_found).snackBar(
                        binding.root,
                        com.jar.app.core_ui.R.drawable.feature_duo_ic_error,
                        progressColor = com.jar.app.core_ui.R.color.color_C05357,
                        duration = 5000L
                    )
                }


            }
        }

    }

    private fun observeLiveData() {
        val weakReference: WeakReference<View> = WeakReference(binding.root)
        viewModel.contactListStaticData.observeNetworkResponse(
            viewLifecycleOwner,
            weakReference,
            onSuccess = {
                binding.tvHeaderText.text = it?.contactListText?.header
            }
        )
        viewModel.contactSyncedLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            weakReference,
            onSuccess = {
                when (com.jar.app.feature_contact_sync_common.shared.domain.model.ContactSyncState.valueOf(it)) {
                    com.jar.app.feature_contact_sync_common.shared.domain.model.ContactSyncState.SYNC_SUCCESS -> {
                        isContactSyncSuccess = true
                    }
                    com.jar.app.feature_contact_sync_common.shared.domain.model.ContactSyncState.NOT_RECEIVED -> viewModel.fetchLocalContactsAndUploadToServer()
                    com.jar.app.feature_contact_sync_common.shared.domain.model.ContactSyncState.SYNC_PENDING -> {
                        syncRetryDelay += 500
                        syncRetryCount++
                        if (syncRetryCount < MAX_SYNC_RETRY_COUNT) {
                            viewModel.fetchContactProcessingStatus(syncRetryDelay)
                        }
                    }
                }
            }
        )

        viewModel.fetchLocalContactAndUploadToServerLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            weakReference,
            onLoading = {
              //  showLoadingView()
            },
            onSuccessWithNullData = {
                syncRetryCount = 0
                syncRetryDelay = 0
                viewModel.fetchContactProcessingStatus()
            })

    }

    private fun showContactSyncSuccessLayout() {
        binding.apply {
            contactSyncSuccessHeader.visibility = View.VISIBLE
            contactSyncedLottie.visibility = View.VISIBLE
            contactSyncSuccessHeader.text =
                binding.root.resources.getString(com.jar.app.feature_contacts_sync_common.R.string.feature_contacts_sync_commoned_succesfully)
        }

        binding.contactSyncedLottie.playLottieWithUrlAndExceptionHandling(
            requireContext(), BaseConstants.LottieUrls.SMALL_CHECK
        ).addAnimatorListener(object : AnimatorListener {
            override fun onAnimationStart(p0: Animator) {

            }

            override fun onAnimationEnd(p0: Animator) {
                uiScope.launch {
                    delay(1000)
                    hideContactSyncSuccessLayout()
                    navigateOut()
                }
            }

            override fun onAnimationCancel(p0: Animator) {

            }

            override fun onAnimationRepeat(p0: Animator) {

            }

        })
    }

    private fun hideContactSyncSuccessLayout() {
        binding.apply {
            contactSyncSuccessHeader.visibility = View.GONE
            contactSyncedLottie.visibility = View.GONE
            contactSyncFooterText.visibility = View.GONE

        }
    }

    private fun setupSyncProgressIndication(totalContact: Long) {
        binding.syncProgressIndicator.progressMax = totalContact.toFloat()

        analyticsHandler.postEvent(
            ContactsSyncConstants.AnalyticKeys.InviteContactsStatusScreenShown, mapOf(
                ContactsSyncConstants.AnalyticKeys.SCREEN to ContactsSyncConstants.AnalyticKeys.Permission_screen,
                ContactsSyncConstants.AnalyticKeys.Feature_Type to args.featureType,
                ContactsSyncConstants.AnalyticKeys.screenstatus to "Contacts Synced Successfully",
            )
        )
        animation =
            ObjectAnimator.ofFloat(binding.syncProgressIndicator, "progress", 0f, totalContact.toFloat())
        animation?.apply {
            duration = 5000
            interpolator = LinearInterpolator()
            addUpdateListener { anim ->
                try {
                    val progressValue = anim.animatedValue as Float
                    binding.syncProgressIndicator.progress = progressValue
                    binding.syncContactProgress.text = binding.root.resources.getString(
                        com.jar.app.feature_contacts_sync_common.R.string.feature_contacts_sync_common_no_of_contacts_synced,
                        getPercentString(progressValue, totalContact)
                    )
                } catch (e: Exception) {
                }
            }
            addListener(object : AnimatorListener {

                override fun onAnimationStart(p0: Animator) {

                }

                override fun onAnimationEnd(p0: Animator) {
                    lifecycleScope.launch {
                        delay(500)
                        hideContactSyncProgressLayout()

                        if (isContactSyncSuccess) {
                            showContactSyncSuccessLayout()
//                            viewModel.fetchContactsWithoutPaging()
                        } else {
                            showProgressBar()
                        }
                    }

                }

                override fun onAnimationCancel(p0: Animator) {

                }

                override fun onAnimationRepeat(p0: Animator) {

                }

            })
            start()
        }
    }

    private fun getPercentString(progressValue: Float, totalContact: Long): String {
        if (totalContact == 0L) return "0%"
        val percent = ((progressValue / totalContact) * 100).toInt()
        return "$percent%"
    }

    private fun navigateOut() {
        args.deeplink?.takeIf { !it.isNullOrBlank() }?.let {
            navigateTo(
                uri = it,
                shouldAnimate = true,
                popUpTo = com.jar.app.feature_contacts_sync_common.R.id.contactPermissionFragment,
                inclusive = true
            )
        } ?: run {
            popBackStack()
        }
    }


    private fun fetchContacts() {
        //showLoadingView()
        viewModel.fetchContactProcessingStatus()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    @OnNeverAskAgain(Manifest.permission.READ_CONTACTS)
    fun onNeverAskAgain() {
        val intent = Intent()
        intent.action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", requireContext().packageName, null)
        intent.data = uri
        startActivity(intent)
    }


    @OnPermissionDenied(Manifest.permission.READ_CONTACTS)
    fun onPermissionDenied() {
        uiScope.launch {
            delay(500)
//            analyticsHandler.post // todo add event here of denied
            getString(com.jar.app.feature_contacts_sync_common.R.string.feature_contacts_sync_common_contact_permission_denied).snackBar(
                binding.root,
                com.jar.app.core_ui.R.drawable.feature_duo_ic_error,
                progressColor = com.jar.app.core_ui.R.color.color_C05357,
                duration = 5000L
            )
        }

        showContactAccessView()
    }


    override fun onResume() {
        super.onResume()

        if (requireContext().hasContactPermission()) {
            showSyncLayoutAndStartSyncTimerWithPermissionCheck()
        } else {
            showContactAccessView()
        }
    }

    override fun onDestroyView() {
        animation?.cancel()
        job?.cancel()
        super.onDestroyView()
    }
}