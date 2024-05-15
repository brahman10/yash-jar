package com.jar.app.feature_round_off.impl.ui.sms_permission

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.SmsPermissionGivenEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.openAppInfo
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_round_off.R
import com.jar.app.feature_round_off.databinding.FeatureRoundOffFragmentSmsPermissionFromRoundOffBinding
import com.jar.app.feature_round_off.shared.MR
import com.jar.app.feature_round_off.shared.util.RoundOffConstants
import com.jar.app.feature_round_off.shared.util.RoundOffEventKey
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnNeverAskAgain
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions
import javax.inject.Inject

@AndroidEntryPoint
@RuntimePermissions
class SmsPermissionFromRoundOffFragment :
    BaseFragment<FeatureRoundOffFragmentSmsPermissionFromRoundOffBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureRoundOffFragmentSmsPermissionFromRoundOffBinding
        get() = FeatureRoundOffFragmentSmsPermissionFromRoundOffBinding::inflate

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    override fun setupAppBar() {
        EventBus.getDefault().post(AppBarData())
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        binding.toolbar.tvTitle.text = getCustomString(MR.strings.feature_round_off_round_off_label)
        binding.toolbar.ivTitleImage.isVisible = true
        binding.toolbar.ivTitleImage.setImageResource(R.drawable.feature_round_off_ic_round_off)
        Glide.with(this)
            .load(BaseConstants.CDN_BASE_URL + com.jar.app.feature_round_off.shared.util.RoundOffConstants.Illustration.SMS_PERMISSION)
            .into(binding.ivSmsPermission)
    }

    private fun setupListeners() {
        binding.btnGrantPermission.setDebounceClickListener {
            readSmsWithPermissionCheck()
        }
        binding.toolbar.btnBack.setDebounceClickListener {
            popBackStack()
        }
    }

    @NeedsPermission(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS)
    fun readSms() {
        EventBus.getDefault().post(SmsPermissionGivenEvent())
        navigateTo(
            SmsPermissionFromRoundOffFragmentDirections.actionSmsPermissionFromRoundOffFragmentToRoundOffCalculationLoadingFragment(),
            popUpTo = R.id.smsPermissionFromRoundOffFragment,
            inclusive = true
        )
    }

    @OnPermissionDenied(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS)
    fun onPermissionDenied() {
        getCustomString(MR.strings.sms_permission_denied_error).snackBar(binding.root)
    }

    @OnNeverAskAgain(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS)
    fun onPermissionNeverAskAgain() {
        getCustomString(MR.strings.permission_needed_desc).snackBar(binding.root)
        requireContext().openAppInfo()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }
}