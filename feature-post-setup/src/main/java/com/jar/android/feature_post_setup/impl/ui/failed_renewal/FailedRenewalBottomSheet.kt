package com.jar.android.feature_post_setup.impl.ui.failed_renewal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.android.feature_post_setup.databinding.FeaturePostSetupFailedRenewalBottomSheetBinding
import com.jar.android.feature_post_setup.impl.data.event.UpdateBankEvent
import com.jar.app.base.data.event.HandleDeepLinkEvent
import com.jar.app.base.data.event.UpdateDSAutopayBankEvent
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.decodeUrl
import com.jar.app.base.util.dp
import com.jar.app.base.util.orFalse
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.R
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.core_ui.label_and_value.LabelAndValue
import com.jar.app.core_ui.label_and_value.LabelAndValueAdapter
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.feature_post_setup.domain.model.DSFailureInfo
import com.jar.app.feature_post_setup.util.PostSetupEventKey
import com.jar.internal.library.jar_core_network.api.util.Serializer
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class FailedRenewalBottomSheet :
    BaseBottomSheetDialogFragment<FeaturePostSetupFailedRenewalBottomSheetBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeaturePostSetupFailedRenewalBottomSheetBinding
        get() = FeaturePostSetupFailedRenewalBottomSheetBinding::inflate
    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig()

    @Inject
    lateinit var serializer: Serializer

    private val viewModel: FailedRenewalViewModel by viewModels()
    private val args: FailedRenewalBottomSheetArgs by navArgs()

    private val dsFailureInfo by lazy {
        args.dSFailureInfo?.let {
            serializer.decodeFromString<DSFailureInfo?>(decodeUrl(it))
        }
    }
    private var isRoundOffsEnabled = false
    private var dailySavingAmount = 0f
    private var mandateAmount = 0f
    private var adapter: LabelAndValueAdapter? = null
    private val spaceItemDecoration = SpaceItemDecoration(0.dp, 8.dp)
    override fun setup() {
        setupUI()
        setupListener()
        observeLiveData()
    }

    private fun setupUI() {
        binding.rvBankDetails.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBankDetails.addItemDecorationIfNoneAdded(spaceItemDecoration)
        adapter = LabelAndValueAdapter()
        binding.rvBankDetails.adapter = adapter

        dsFailureInfo?.let {
            generateLabelValueData(
                DSBankSetupDetails(
                    provider = it.provider,
                    upiId = it.upiId,
                    autopaySubsId = it.autopaySubsId,
                    bankLogo = it.bankLogo,
                    bankName = it.bankName
                )
            )
        } ?: kotlin.run {
            viewModel.fetchUserRoundOffDetails()
        }
    }

    private fun setupListener() {
        binding.btnUpdateAutoSave.setDebounceClickListener {
            dsFailureInfo?.let {
                EventBus.getDefault().post(UpdateBankEvent())
            } ?: kotlin.run {
                EventBus.getDefault().post(UpdateDSAutopayBankEvent(dailySavingAmount,mandateAmount,isRoundOffsEnabled))
            }
            dismiss()
        }
    }
    private fun observeLiveData() {
        val viewRef: WeakReference<View> = WeakReference(binding.root)
        viewModel.roundOffDetailsLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onLoading = { showProgressBar() },
            onSuccess = {
                isRoundOffsEnabled = it.enabled.orFalse() && it.autoSaveEnabled.orFalse()
                viewModel.fetchUserDailySavingsDetails()
            },
            onError = { dismissProgressBar() }
        )

        viewModel.dailySavingsDetailsLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            viewRef,
            onSuccess = {
                dismissProgressBar()
                dailySavingAmount = it.subscriptionAmount
                mandateAmount = it.mandateAmount.orZero()
                generateLabelValueData(
                    DSBankSetupDetails(
                        provider = it.provider,
                        upiId = it.upiId,
                        autopaySubsId = it.subscriptionId,
                        bankLogo = it.bankLogo,
                        bankName = it.bankName
                    )
                )
            }
        )
    }

    private fun generateLabelValueData(dsBankSetupDetails: DSBankSetupDetails) {
        val list = ArrayList<LabelAndValue>()
        if (dsBankSetupDetails.provider.isNullOrEmpty().not())
            list.add(
                LabelAndValue(
                    getString(R.string.core_ui_upi_app),
                    dsBankSetupDetails.provider.orEmpty(),
                    labelColorRes = R.color.color_ACA1D3,
                    valueColorRes = R.color.white,
                    labelTextStyle = R.style.CommonTextViewStyle,
                    valueTextStyle = R.style.CommonTextViewStyle
                )
            )
        if (dsBankSetupDetails.upiId.isNullOrEmpty().not())
            list.add(
                LabelAndValue(
                    dsBankSetupDetails.upiId?.let { getString(R.string.core_ui_upi_id) }
                        ?: kotlin.run { getString(R.string.core_ui_subscription_id) },
                    dsBankSetupDetails.upiId ?: dsBankSetupDetails.autopaySubsId.orEmpty(),
                    labelColorRes = R.color.color_ACA1D3,
                    valueColorRes = R.color.white,
                    labelTextStyle = R.style.CommonTextViewStyle,
                    valueTextStyle = R.style.CommonTextViewStyle
                )
            )
        if ((dsBankSetupDetails.bankLogo ?: dsBankSetupDetails.bankName).isNullOrEmpty()
                .not()
        ) list.add(
            LabelAndValue(
                getString(R.string.core_ui_bank_account),
                dsBankSetupDetails.bankLogo ?: dsBankSetupDetails.bankName.orEmpty(),
                isTextualValue = dsBankSetupDetails.bankLogo.isNullOrEmpty(),
                labelColorRes = R.color.color_ACA1D3,
                valueColorRes = R.color.white,
                labelTextStyle = R.style.CommonTextViewStyle,
                valueTextStyle = R.style.CommonTextViewStyle
            )
        )
        adapter?.submitList(list)
    }

    data class DSBankSetupDetails(
        val provider: String? = null,
        val upiId: String? = null,
        val autopaySubsId: String? = null,
        val bankLogo: String? = null,
        val bankName: String? = null,
    )
}