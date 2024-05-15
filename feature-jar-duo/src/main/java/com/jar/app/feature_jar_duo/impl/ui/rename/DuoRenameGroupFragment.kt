package com.jar.app.feature_jar_duo.impl.ui.rename

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.jar.app.base.ui.fragment.BaseDialogFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.textChanges
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.extension.*
import com.jar.app.feature_jar_duo.R
import com.jar.app.feature_jar_duo.databinding.FeatureDuoRenameGroupBinding
import com.jar.app.feature_jar_duo.shared.domain.model.RefreshGroupListEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference

@AndroidEntryPoint
internal class DuoRenameGroupFragment : BaseDialogFragment<FeatureDuoRenameGroupBinding>() {

    private val viewModel by viewModels<DuoRenameGroupViewModel> {
        defaultViewModelProviderFactory
    }

    private val args by navArgs<DuoRenameGroupFragmentArgs>()

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureDuoRenameGroupBinding
        get() = FeatureDuoRenameGroupBinding::inflate

    override val dialogConfig: DialogFragmentConfig
        get() = DialogFragmentConfig(isCancellable = true)

    override fun setup() {
        setupUI()
        setupListeners()
        observeLiveData()
    }

    private fun setupUI() {
        binding.etRename.setText(args.groupName)
        binding.etRename.setSelection(args.groupName.length)

        binding.tvEnterName.text = getString(R.string.feature_duo_enter_a_name)
        binding.tvCancel.text = getString(R.string.feature_duo_cancel)

        binding.etRename.textChanges()
            .debounce(100)
            .onEach {
                binding.btnSAVE.setDisabled(it.isNullOrBlank() || (it.toString() == args.groupName))
            }
            .launchIn(uiScope)
        binding.etRename.showKeyboard()
        binding.tvSuccess.text = getString(R.string.feature_duo_yay_name_updated_successfully)

        binding.lottieSuccess.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            "${BaseConstants.CDN_BASE_URL}/LottieFiles/Generic/tick.json"
        )
    }

    private fun setupListeners() {
        binding.btnSAVE.setDebounceClickListener {
            val groupName = binding.etRename.text?.toString()
            if (groupName.isNullOrBlank().not()) {
                viewModel.renameGroup(args.groupID, groupName!!)
            } else {
                getString(R.string.feature_duo_please_enter_the_group_name).snackBar(binding.root)
            }
        }

        binding.tvCancel.setDebounceClickListener {
            popBackStack()
        }

        binding.ivBackspace.setDebounceClickListener {
            binding.etRename.text?.clear()
        }
    }

    private fun observeLiveData() {
        viewModel.renameGroupLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onLoading = { showProgressBar() },
            onSuccessWithNullData = {
                dismissProgressBar()
                binding.clRename.slideToRevealNew(
                    binding.clSuccess,
                    onAnimationEnd = {
                        uiScope.launch {
                            delay(2000)
                            EventBus.getDefault().post(RefreshGroupListEvent())
                            dismissAllowingStateLoss()
                        }
                    }
                )
            },
            onError = { dismissProgressBar() }
        )
    }


}