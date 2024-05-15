package com.jar.app.feature_lending.impl.ui.personal_details.address.add_address

import android.location.Address
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.DispatcherProvider
import com.jar.app.base.util.isFragmentInBackStack
import com.jar.app.base.util.textChanges
import com.jar.app.core_base.domain.model.User
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_base.util.orZero
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.slideToRevealNew
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_lending.BuildConfig
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FragmentLendingAddAddressBinding
import com.jar.app.feature_lending.impl.domain.event.UpdateLendingStepsToolbarEvent
import com.jar.app.feature_lending.impl.ui.common.LendingViewModel
import com.jar.app.feature_lending.impl.util.GeocoderUtil
import com.jar.app.feature_lending.shared.MR
import com.jar.app.feature_lending.shared.domain.LendingEventKey
import com.jar.app.feature_lending.shared.ui.step_view.LendingStep
import com.jar.app.feature_lending.shared.util.LendingConstants
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.app.core_base.util.EncryptionUtil
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

@AndroidEntryPoint
internal class LendingAddAddressFragment : BaseFragment<FragmentLendingAddAddressBinding>() {

    @Inject
    lateinit var geocoderUtil: GeocoderUtil

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var dispatcherProvider: DispatcherProvider

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var encryptionUtil: EncryptionUtil

    private val lendingViewModel by activityViewModels<LendingViewModel> { defaultViewModelProviderFactory }

