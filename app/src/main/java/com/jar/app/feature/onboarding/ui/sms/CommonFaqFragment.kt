package com.jar.app.feature.onboarding.ui.sms

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.navArgs
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.databinding.FragmentSmsFaqBinding
import com.jar.app.core_base.util.BaseConstants.StaticContentType
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_analytics.EventKey
import com.jar.app.core_ui.util.observeNetworkResponseUnwrapped
import com.jar.app.base.util.dp
import com.jar.app.core_ui.BaseEdgeEffectFactory
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class CommonFaqFragment : BaseBottomSheetDialogFragment<FragmentSmsFaqBinding>() {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val viewModelProvider by viewModels<CommonFaqFragmentViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val args by navArgs<CommonFaqFragmentArgs>()

    private var adapter: CommonFaqAdapter? = null

    private val spaceItemDecoration = SpaceItemDecoration(0.dp, 12.dp)

    private val baseEdgeEffectFactory = BaseEdgeEffectFactory()

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSmsFaqBinding
        get() = FragmentSmsFaqBinding::inflate

    override val bottomSheetConfig: BottomSheetConfig
        get() = DEFAULT_CONFIG

    override fun setup() {
        setupUI()
        observeLiveData()
        getData()
        addAnalyticsEvent()
    }

    private fun setupUI() {
        binding.tvTitle.text = args.title

        adapter = CommonFaqAdapter()
        binding.rvFaq.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.rvFaq.edgeEffectFactory = baseEdgeEffectFactory
        binding.rvFaq.adapter = adapter
    }

    private fun observeLiveData() {
        viewModel.smsFaqListFlow.asLiveData().observeNetworkResponseUnwrapped(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onSuccess = {
                adapter?.submitList(it)
            }
        )
    }

    private fun getData() {
        viewModel.fetchFaqList(StaticContentType.valueOf(args.type))
    }

    private fun addAnalyticsEvent() {
        when (args.type) {
            StaticContentType.SMS_FAQS.name ->
                analyticsHandler.postEvent(EventKey.ShownFAQ_PermissionScreen_Onboarding)

            StaticContentType.AUTO_INVEST_FAQS.name ->
                analyticsHandler.postEvent(EventKey.ShownHowScreen_AutoInvestScreen_Onboarding)
        }
    }
}