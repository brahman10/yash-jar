package com.jar.app.feature_profile.impl.ui.profile.gender

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.jar.app.base.data.livedata.SharedPreferencesUserLiveData
import com.jar.app.base.ui.BaseResources
import com.jar.app.base.ui.fragment.BaseDialogFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.slideToRevealNew
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_utils.data.NetworkFlow
import com.jar.app.feature_profile.databinding.FragmentDialogEditProfileGenderBinding
import com.jar.app.feature_profile.domain.ProfileEventKey
import com.jar.app.feature_profile.shared.MR
import com.jar.app.feature_profile.ui.EditProfileGenderViewModel
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileGenderDialogFragment :
    BaseDialogFragment<FragmentDialogEditProfileGenderBinding>(), BaseResources {

    @Inject
    lateinit var userLiveData: SharedPreferencesUserLiveData

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val viewModelProvider by viewModels<EditProfileGenderViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    @Inject
    lateinit var networkFlow: NetworkFlow

    private var isNetworkAvailable = false

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentDialogEditProfileGenderBinding
        get() = FragmentDialogEditProfileGenderBinding::inflate

    override val dialogConfig: DialogFragmentConfig
        get() = DialogFragmentConfig(isCancellable = false)


    private var adapter: GenderAdapter? = null

    private val spaceItemDecoration =
        com.jar.app.core_ui.item_decoration.SpaceItemDecoration(0.dp, 8.dp)
    private val baseEdgeEffectFactory = com.jar.app.core_ui.BaseEdgeEffectFactory()

    override fun setup() {
        setupUI()
        initClickListeners()
        observeLiveData()
        getData()
        analyticsHandler.postEvent(ProfileEventKey.Events.Shown_SelectGender_GenderPopUp)
    }

    private fun setupUI() {
        binding.editProfileSuccessLayout.animTick.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            "${BaseConstants.CDN_BASE_URL}/LottieFiles/Generic/tick.json"
        )
        toggleMainButton(true)
        adapter = GenderAdapter(onGenderSelected = { genderData ->
            viewModel.updatedGenderSelection(genderData)
            toggleMainButton()
        })
        binding.rvGender.addItemDecorationIfNoneAdded(spaceItemDecoration)
        binding.rvGender.edgeEffectFactory = baseEdgeEffectFactory
        binding.rvGender.adapter = adapter
    }

    private fun initClickListeners() {
        binding.tvCancel.setDebounceClickListener {
            analyticsHandler.postEvent(ProfileEventKey.Events.Clicked_Cancel_GenderPopUp)
            dismiss()
        }

        binding.btnSave.setDebounceClickListener {
            viewModel.currentlySelected?.let { selectedGender ->
                viewModel.updateGender(selectedGender.genderType.name)
                analyticsHandler.postEvent(
                    ProfileEventKey.Events.Clicked_SaveGender_GenderPopUp, mapOf(
                        ProfileEventKey.Props.Gender to selectedGender.genderType.name
                    )
                )
            }
                ?: run { getCustomString(MR.strings.feature_profile_please_select_gender).snackBar(binding.root) }
        }
    }

    private fun observeLiveData() {
        networkFlow.networkStatus
            .onEach {
                isNetworkAvailable = it
                toggleMainButton()
            }
            .launchIn(uiScope)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.genderListLiveData.collectLatest {
                    adapter?.submitList(it)
                }
            }
        }

        userLiveData.observe(viewLifecycleOwner) { user ->
            user?.let {
                val gender = it.gender
                if (!gender.isNullOrBlank()) {
                    //Set in advance, otherwise launch{} causes race condition, leading to button being disabled
                    viewModel.currentlySelected = viewModel.preselect(gender)
                    toggleMainButton()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.updateUserLiveData.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onError = { errorMessage, errorCode ->
                        dismissProgressBar()
                        errorMessage.snackBar(binding.root)
                    },
                    onSuccess = {
                        dismissProgressBar()
                        viewModel.updateUserGenderLocally(it?.gender)
                        binding.editProfileSuccessLayout.tvSuccessDes.text =
                            getCustomString(MR.strings.feature_profile_gender_updated_successfully)
                        binding.cvProfileGenderContainer.slideToRevealNew(
                            viewToReveal = binding.editProfileSuccessLayout.root,
                            onAnimationEnd = {
                                binding.editProfileSuccessLayout.animTick.playAnimation()
                                uiScope.launch {
                                    analyticsHandler.postEvent(ProfileEventKey.Events.Shown_Success_GenderPopUp)
                                    delay(3000)
                                    dismissAllowingStateLoss()
                                }
                            }
                        )
                    }
                )
            }
        }
    }

    private fun getData() {
        viewModel.fetchGenderList()
    }

    private fun toggleMainButton(disableAnyway: Boolean = false) {
        val shouldEnable = if (disableAnyway) false else (
                isNetworkAvailable
                        && viewModel.currentlySelected != null)
        binding.btnSave.isEnabled = shouldEnable
    }
}