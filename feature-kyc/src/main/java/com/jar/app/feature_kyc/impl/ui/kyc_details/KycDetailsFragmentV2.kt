package com.jar.app.feature_kyc.impl.ui.kyc_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarDefault
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.base.util.getMaskedString
import com.jar.app.core_base.util.orZero
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.item_decoration.BaseItemDecoration
import com.jar.app.feature_kyc.R
import com.jar.app.feature_kyc.databinding.FragmentKycDetailsV2Binding
import com.jar.app.feature_kyc.impl.ui.kyc_faq.KycFaqAdapter
import com.jar.app.feature_kyc.shared.domain.model.KYCStatusDetails
import com.jar.app.feature_kyc.shared.domain.model.KYCStatusInfo
import com.jar.app.feature_kyc.shared.util.KycConstants
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class KycDetailsFragmentV2 : BaseFragment<FragmentKycDetailsV2Binding>(),
    BaseItemDecoration.SectionCallback {

    private val viewModelProvider by viewModels<KycDetailsViewModelAndroid> { defaultViewModelProviderFactory }
    private val viewModel by lazy { viewModelProvider.getInstance() }


    private var showDocId = false

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private var pan: KYCStatusInfo? = null

    private val args by navArgs<KycDetailsFragmentV2Args>()

    private val spaceItemDecoration =
        com.jar.app.core_ui.item_decoration.SpaceItemDecoration(0.dp, 8.dp)

    private val headerItemDecoration =
        com.jar.app.core_ui.item_decoration.HeaderItemDecoration(this)

    private val adapter = KycFaqAdapter()

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentKycDetailsV2Binding
        get() = FragmentKycDetailsV2Binding::inflate

    private val baseEdgeEffectFactory = com.jar.app.core_ui.BaseEdgeEffectFactory()


    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateAppBarEvent(
                AppBarData(
                    ToolbarDefault(
                        getCustomString(com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_identity_verification),
                        showSeparator = true
                    )
                )
            )
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        getData()
        observeFlow()
        setupListeners()
    }

    private fun setupUI() {
        binding.rvFaq.adapter = adapter
        binding.rvFaq.edgeEffectFactory = baseEdgeEffectFactory
        binding.rvFaq.addItemDecorationIfNoneAdded(spaceItemDecoration, headerItemDecoration)
    }

    private fun getData() {
        viewModel.fetchKycDetails()
        viewModel.fetchFaq()
    }

    private fun setupListeners() {
        binding.tvShowPan.setOnClickListener {
            showDocId = !showDocId
            if (showDocId) {
                pan?.getKycDocType()?.hideMsg?.let {
                    binding.tvShowPan.text = getCustomString(it)
                }
                binding.tvShowPan.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.feature_kyc_ic_eye_hide, 0, 0, 0
                )
                binding.tvPan.text = pan?.docId
            } else {
                pan?.getKycDocType()?.showMsg?.let {
                    binding.tvShowPan.text = getCustomString(it)
                }
                binding.tvShowPan.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.feature_kyc_ic_eye_show, 0, 0, 0
                )
                binding.tvPan.text =
                    pan?.docId?.getMaskedString(1, pan?.docId?.length.orZero() - 2, "*")
            }
        }
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.kycDetailsFlow.collect(
                    onLoading = {
                        binding.shimmer.isVisible = true
                        binding.shimmer.startShimmer()
                    },
                    onSuccess = {
                        binding.shimmer.isVisible = false
                        binding.shimmer.stopShimmer()
                        bindPanDetails(it)
                    },
                    onSuccessWithNullData = {
                        binding.shimmer.isVisible = false
                        binding.shimmer.stopShimmer()
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                        binding.shimmer.stopShimmer()
                        binding.shimmer.isVisible = false
                        binding.panGroup.isVisible = false
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.faqFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        viewLifecycleOwner.lifecycleScope.launch {
                            val faqList = viewModel.getFlattenedFaqData()
                            adapter.submitList(faqList)
                        }
                    }, onError = { errorMessage, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }
    }

    private fun bindPanDetails(details: KYCStatusDetails?) {
        details?.panData ?: return
        pan = details.panData
        binding.panGroup.isVisible = true
        binding.tvName.text = pan?.name
        binding.tvDob.text = pan?.dob
        binding.tvPan.text = pan?.getHiddenDocId()
        binding.tvHeading.text = getCustomString(pan?.getKycDocType()?.docName!!)
        binding.tvVerifiedOn.text =
            getCustomStringFormatted(com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_verified_on, details.verifiedOn ?: "--")
        binding.tvPanHeading.text = getCustomString(pan?.getKycDocType()?.docType!!)
        binding.tvShowPan.text = getCustomString(pan?.getKycDocType()?.showMsg!!)
        analyticsHandler.postEvent(
            KycConstants.AnalyticsKeys.SHOWN_ID_DETAILS_IDENTITY_VERIFICATION_SCREEN,
            mapOf(
                EventKey.FromScreen to args.fromScreen
            )
        )
    }

    override fun isItemDecorationSection(position: Int): Boolean {
        return when {
            adapter.currentList.isNullOrEmpty() -> false
            position == 0 -> true
            else -> {
                val prev = adapter.currentList.getOrNull(position)?.type
                val next = adapter.currentList.getOrNull(position - 1)?.type
                prev != next
            }
        }
    }

    override fun getItemDecorationLayoutRes(position: Int): Int {
        return R.layout.cell_kyc_faq_header
    }

    override fun bindItemDecorationData(view: View, position: Int) {
        val header = view.findViewById<AppCompatTextView>(R.id.tvHeader)
        val title = adapter.currentList.getOrNull(position)?.type
        header.isVisible = title != null
        if (title != null)
            header.text = title
    }

}