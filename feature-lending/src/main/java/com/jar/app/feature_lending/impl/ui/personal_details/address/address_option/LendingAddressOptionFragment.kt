package com.jar.app.feature_lending.impl.ui.personal_details.address.address_option

import android.Manifest
import android.content.Context
import android.content.res.ColorStateList
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.textChanges
import com.jar.app.core_base.util.EncryptionUtil
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_lending.BuildConfig
import com.jar.app.feature_lending.R
import com.jar.app.feature_lending.databinding.FragmentLendingAddressOptionBinding
import com.jar.app.feature_lending.impl.domain.event.UpdateLendingStepsToolbarEvent
import com.jar.app.feature_lending.shared.domain.model.PlacesPrediction
import com.jar.app.feature_lending.impl.ui.common.LendingViewModel
import com.jar.app.feature_lending.shared.ui.step_view.LendingStep
import com.jar.app.feature_lending.shared.domain.LendingEventKey
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnNeverAskAgain
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions
import javax.inject.Inject
import com.jar.app.feature_lending.shared.MR

@AndroidEntryPoint
@RuntimePermissions
class LendingAddressOptionFragment : BaseFragment<FragmentLendingAddressOptionBinding>(), LocationListener {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var encryptionUtil: EncryptionUtil

    private val lendingViewModel by activityViewModels<LendingViewModel> { defaultViewModelProviderFactory }

    private val args by navArgs<LendingAddressOptionFragmentArgs>()

    private var adapter: AutoCompletePlacesAdapter? = null

    private var autoCompleteToken: AutocompleteSessionToken? = null

    private var placesClient: PlacesClient? = null

    private val autoCompleteList = mutableListOf<PlacesPrediction>()

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLendingAddressOptionBinding
        get() = FragmentLendingAddressOptionBinding::inflate