    private val viewModelProvider by viewModels<LendingAddAddressViewModelAndroid> { defaultViewModelProviderFactory }
    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }


    private val args by navArgs<LendingAddAddressFragmentArgs>()

    private var placesClient: PlacesClient? = null

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLendingAddAddressBinding
        get() = FragmentLendingAddAddressBinding::inflate

    private var geocoderAddressItem: Address? = null

    private var placeAddressItem: Place? = null

    var subAddressLine2 = ""

    override fun setupAppBar() {
        EventBus.getDefault().post(
            UpdateAppBarEvent(
                AppBarData(
                    ToolbarNone
                )
            )
        )
    }

    override fun setup(savedInstanceState: Bundle?) {
        initGooglePlaces()
        if (args.placePrediction != null) {
            getAddressFromPlacesSdk(args.placePrediction?.placeId.orEmpty())
        } else if (args.lat != null && args.long != null) {
            getAddressFromGeocoder(args.lat?.toDoubleOrNull().orZero(), args.long?.toDoubleOrNull().orZero())
        } else {
            //Else do nothing, which means we have address and it is edit flow so just set the visibility to true
            binding.clContent.isVisible = true
        }
        observeFlow()
        setupUI()
        initClickListeners()
    }

    private fun getAddressFromPlacesSdk(placeId: String) {
        showLoadingView()
        val placeFields = listOf(Place.Field.ID, Place.Field.LAT_LNG)
        val request = FetchPlaceRequest.newInstance(placeId, placeFields)

        placesClient?.let {
            it.fetchPlace(request)
                .addOnSuccessListener { response: FetchPlaceResponse ->
                    dismissLoadingView()
                    val place = response.place
                    placeAddressItem = place
                    place.latLng?.let { it1 ->
                        getAddressFromGeocoder(it1.latitude, it1.longitude)
                    } ?: run {
                        getString(com.jar.app.core_ui.R.string.something_went_wrong).snackBar(binding.root)
                        dismissLoadingView(isError = true)
                    }
                }.addOnFailureListener {
                    getString(com.jar.app.core_ui.R.string.something_went_wrong).snackBar(binding.root)
                    dismissLoadingView(isError = true)
                }
        }
    }

    private fun getAddressFromGeocoder(lat: Double, long: Double) {
        showLoadingView()
        uiScope.launch(dispatcherProvider.io) {
            val addressWrapper = geocoderUtil.getAddress(
                lat,
                long
            )
            addressWrapper.let { geocoderAddressWrapper ->
                if (geocoderAddressWrapper.errorMessage.isNullOrEmpty()) {
                    geocoderAddressItem = geocoderAddressWrapper.address
                    subAddressLine2 = geocoderAddressItem?.subLocality.orEmpty()
                    withContext(dispatcherProvider.main) {
                        dismissLoadingView()
                        setupAddressBar()
                    }
                } else {
                    withContext(dispatcherProvider.main) {
                        geocoderAddressWrapper.errorMessage.snackBar(binding.root, translationY = 0f)
                        dismissLoadingView(isError = true)
                    }
                }
            }
        }
    }

    private fun initClickListeners() {
        binding.tvChange.setDebounceClickListener {
            val employmentDetails = lendingViewModel.loanApplications?.applications?.getOrNull(0)?.details?.EMPLOYMENT_DETAILS
            analyticsHandler.postEvent(
                LendingEventKey.OnClick_ReadyCash_PersonalDetails_AddAddress_Change,
                mapOf(
                    LendingEventKey.employmentType to employmentDetails?.employmentType.orEmpty(),
                    LendingEventKey.companyName to employmentDetails?.companyName.orEmpty(),
                    LendingEventKey.monthlyIncome to employmentDetails?.getMonthlyIncome().orZero(),
                    LendingEventKey.entryPoint to args.flowType,
                    LendingEventKey.chooseAddress to LendingEventKey.new,
                    LendingEventKey.addNewAddressVia to args.newAddressAddedVia
                )
            )
            if (findNavController().isFragmentInBackStack(R.id.lendingAddressOptionFragment)) {
                popBackStack()
            } else {
                navigateTo(
                    LendingAddAddressFragmentDirections.actionLendingAddAddressFragmentToLendingAddressOptionFragment(
                        flowType = args.flowType
                    ),
                    popUpTo = R.id.lendingAddAddressFragment,
                    inclusive = true
                )
            }
        }

        binding.btnSaveAddress.setDebounceClickListener {
            validate()?.let {
                args.address?.let { _editAddress ->
                    val employmentDetails = lendingViewModel.loanApplications?.applications?.getOrNull(0)?.details?.EMPLOYMENT_DETAILS
                    analyticsHandler.postEvent(
                        LendingEventKey.OnClick_ReadyCash_PersonalDetails_AddAddress_Save,
                        mapOf(
                            LendingEventKey.employmentType to employmentDetails?.employmentType.orEmpty(),
                            LendingEventKey.companyName to employmentDetails?.companyName.orEmpty(),
                            LendingEventKey.monthlyIncome to employmentDetails?.getMonthlyIncome().orZero(),
                            LendingEventKey.entryPoint to args.flowType,
                            LendingEventKey.chooseAddress to LendingEventKey.new,
                            LendingEventKey.addNewAddressVia to args.newAddressAddedVia,
                            LendingEventKey.selectedAddress to _editAddress.address.orEmpty()
                        )
                    )
                    viewModel.editAddress(it.addressId!!, it)
                } ?: kotlin.run {
                    val employmentDetails = lendingViewModel.loanApplications?.applications?.getOrNull(0)?.details?.EMPLOYMENT_DETAILS
                    analyticsHandler.postEvent(
                        LendingEventKey.OnClick_ReadyCash_PersonalDetails_AddAddress_Save,
                        mapOf(
                            LendingEventKey.employmentType to employmentDetails?.employmentType.orEmpty(),
                            LendingEventKey.companyName to employmentDetails?.companyName.orEmpty(),
                            LendingEventKey.monthlyIncome to employmentDetails?.getMonthlyIncome().orZero(),
                            LendingEventKey.entryPoint to args.flowType,
                            LendingEventKey.chooseAddress to LendingEventKey.new,
                            LendingEventKey.addNewAddressVia to args.newAddressAddedVia,
                            LendingEventKey.selectedAddress to it.address.orEmpty()
                        )
                    )
                    viewModel.addAddress(it)
                }
            }
        }

        binding.ivFlatClear.setDebounceClickListener {
            binding.etFlat.text = null
            binding.ivFlatClear.isVisible = false
        }

        binding.ivLandmarkClear.setDebounceClickListener {
            binding.etLandmark.text = null
            binding.ivLandmarkClear.isVisible = false
        }

        binding.ivStreetClear.setDebounceClickListener {
            binding.etStreet.text = null
            binding.ivStreetClear.isVisible = false
        }

        binding.etFlat.textChanges()
            .debounce(300)
            .onEach {
                binding.ivFlatClear.isVisible = !it.isNullOrEmpty()
                val isErrorState = it?.length.orZero() + binding.tvPrimaryText.text?.length.orZero() < 5
                binding.errorFlat.isVisible = isErrorState
                binding.clFlat.setBackgroundResource(
                    if (isErrorState) R.drawable.feature_lending_bg_rounded_2e2942_error_state_12dp else R.drawable.feature_lending_bg_rounded_2e2942_12dp
                )
                toggleMainButton()
            }
            .launchIn(uiScope)

        binding.etLandmark.textChanges()
            .debounce(300)
            .onEach {
                setupEditTextFilters()
                binding.ivLandmarkClear.isVisible = !it.isNullOrEmpty()
                toggleMainButton()
            }
            .launchIn(uiScope)

        binding.etStreet.textChanges()
            .debounce(300)
            .onEach {
                setupEditTextFilters()
                binding.ivStreetClear.isVisible = !it.isNullOrEmpty()
                val isErrorState = it?.length.orZero() + subAddressLine2.length.orZero() < 5
                binding.errorStreet.isVisible = isErrorState
                binding.clStreet.setBackgroundResource(
                    if (isErrorState) R.drawable.feature_lending_bg_rounded_2e2942_error_state_12dp else R.drawable.feature_lending_bg_rounded_2e2942_12dp
                )
                toggleMainButton()
            }
            .launchIn(uiScope)
    }

    //Covers edge case where geocoder is not able to fetch basic address details due to some error
    private fun validate(): com.jar.app.feature_user_api.domain.model.Address? {
        val user = serializer.decodeFromString<User?>(prefs.getUserStringSync().orEmpty())
        val address1 =
            if (args.address != null)
                binding.etFlat.text?.toString().orEmpty()
            else
                binding.etFlat.text?.toString()
                    .orEmpty() + " " + binding.tvPrimaryText.text?.toString().orEmpty()
        val address2 =
            if (args.address != null)
                binding.etStreet.text?.toString()
            else
                binding.etStreet.text?.toString()
                    .orEmpty() + " " + subAddressLine2

        val address = com.jar.app.feature_user_api.domain.model.Address(
            name = args.address?.name ?: user?.getFullName().orEmpty(),
            phoneNumber = args.address?.phoneNumber ?: (BaseConstants.DEFAULT_COUNTRY_CODE_WITH_PLUS_SIGN + user?.getPhoneNumberWithoutCountryCode()),
            pinCode = args.address?.pinCode ?: geocoderAddressItem?.postalCode.orEmpty(),
            address1 = address1,
            address2 = address2,
            city = args.address?.city ?: geocoderAddressItem?.locality.orEmpty(),
            state = args.address?.state ?: geocoderAddressItem?.adminArea.orEmpty()
                .ifEmpty { geocoderAddressItem?.subAdminArea.orEmpty() },
            latitude = args.address?.latitude ?: placeAddressItem?.latLng?.latitude.orZero().toString(),
            longitude = args.address?.longitude ?: placeAddressItem?.latLng?.longitude.orZero().toString(),
            addressId = args.address?.addressId,
            landmark = binding.etLandmark.text?.toString().orEmpty()
        )

        if (address.city.isEmpty() || address.state.isEmpty() || address.pinCode.isEmpty() || address.pinCode.length.orZero() != 6) {
            getString(com.jar.app.core_ui.R.string.something_went_wrong).snackBar(binding.root, translationY = 0f)
            val employmentDetails = lendingViewModel.loanApplications?.applications?.getOrNull(0)?.details?.EMPLOYMENT_DETAILS
            analyticsHandler.postEvent(
                LendingEventKey.Error_ReadyCash_PersonalDetails_AddAddress_Save,
                mapOf(
                    LendingEventKey.employmentType to employmentDetails?.employmentType.orEmpty(),
                    LendingEventKey.companyName to employmentDetails?.companyName.orEmpty(),
                    LendingEventKey.monthlyIncome to employmentDetails?.getMonthlyIncome().orZero(),
                    LendingEventKey.entryPoint to args.flowType,
                    LendingEventKey.chooseAddress to LendingEventKey.new,
                    LendingEventKey.addNewAddressVia to args.newAddressAddedVia,
                    LendingEventKey.selectedAddress to address.address.orEmpty()
                )
            )
            return null
        }

        return address
    }

    private fun observeFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.addressFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        navigateTo(
                            LendingAddAddressFragmentDirections.actionLendingAddAddressFragmentToLendingSelectAddressFragment(
                                isNewAddressAdded = true,
                                flowType = args.flowType,
                                newAddressAddedVia = args.newAddressAddedVia
                            ),
                            popUpTo = R.id.lendingSelectAddressFragment,
                            inclusive = true
                        )
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.editAddressFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        navigateTo(
                            LendingAddAddressFragmentDirections.actionLendingAddAddressFragmentToLendingSelectAddressFragment(
                                isNewAddressAdded = true,
                                flowType = args.flowType,
                                newAddressAddedVia = args.newAddressAddedVia
                            ),
                            popUpTo = R.id.lendingSelectAddressFragment,
                            inclusive = true
                        )
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                    }
                )
            }
        }
    }

    private fun setupUI() {
        args.address?.let {
            binding.tvPrimaryText.text = it.address1
            binding.tvSecondaryText.text = it.address
            binding.etFlat.setText(it.address1)
            binding.etStreet.setText(it.address2)
            binding.etLandmark.setText(it.landmark)
        }
        setupToolbar()
        setupEditTextFilters()
        toggleMainButton(disableAnyway = true)
    }

    private fun setupAddressBar() {
        if (args.placePrediction != null) {
            binding.tvPrimaryText.text = args.placePrediction?.primaryText.orEmpty()
            binding.tvSecondaryText.text = args.placePrediction?.secondaryText.orEmpty()
        } else {
            binding.tvPrimaryText.text = geocoderAddressItem?.featureName ?: geocoderAddressItem?.subAdminArea.orEmpty()
            binding.tvSecondaryText.text = geocoderAddressItem?.getAddressLine(0).orEmpty()
        }
    }

    private fun setupToolbar() {
        binding.toolbar.tvTitle.text = getCustomString(MR.strings.fetaure_lending_add_your_current_address)

        binding.toolbar.separator.isVisible = true
        binding.toolbar.ivEndImage.isVisible = false
        binding.toolbar.lottieView.isVisible = false

        binding.toolbar.btnBack.setDebounceClickListener {
            popBackStack()
        }

        EventBus.getDefault().post(
            UpdateLendingStepsToolbarEvent(shouldShowSteps = false, LendingStep.KYC)
        )
    }

    private fun initGooglePlaces() {
        uiScope.launch {
            Places.initialize(requireActivity().applicationContext, encryptionUtil.decrypt(BuildConfig.GOOGLE_PLACES_API_KEY_ENCRYPTED))
            placesClient = Places.createClient(requireContext())
        }
    }

    private fun toggleMainButton(disableAnyway: Boolean = false) {
        val isDisabled =
            if (disableAnyway)
                true
            else
                binding.etFlat.text?.toString().isNullOrEmpty()
                        //Since address line 2 = Street + SubAddress Line 2, which must be min 5 chars
                        || binding.etStreet.text?.length.orZero() + subAddressLine2.length.orZero() < 5
                        //Since address line 1 = Flat + Primary Address, which must be min 5 chars
                        || binding.etFlat.text?.length.orZero() + binding.tvPrimaryText.text?.length.orZero() < 5
        binding.btnSaveAddress.setDisabled(isDisabled = isDisabled)
    }

    private fun setupEditTextFilters() {
        //Considering max length of address1 and address2 as 250 instead of 256 since we add space, comma, etc. when we concat fields
        //The remaining 6 may act as buffer
        binding.etFlat.filters = arrayOf(InputFilter.LengthFilter(250 - binding.tvPrimaryText.text?.length.orZero()))
        //Total char limit for address line 2 is 256 and address line 2 is sum of Street + SubAddressLine2
        //Hence, Setting the max length as 250 minus size occupied by the other field
        binding.etStreet.filters = arrayOf(InputFilter.LengthFilter(250 - subAddressLine2.length.orZero()))
        binding.etLandmark.filters = arrayOf(InputFilter.LengthFilter(250))
    }

    private fun showLoadingView() {
        binding.lottieViewLoading.clearAnimation()
        binding.lottieViewLoading.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            LendingConstants.LottieUrls.FETCH_LOCATION
        )
        binding.llLoading.isVisible = true
    }

    private fun dismissLoadingView(isError: Boolean = false) {
        binding.lottieViewLoading.clearAnimation()
        if (!isError) {
            binding.llLoading.isVisible = false
            binding.llLoading.slideToRevealNew(binding.clContent)
        } else {
            popBackStack()
        }
    }
}