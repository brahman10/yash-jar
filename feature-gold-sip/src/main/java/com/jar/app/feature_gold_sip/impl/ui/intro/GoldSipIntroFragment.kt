package com.jar.app.feature_gold_sip.impl.ui.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.dp
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.feature_gold_sip.databinding.FeatureGoldSipFragmentIntroBinding
import com.jar.app.feature_gold_sip.shared.GoldSipMR
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import com.jar.app.core_ui.extension.snackBar


@AndroidEntryPoint
internal class GoldSipIntroFragment : BaseFragment<FeatureGoldSipFragmentIntroBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureGoldSipFragmentIntroBinding
        get() = FeatureGoldSipFragmentIntroBinding::inflate

    private val viewModelProvider by viewModels<GoldSipIntroViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }
    private val adapter = GoldSipIntroAdapter()
    private val spaceItemDecoration = SpaceItemDecoration(2.dp, 4.dp)
    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListener()
        observeLiveData()
    }

    private fun setupUI() {
        binding.toolbar.separator.isVisible = true
        binding.toolbar.tvTitle.text = getCustomString(GoldSipMR.strings.feature_gold_sip_label)
        binding.toolbar.ivTitleImage.setImageResource(com.jar.app.core_ui.R.drawable.core_ui_ic_gold_sip)
        viewModel.fetchGoldSipIntro()
        binding.rvIntroData.layoutManager = LinearLayoutManager(requireContext())
        binding.rvIntroData.adapter = adapter
        binding.lottieAnimation.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            com.jar.app.feature_gold_sip.shared.util.GoldSipConstants.LottieUrl.GOLD_SIP_INFO
        )
        viewModel.fireSipIntroEvent(
            com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown_SIPEducationScreen,
            com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown
        )
    }

    private fun setupListener() {
        binding.toolbar.btnBack.setDebounceClickListener {
            popBackStack()
        }

        binding.btnNext.setDebounceClickListener {
            viewModel.fireSipIntroEvent(
                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.Shown_SIPEducationScreen,
                com.jar.app.feature_gold_sip.shared.util.GoldSipEventKey.CTA_clicked
            )
            navigateTo(
                GoldSipIntroFragmentDirections.actionGoldSipIntroFragmentToGoldSipTypeSelectionFragment(
                    null
                )
            )
        }
    }

    private fun observeLiveData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.goldSipIntroFlow.collect(
                    onLoading = { showProgressBar() },
                    onSuccess = {
                        dismissProgressBar()
                        binding.tvSaveSmartWithSip.text = it.goldSipIntro.title
                        adapter.submitList(it.goldSipIntro.goldSipDataList)
                    },
                    onError = { errorMessage, _ ->
                        errorMessage.snackBar(binding.root)
                        dismissProgressBar()
                    }
                )
            }
        }
    }

}