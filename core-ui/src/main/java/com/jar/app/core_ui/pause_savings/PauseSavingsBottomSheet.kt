package com.jar.app.core_ui.pause_savings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.decodeUrl
import com.jar.app.base.util.dp
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_ui.databinding.CoreUiPauseSavingBottomSheetBinding
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.internal.library.jar_core_network.api.util.Serializer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PauseSavingsBottomSheet :
    BaseBottomSheetDialogFragment<CoreUiPauseSavingBottomSheetBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> CoreUiPauseSavingBottomSheetBinding
        get() = CoreUiPauseSavingBottomSheetBinding::inflate
    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig()

    @Inject
    lateinit var serializer: Serializer

    private var adapter: PauseSavingsAdapter? = null
    private val viewModel: PauseSavingsViewModel by viewModels()
    private val args: PauseSavingsBottomSheetArgs by navArgs()
    private val spaceItemDecoration = SpaceItemDecoration(4.dp, 0.dp)

    private val pauseSavingData by lazy {
        serializer.decodeFromString<GenericPauseData>(decodeUrl(args.pauseSavingsData))
    }

    override fun setup() {
        setupUI()
        setupListener()
        observeLiveData()
    }

    private fun setupUI() {
        viewModel.fetchPauseOptions(pauseSavingData.list)
        binding.rvPauseDuration.isVisible = true
        binding.rvPauseDuration.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        adapter = PauseSavingsAdapter { pauseRoundOffOption, position ->
            updatePauseListOnClick(position)
        }
        binding.rvPauseDuration.adapter = adapter
        binding.rvPauseDuration.addItemDecorationIfNoneAdded(spaceItemDecoration)
    }

    private fun setupListener() {
        binding.btnPause.setDebounceClickListener {
            viewModel.pauseSavingOptionWrapper?.pauseSavingOption?.let {
                findNavController().currentBackStackEntry?.savedStateHandle?.set(
                    BaseConstants.PAUSE_SAVING_DIALOG_PAUSE_ACTION,
                    it
                )
            }
            dismiss()
        }

        binding.btnCancel.setDebounceClickListener {
            findNavController().currentBackStackEntry?.savedStateHandle?.set(
                BaseConstants.PAUSE_SAVING_DIALOG_DISMISSED,
                true
            )
            dismiss()
        }

        binding.ivCross.setDebounceClickListener {
            findNavController().currentBackStackEntry?.savedStateHandle?.set(
                BaseConstants.PAUSE_SAVING_DIALOG_DISMISSED,
                true
            )
            dismiss()
        }
    }

    private fun observeLiveData() {
        viewModel.pauseOptionsLiveData.observe(viewLifecycleOwner) {
            binding.btnPause.setDisabled(viewModel.pauseSavingOptionWrapper == null)
            it?.let { adapter?.submitList(it) }
        }
    }

    private fun updatePauseListOnClick(position: Int) {
        adapter?.currentList?.let {
            viewModel.updatePauseOptionListOnItemClick(it, position)
        }
    }

}