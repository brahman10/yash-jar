package com.jar.app.feature_jar_duo.impl.ui.delete

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_jar_duo.R
import com.jar.app.feature_jar_duo.databinding.FeatureDuoFragmentDeleteGroupBinding
import com.jar.app.feature_jar_duo.shared.domain.model.RefreshGroupListEvent
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference

@AndroidEntryPoint
internal class DuoDeleteGroupFragment :
    BaseBottomSheetDialogFragment<FeatureDuoFragmentDeleteGroupBinding>() {

    private val viewModel by viewModels<DuoDeleteGroupViewModel> {
        defaultViewModelProviderFactory
    }

    private val args by navArgs<DuoDeleteGroupFragmentArgs>()

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureDuoFragmentDeleteGroupBinding
        get() = FeatureDuoFragmentDeleteGroupBinding::inflate

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig(isCancellable = true)

    override fun setup() {
        setupUI()
        setupListeners()
        observeLiveData()
    }

    private fun setupUI() {
        binding.tvDeleteGroup.text =
            getString(R.string.feature_duo_sure_you_want_to_delete_this_duo)
        binding.tvDeleteGroupDetail.text =
            getString(R.string.feature_duo_saving_together_and_competing_with_friends_will_keep_you_motivated_to_save)
    }

    private fun setupListeners() {
        binding.btnDelete.setDebounceClickListener {
            viewModel.deleteGroup(args.groupID)
        }
        binding.btnDontDelete.setDebounceClickListener {
            popBackStack()
        }
    }

    private fun observeLiveData() {
        viewModel.deleteGroupLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onLoading = {
                showProgressBar()
                        },
            onSuccessWithNullData = {
                dismissProgressBar()
                EventBus.getDefault().post(com.jar.app.feature_jar_duo.shared.domain.model.RefreshGroupListEvent())
                viewModel.fetchGroupList()
            },
            onError = { dismissProgressBar() }
        )

        viewModel.listGroupLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onSuccess = {
                popBackStack(R.id.duosList, false)
            }
        )
    }
}