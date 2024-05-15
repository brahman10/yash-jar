package com.jar.app.feature_profile.impl.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.jar.app.base.data.event.UpdateSideNavProfilePicEvent
import com.jar.app.base.data.livedata.SharedPreferencesUserLiveData
import com.jar.app.base.ui.BaseResources
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.util.orZero
import com.jar.app.core_base.domain.model.KycProgressResponse
import com.jar.app.core_base.domain.model.User
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orFalse
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.util.observeNetworkResponseUnwrapped
import com.jar.app.feature_kyc.api.KycApi
import com.jar.app.feature_lending_kyc.api.LendingKycApi
import com.jar.app.feature_lending_kyc.shared.util.LendingKycEventKey
import com.jar.app.feature_profile.databinding.FragmentProfileBinding
import com.jar.app.feature_profile.domain.ProfileEventKey
import com.jar.app.feature_profile.shared.MR
import com.jar.app.feature_user_api.api.UserApi
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import dev.icerock.moko.resources.StringResource
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class ProfileFragment : BaseFragment<FragmentProfileBinding>(), BaseResources {

    @Inject
    lateinit var userLiveData: SharedPreferencesUserLiveData

    @Inject
    lateinit var userApi: UserApi

    @Inject
    lateinit var kycApi: KycApi

    @Inject
    lateinit var lendingKycApi: LendingKycApi

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var prefs: PrefsApi

    private val viewModelProvider by viewModels<EditProfileFragmentViewModelAndroid> { defaultViewModelProviderFactory }
    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentProfileBinding
        get() = FragmentProfileBinding::inflate

    var appStartTime: Long = 0L

    private var userHasProfilePicture: Boolean? = null

    private var lendingKycResponse: KycProgressResponse? = null

    companion object {
        const val DEFAULT = "DEFAULT"

        fun newInstance() = ProfileFragment()
    }

    override fun setupAppBar() {
    }

    override fun setup(savedInstanceState: Bundle?) {
        toggleIdentityVerification(shouldShow = false)
        getData()
        observeLiveData()
        initClickListeners()
    }

    private fun getData() {
        viewModel.getSavedAddress()
        viewModel.fetchPrimaryUpiId()
        viewModel.fetchUserEmail()
    }

    private fun openProfilePicDialog() {
        analyticsHandler.postEvent(
            ProfileEventKey.Events.Clicked_ProfilePicture_ProfileScreen, mapOf(
                ProfileEventKey.Props.ProfilePicture to if (userHasProfilePicture.orFalse()) ProfileEventKey.Props.Yes else ProfileEventKey.Props.No
            )
        )
        val uri = "android-app://com.jar.app/editProfilePic/${com.jar.app.feature_profile.util.Constants.PROFILE}"
        navigateTo(uri)
    }

    private fun initClickListeners() {
        binding.ivEditProfilePic.setDebounceClickListener {
            openProfilePicDialog()
        }

        binding.ivProfilePicture.setDebounceClickListener {
            openProfilePicDialog()
        }

        binding.tvUserName.setDebounceClickListener {
            analyticsHandler.postEvent(ProfileEventKey.Events.Clicked_Name_ProfileScreen)
            val uri = "android-app://com.jar.app/editProfileName"
            navigateTo(uri)
        }

        binding.tvUserAge.setDebounceClickListener {
            analyticsHandler.postEvent(
                ProfileEventKey.Events.Clicked_Age_ProfileScreen, mapOf(
                    ProfileEventKey.Props.Age to userLiveData.value?.age.orZero()
                )
            )
            val uri = "android-app://com.jar.app/editProfileAge"
            navigateTo(uri)
        }

        binding.tvUserGender.setDebounceClickListener {
            analyticsHandler.postEvent(
                ProfileEventKey.Events.Clicked_Gender_ProfileScreen, mapOf(
                    ProfileEventKey.Props.Gender to userLiveData.value?.gender.orEmpty()
                )
            )
            val uri = "android-app://com.jar.app/editProfileGender"
            navigateTo(uri)
        }

        //TODO:Explicitly commented for now asked by Product
//        binding.tvUserPhone.setDebounceClickListener {
//            analyticsHandler.postEvent(
//                ProfileEventKey.Events.Clicked_EditPhoneNumber_ProfileScreen, mapOf(
//                    ProfileEventKey.Props.PhoneNumber to userLiveData.value?.phoneNumber.orEmpty()
//                )
//            )
//            val uri = "android-app://com.jar.app/editProfileNumber"
//            navigateTo(uri)
//        }

        binding.tvEmail.setDebounceClickListener {
            analyticsHandler.postEvent(
                ProfileEventKey.Events.Clicked_EditPhoneNumber_ProfileScreen, mapOf(
                    ProfileEventKey.Props.Email to userLiveData.value?.email.orEmpty()
                )
            )
            val uri = "android-app://com.jar.app/editProfileEmail"
            navigateTo(uri)
        }

        binding.tvUserSavedAddresses.setDebounceClickListener {
            analyticsHandler.postEvent(
                ProfileEventKey.Events.Clicked_SavedAddresses_ProfileTab, mapOf(
                    ProfileEventKey.Props.NoOfSavedAddresses to viewModel.savedAddressLiveData.value.data?.data?.addresses?.size.orZero()
                )
            )
            userApi.openUserSavedAddress(BaseConstants.PROFILE)
        }

        binding.tvUserLendingKyc.setDebounceClickListener {
            lendingKycResponse?.let {
                lendingKycApi.openLendingKycWithProgressResponse(
                    BaseConstants.LendingKycFromScreen.PROFILE,
                    it
                )
            }
        }
        binding.tvUserLendingKyc.setDebounceClickListener {
            lendingKycResponse?.let {
                lendingKycApi.openLendingKycWithProgressResponse(
                    BaseConstants.LendingKycFromScreen.PROFILE,
                    it
                )
            }
        }
    }

    private fun observeLiveData() {
        val viewRef: WeakReference<View> = WeakReference(binding.root)

        userLiveData.observe(viewLifecycleOwner) {
            it?.let {
                it.profilePicUrl?.let {profilePicture->
                    loadImage(it, profilePicture)
                }
                binding.tvUserName.text = it.getFullName()
                prefs.setUserName(it.getFullName().orEmpty())
                binding.tvUserPhone.text = it.phoneNumber
                binding.tvUserAge.text = it.age.toString()
                val gender =
                    Gender.values().find { enum -> enum.name == it.gender } ?: Gender.DEFAULT
                binding.tvUserGender.setText(gender.gender.resourceId)
                it.email?.let {
                    binding.tvEmail.text = it
                }

                setViewsForPrimaryUpiId(prefs.getPrimaryUpiId())
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.userKycStatusLiveData.collect(
                    onSuccess = {
                        it?.let { kycStatus ->
                            binding.tvUserKycVerification.setDebounceClickListener {
                                analyticsHandler.postEvent(
                                    ProfileEventKey.Events.Clicked_KYC_ProfileScreen, mapOf(
                                        ProfileEventKey.Props.Status to kycStatus.kycStatus.orEmpty()
                                    )
                                )
                                kycApi.openKYC(
                                    kycStatus,
                                    BaseConstants.KycFromScreen.PROFILE
                                )
                            }
                            toggleIdentityVerification(
                                shouldShow = true,
                                title = kycStatus.title.orEmpty(),
                                isVerified = kycStatus.isVerified()
                            )
                        }
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.savedAddressLiveData.collect(
                    onSuccess = {
                        val addressCount = it.addresses.size.orZero()
                        binding.tvUserSavedAddresses.text =
                            if (addressCount == 0) "" else addressCount.toString()
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.userLendingKycProgressLiveData.collect(
                    onSuccess = {
                        lendingKycResponse = it
//                toggleLendingKycVerified(it.kycVerified)
                    }
                )
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.updateUserFlow.collect(
                    onSuccess = {
                        binding.tvEmail.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                if (it?.email.isNullOrEmpty()) com.jar.app.core_ui.R.color.color_ACA1D3 else com.jar.app.core_ui.R.color.color_EEEAFF
                            )
                        )
                        it?.email?.let {
                            binding.tvEmail.text = it
                        } ?: kotlin.run {
                            binding.tvEmail.text =
                                getCustomString(MR.strings.feature_profile_add_your_email)
                        }
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.updateUserFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        it?.let {
                            viewModel.updateUserLocally(it)
                        }
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.primaryUpiIdLiveData.collect(
                    onSuccess = {
                        it.primaryUpi?.primaryUpiId?.let {
                            prefs.setPrimaryUpiId(it)
                            setViewsForPrimaryUpiId(it)
                        }
                    }
                )
            }
        }
    }

    private fun loadImage(user: User?, url: String?) {
        EventBus.getDefault().post(UpdateSideNavProfilePicEvent())
        userHasProfilePicture = !url.isNullOrEmpty()
        binding.ivProfilePicture.setUserImage(user)
    }

    private fun toggleIdentityVerification(
        shouldShow: Boolean = false,
        title: String = getCustomString(MR.strings.feature_lending_kyc_complete_now),
        isVerified: Boolean = false
    ) {
        binding.tvKycVerification.isVisible = shouldShow
        binding.tvUserKycVerification.isVisible = shouldShow
        binding.dividerKycVerification.isVisible = shouldShow
        binding.tvUserKycVerification.text =
            title.ifEmpty { getCustomString(MR.strings.feature_lending_kyc_complete_now) }
        binding.ivKycVerified.isVisible = isVerified
    }

    private fun toggleLendingKycVerified(
        isVerified: Boolean = false
    ) {
        binding.tvUserAge.alpha = if (isVerified) 0.6f else 1f
        binding.tvUserAge.isClickable = isVerified.not()
        binding.tvUserAge.isEnabled = isVerified.not()
        binding.tvUserGender.alpha = if (isVerified) 0.6f else 1f
        binding.tvUserGender.isClickable = isVerified.not()
        binding.tvUserGender.isEnabled = isVerified.not()
        binding.tvUserLendingKyc.text =
            if (isVerified) getCustomString(MR.strings.feature_kyc_verified) else getCustomString(
                MR.strings.feature_lending_kyc_complete_now
            )
        binding.ivLendingKycVerified.isVisible = isVerified
        if (isVerified)
            analyticsHandler.postEvent(LendingKycEventKey.Shown_KYCVerifiedInProfileScreen)
    }

    enum class Gender(val gender: StringResource) {
        MALE(MR.strings.feature_profile_male),
        FEMALE(MR.strings.feature_profile_female),
        OTHER(MR.strings.feature_profile_other),
        DEFAULT(MR.strings.feature_profile_gender_default)
    }

    private fun setViewsForPrimaryUpiId(upiId: String?) {
        if (upiId.isNullOrEmpty().not()) {
            binding.primaryUpiIdGroup.isVisible = true
            binding.tvPrimaryUpiId.text = upiId.orEmpty()
        } else {
            binding.primaryUpiIdGroup.isVisible = false
        }
    }

    override fun onResume() {
        super.onResume()
        appStartTime = System.currentTimeMillis()
        analyticsHandler.postEvent(ProfileEventKey.Events.Shown_ProfileScreen_Account)
        viewModel.fetchUserKycStatus()
    }

    override fun onPause() {
        super.onPause()
        analyticsHandler.postEvent(
            ProfileEventKey.Events.Exit_ProfileTab_Account, mapOf(
                ProfileEventKey.Props.TimeSpent to System.currentTimeMillis() - appStartTime
            )
        )
    }
}