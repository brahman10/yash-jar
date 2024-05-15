package com.jar.app.feature_kyc.impl.ui.alternate_doc.choose_doc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.core_ui.BaseEdgeEffectFactory
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_kyc.KycV2NavigationDirections
import com.jar.app.feature_kyc.R
import com.jar.app.feature_kyc.databinding.FragmentChooseKycDocBinding
import com.jar.app.feature_kyc.shared.util.KycConstants
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class ChooseKycDocFragment : BaseFragment<FragmentChooseKycDocBinding>() {

    private var adapter: ChooseDocAdapter? = null

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val args by navArgs<ChooseKycDocFragmentArgs>()

    private val baseEdgeEffectFactory = BaseEdgeEffectFactory()

    private val spaceItemDecoration =
        com.jar.app.core_ui.item_decoration.SpaceItemDecoration(0.dp, 8.dp)

    private val viewModelProvider by viewModels<ChooseKycDocViewModelAndroid> { defaultViewModelProviderFactory }
    private val viewModel by lazy { viewModelProvider.getInstance() }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentChooseKycDocBinding
        get() = FragmentChooseKycDocBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        viewModel.fetchData()
        setupUI()
        observeFlow()
    }

    private fun setupUI() {
        setupToolbar()

        adapter = ChooseDocAdapter(
            onDocSelected = {
                analyticsHandler.postEvent(
                    KycConstants.AnalyticsKeys.CLICKED_BUTTON,
                    mapOf(KycConstants.AnalyticsKeys.OPTION_CHOSEN to it.title)
                )
                if (it.disable) {
                    getCustomStringFormatted(com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_retry_limit_exhausted, it.title).snackBar(
                        binding.root,
                        com.jar.app.core_ui.R.drawable.ic_filled_information_icon,
                        progressColor = com.jar.app.core_ui.R.color.color_016AE1,
                        translationY = -4.dp.toFloat()
                    )
                } else {
                    navigateTo(KycV2NavigationDirections.actionToUploadKycDoc(it, args.fromScreen))
                }
            }
        )
        binding.rvKycDocs.edgeEffectFactory = baseEdgeEffectFactory
        binding.rvKycDocs.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.rvKycDocs.adapter = adapter
        binding.rvKycDocs.layoutManager = LinearLayoutManager(context)
    }

    private fun setupToolbar() {
        binding.toolbar.tvTitle.text = getCustomString(com.jar.app.feature_kyc.shared.MR.strings.feature_kyc_choose_document)
        binding.toolbar.ivEndImage.setImageResource(R.drawable.feature_kyc_ic_question)
        binding.toolbar.lottieView.isVisible = false
        binding.toolbar.separator.isVisible = true
        binding.toolbar.ivEndImage.isVisible = true

        binding.toolbar.btnBack.setDebounceClickListener {
            popBackStack()
        }

        binding.toolbar.ivEndImage.setDebounceClickListener {
            navigateTo("android-app://com.jar.app/kycFaqFragmentV2", shouldAnimate = true)
        }
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.kycDocListFlow.collect(
                    onSuccess = {
                        it?.let {
                            adapter?.submitList(it.kycAlternateDocs.kycDoc)
                            binding.shimmerPlaceholder.stopShimmer()
                            binding.shimmerPlaceholder.isVisible = false
                            binding.rvKycDocs.isVisible = true
                            analyticsHandler.postEvent(
                                KycConstants.AnalyticsKeys.SHOWN_CHOOSE_DOCUMENT_SCREEN,
                                emptyMap()
                            )
                        }
                    },
                    onError = { errorMessage, _ ->
                        binding.shimmerPlaceholder.stopShimmer()
                    }
                )
            }
        }
    }
}