    private lateinit var locationManager: LocationManager

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
        setupUI()
        initClickListeners()
    }

    private fun setupUI() {
        setupToolbar()
        ContextCompat.getDrawable(
            requireContext(), R.drawable.feature_lending_ic_locate_me
        )?.let {
            binding.btnLocateMe.setCompoundDrawablesRelativeWithIntrinsicBounds(icon = it)
        }
        adapter = AutoCompletePlacesAdapter(
            requireContext(),
            autoCompleteList,
            onPlaceSelected = {
                val employmentDetails = lendingViewModel.loanApplications?.applications?.getOrNull(0)?.details?.EMPLOYMENT_DETAILS
                analyticsHandler.postEvent(
                    LendingEventKey.OnClick_ReadyCash_PersonalDetails_AddAddressOptions_Via,
                    mapOf(
                        LendingEventKey.employmentType to employmentDetails?.employmentType.orEmpty(),
                        LendingEventKey.companyName to employmentDetails?.companyName.orEmpty(),
                        LendingEventKey.monthlyIncome to employmentDetails?.getMonthlyIncome().orZero(),
                        LendingEventKey.entryPoint to args.flowType,
                        LendingEventKey.type to LendingEventKey.search
                    )
                )
                binding.autoCompleteSearch.text = null
                binding.autoCompleteSearch.clearFocus()
                navigateTo(
                    LendingAddressOptionFragmentDirections.actionLendingAddressOptionFragmentToLendingAddAddressFragment(
                        address = null, placePrediction = it, null, null, flowType = args.flowType, newAddressAddedVia = LendingEventKey.search
                    )
                )
            }
        )
        binding.autoCompleteSearch.setAdapter(adapter)
        binding.autoCompleteSearch.setDropDownBackgroundDrawable(
            ContextCompat.getDrawable(
                requireContext(), com.jar.app.core_ui.R.drawable.rounded_bg_3c3357_12dp
            )
        )
    }

    private fun setupToolbar() {
        binding.toolbar.tvTitle.text = getCustomString(MR.strings.fetaure_lending_add_your_current_address)

        binding.toolbar.separator.isVisible = true
        binding.toolbar.ivEndImage.isVisible = false
        binding.toolbar.lottieView.isVisible = false

        binding.toolbar.btnBack.setDebounceClickListener {
            val employmentDetails = lendingViewModel.loanApplications?.applications?.getOrNull(0)?.details?.EMPLOYMENT_DETAILS
            analyticsHandler.postEvent(
                LendingEventKey.OnClick_ReadyCash_PersonalDetails_AddAddressOptions_Back,
                mapOf(
                    LendingEventKey.employmentType to employmentDetails?.employmentType.orEmpty(),
                    LendingEventKey.companyName to employmentDetails?.companyName.orEmpty(),
                    LendingEventKey.monthlyIncome to employmentDetails?.getMonthlyIncome().orZero(),
                    LendingEventKey.entryPoint to args.flowType
                )
            )
            popBackStack()
        }

        EventBus.getDefault().post(
            UpdateLendingStepsToolbarEvent(shouldShowSteps = false, LendingStep.KYC)
        )
    }

    private fun initClickListeners() {
        binding.ivSearchClear.setDebounceClickListener {
            binding.autoCompleteSearch.text = null
            binding.ivSearchClear.isVisible = false
        }

        binding.autoCompleteSearch.textChanges()
            .debounce(500)
            .onEach {
                binding.ivSearchClear.isVisible = !it.isNullOrEmpty()
                if (it.isNullOrEmpty()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        binding.autoCompleteSearch.compoundDrawableTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                requireContext(), com.jar.app.core_ui.R.color.color_776E94
                            )
                        )
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        binding.autoCompleteSearch.compoundDrawableTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                requireContext(), com.jar.app.core_ui.R.color.color_EEEAFF
                            )
                        )
                    }
                }
                if (it?.toString().orEmpty().trim().length.orZero() >= 3) {
                    searchPlace(it.toString())
                }
            }
            .launchIn(uiScope)

        binding.btnLocateMe.setDebounceClickListener {
            val employmentDetails = lendingViewModel.loanApplications?.applications?.getOrNull(0)?.details?.EMPLOYMENT_DETAILS
            analyticsHandler.postEvent(
                LendingEventKey.OnClick_ReadyCash_PersonalDetails_AddAddressOptions_Via,
                mapOf(
                    LendingEventKey.employmentType to employmentDetails?.employmentType.orEmpty(),
                    LendingEventKey.companyName to employmentDetails?.companyName.orEmpty(),
                    LendingEventKey.monthlyIncome to employmentDetails?.getMonthlyIncome().orZero(),
                    LendingEventKey.entryPoint to args.flowType,
                    LendingEventKey.type to LendingEventKey.locate
                )
            )
            getLatitudeAndLongitudeWithPermissionCheck()
        }
    }

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    fun getLatitudeAndLongitude() {
        showProgressBar()
        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)

            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            dismissProgressBar()
            navigateToAddAddressWithLatLong(location?.latitude.orZero().toString(), location?.longitude.orZero().toString())
        } else {
            dismissProgressBar()
            getCustomString(MR.strings.feature_lending_please_enable_gps).snackBar(binding.root, translationY = 0f)
        }
    }

    @OnPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    fun onLocationPermissionDenied() {
        getCustomString(MR.strings.feature_lending_permisison_required).snackBar(binding.root)
        val employmentDetails = lendingViewModel.loanApplications?.applications?.getOrNull(0)?.details?.EMPLOYMENT_DETAILS
        analyticsHandler.postEvent(
            LendingEventKey.Fetching_ReadyCash_PersonalDetails_AddAddress,
            mapOf(
                LendingEventKey.employmentType to employmentDetails?.employmentType.orEmpty(),
                LendingEventKey.companyName to employmentDetails?.companyName.orEmpty(),
                LendingEventKey.monthlyIncome to employmentDetails?.getMonthlyIncome().orZero(),
                LendingEventKey.entryPoint to args.flowType,
                LendingEventKey.chooseAddress to LendingEventKey.new,
                LendingEventKey.addNewAddressVia to LendingEventKey.locate,
                LendingEventKey.permission to LendingEventKey.denied
            )
        )
    }

    @OnNeverAskAgain(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    fun onNeverAskLocationPermission() {
        getCustomString(MR.strings.feature_lending_permisison_required).snackBar(binding.root)
        val employmentDetails = lendingViewModel.loanApplications?.applications?.getOrNull(0)?.details?.EMPLOYMENT_DETAILS
        analyticsHandler.postEvent(
            LendingEventKey.Fetching_ReadyCash_PersonalDetails_AddAddress,
            mapOf(
                LendingEventKey.employmentType to employmentDetails?.employmentType.orEmpty(),
                LendingEventKey.companyName to employmentDetails?.companyName.orEmpty(),
                LendingEventKey.monthlyIncome to employmentDetails?.getMonthlyIncome().orZero(),
                LendingEventKey.entryPoint to args.flowType,
                LendingEventKey.chooseAddress to LendingEventKey.new,
                LendingEventKey.addNewAddressVia to LendingEventKey.locate,
                LendingEventKey.permission to LendingEventKey.denied
            )
        )
    }

    private fun navigateToAddAddressWithLatLong(lat: String, long: String) {
        val employmentDetails = lendingViewModel.loanApplications?.applications?.getOrNull(0)?.details?.EMPLOYMENT_DETAILS
        analyticsHandler.postEvent(
            LendingEventKey.Fetching_ReadyCash_PersonalDetails_AddAddress,
            mapOf(
                LendingEventKey.employmentType to employmentDetails?.employmentType.orEmpty(),
                LendingEventKey.companyName to employmentDetails?.companyName.orEmpty(),
                LendingEventKey.monthlyIncome to employmentDetails?.getMonthlyIncome().orZero(),
                LendingEventKey.entryPoint to args.flowType,
                LendingEventKey.chooseAddress to LendingEventKey.new,
                LendingEventKey.addNewAddressVia to LendingEventKey.locate,
                LendingEventKey.permission to LendingEventKey.allow
            )
        )
        navigateTo(
            LendingAddressOptionFragmentDirections.actionLendingAddressOptionFragmentToLendingAddAddressFragment(
                address = null, placePrediction = null, lat = lat, long = long, flowType = args.flowType, newAddressAddedVia = LendingEventKey.locate
            )
        )
    }

    private fun initGooglePlaces() {
        uiScope.launch {
            Places.initialize(requireActivity().applicationContext, encryptionUtil.decrypt(BuildConfig.GOOGLE_PLACES_API_KEY_ENCRYPTED))
            placesClient = Places.createClient(requireContext())
            autoCompleteToken = AutocompleteSessionToken.newInstance()
        }
    }

    private fun searchPlace(input: String) {
        val request = getPlacesRequest(input)
        placesClient?.let {
            it.findAutocompletePredictions(request)
                .addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
                    autoCompleteList.clear()
                    if (response.autocompletePredictions.isNullOrEmpty()) {
                        autoCompleteList.add(
                            PlacesPrediction(
                                placeId = "",
                                primaryText = "",
                                secondaryText = "",
                                fullText = "",
                                showPlaceNotFound = true
                            )
                        )
                        autoCompleteList.add(
                            PlacesPrediction(
                                placeId = "",
                                primaryText = "",
                                secondaryText = "",
                                fullText = "",
                                showGoogleAttribution = true
                            )
                        )
                    } else {
                        for (i in 0 until response.autocompletePredictions.size) {
                            if (i < 3) {
                                val prediction = response.autocompletePredictions[i]
                                val placesPrediction = PlacesPrediction(
                                    placeId = prediction.placeId.orEmpty(),
                                    primaryText = prediction?.getPrimaryText(null)?.toString()
                                        .orEmpty(),
                                    secondaryText = prediction?.getSecondaryText(null)?.toString()
                                        .orEmpty(),
                                    fullText = prediction?.getFullText(null)?.toString().orEmpty(),
                                )
                                autoCompleteList.add(placesPrediction)
                            } else {
                                break
                            }
                        }
                        autoCompleteList.add(
                            PlacesPrediction(
                                placeId = "",
                                primaryText = "",
                                secondaryText = "",
                                fullText = "",
                                showGoogleAttribution = true
                            )
                        )
                    }
                    adapter?.notifyDataSetChanged()
                }.addOnFailureListener {
                    autoCompleteList.clear()
                    autoCompleteList.add(
                        PlacesPrediction(
                            placeId = "",
                            primaryText = "",
                            secondaryText = "",
                            fullText = "",
                            showPlaceNotFound = true
                        )
                    )
                    autoCompleteList.add(
                        PlacesPrediction(
                            placeId = "",
                            primaryText = "",
                            secondaryText = "",
                            fullText = "",
                            showGoogleAttribution = true
                        )
                    )
                    adapter?.notifyDataSetChanged()
                    getString(com.jar.app.core_ui.R.string.something_went_wrong).snackBar(binding.root, translationY = 0f)
                }
        }
    }

    private fun getPlacesRequest(query: String): FindAutocompletePredictionsRequest {
        return FindAutocompletePredictionsRequest.builder()
            .setCountries("IN")
            .setTypeFilter(TypeFilter.REGIONS)
            .setSessionToken(autoCompleteToken)
            .setQuery(query)
            .build()
    }

    override fun onLocationChanged(p0: Location) {}

    override fun onDestroy() {
        super.onDestroy()
        if (this::locationManager.isInitialized) {
            locationManager.removeUpdates(this)
        }
    }
}