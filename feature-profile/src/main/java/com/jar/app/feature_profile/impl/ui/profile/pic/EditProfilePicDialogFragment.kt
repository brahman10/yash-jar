package com.jar.app.feature_profile.impl.ui.profile.pic

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.whenResumed
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.signature.ObjectKey
import com.jar.app.base.data.livedata.SharedPreferencesUserLiveData
import com.jar.app.base.ui.BaseResources
import com.jar.app.base.ui.fragment.BaseDialogFragment
import com.jar.app.base.util.*
import com.jar.app.core_base.domain.model.User
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.core_preferences.api.PrefsApi
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.slideToRevealNew
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.widget.carousal_layout_manager.CarouselLayoutManager
import com.jar.app.core_ui.widget.carousal_layout_manager.CenterSnapHelper
import com.jar.app.feature_profile.R
import com.jar.app.feature_profile.databinding.FragmentDialogEditProfilePicBinding
import com.jar.app.feature_profile.domain.ProfileEventKey
import com.jar.app.feature_profile.domain.model.AvatarInfo
import com.jar.app.feature_profile.shared.MR
import com.jar.app.feature_profile.ui.EditProfilePicViewModel
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.internal.library.jar_core_network.api.util.collect
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.ref.WeakReference
import javax.inject.Inject

//TODO: REFACTOR CLASS
@AndroidEntryPoint
class EditProfilePicDialogFragment : BaseDialogFragment<FragmentDialogEditProfilePicBinding>(),
    BaseResources {

    @Inject
    lateinit var userLiveData: SharedPreferencesUserLiveData

    @Inject
    lateinit var prefs: PrefsApi

    @Inject
    lateinit var serializer: Serializer

    @Inject
    lateinit var imageCompressionUtil: ImageCompressionUtil

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val viewModelProvider by viewModels<EditProfilePicViewModelAndroid> { defaultViewModelProviderFactory }

    private val viewModel by lazy {
        viewModelProvider.getInstance()
    }

    private val args by navArgs<EditProfilePicDialogFragmentArgs>()

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentDialogEditProfilePicBinding
        get() = FragmentDialogEditProfilePicBinding::inflate

    override val dialogConfig: DialogFragmentConfig
        get() = DialogFragmentConfig(isCancellable = false)

    private var adapter: ProfilePicAdapter? = null

    private var layoutManager: CarouselLayoutManager? = null

    private val galleryPickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            it?.let { updateImageUri(it) }
        }

    private var cameraResultLauncher: ActivityResultLauncher<Uri>? = null

    private var cameraUri: Uri? = null

    private var imageBitmap: Bitmap? = null

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            openCamera()
        } else {
            Toast.makeText(
                requireContext(),
                PROVIDE_PERMISSION_ERROR,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val PROVIDE_PERMISSION_ERROR = "Please provide camera permission!!"
    }

    override fun setup() {
        setupUI()
        initClickListeners()
        observeLiveData()
        analyticsHandler.postEvent(
            ProfileEventKey.Events.Shown_ProfilePicture_ProfilePicturePopUp,
            mapOf(ProfileEventKey.Props.FromScreen to args.fromScreen)
        )
    }

    private fun setupUI() {
        binding.editProfileSuccessLayout.animTick.playLottieWithUrlAndExceptionHandling(
            requireContext(),
            "${BaseConstants.CDN_BASE_URL}/LottieFiles/Generic/tick.json"
        )
        layoutManager =
            CarouselLayoutManager(
                requireContext(),
                10.dp,
                RecyclerView.HORIZONTAL,
                false
            )
        binding.rvProfilePic.layoutManager = layoutManager
        val snapHelper =
            CenterSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvProfilePic)
    }

    private fun getProfilePicData() {
        //viewModel.fetchProfileList(userLiveData.value?.gender)
        val avatarList: ArrayList<AvatarInfo> = ArrayList()
        avatarList.add(
            AvatarInfo(
                image = "",
                default = false,
                resourceId = R.drawable.feature_profile_ic_upload,
                imageBitmap = null
            )
        )
        avatarList.add(
            AvatarInfo(
                image = getUserImageLinkIfNotAvatar().orEmpty(),
                default = false,
                resourceId = 0,
                imageBitmap = null
            )
        )

        adapter?.submitList(avatarList)
        preSelectExistingImage()
    }

    private fun initClickListeners() {
        binding.tvCancel.setDebounceClickListener {
            dismiss()
            analyticsHandler.postEvent(ProfileEventKey.Events.Clicked_Cancel_ProfilePicturePopUp)
        }
        binding.btnSave.setDebounceClickListener {
            if (imageBitmap != null) {
                val outputStream = ByteArrayOutputStream()
                imageBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                val byteArray = outputStream.toByteArray()
                viewModel.updateUserProfilePic(byteArray)
            } else {
                adapter?.currentList?.getOrNull(layoutManager?.currentPosition ?: -1)?.let {
                    viewModel.updateUserImage(it.image)
                } ?: kotlin.run {
                    getCustomString(MR.strings.feature_profile_please_select_profile_pic).snackBar(
                        binding.root
                    )
                }
            }
            analyticsHandler.postEvent(ProfileEventKey.Events.Clicked_Save_ProfilePicturePopUp)
        }
        binding.rvProfilePic.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                binding.rvProfilePic.post {
                    selectMiddleItem()
                }
            }
        })
        binding.tvChooseFromGallery.setDebounceClickListener {
            openGallery()
            analyticsHandler.postEvent(ProfileEventKey.Events.Clicked_ChooseGallery_UploadPicturePopUp)
        }
        binding.tvTakeSelfie.setDebounceClickListener {
            permissionLauncher.launch(REQUIRED_PERMISSIONS)
            analyticsHandler.postEvent(ProfileEventKey.Events.Clicked_TakeSelfie_UploadPicturePopUp)
        }
    }

    private fun observeLiveData() {
        val viewRef: WeakReference<View> = WeakReference(binding.root)

        userLiveData.observe(viewLifecycleOwner) {
            adapter = ProfilePicAdapter(
                user = it,
                onChangeClicked = {
                    if (it == 0) {
                        toggleUploadLayout(true)
                    } else {
                        binding.rvProfilePic.smoothScrollToPosition(0)
                    }
                },
                onUploadClicked = {
                    permissionLauncher.launch(REQUIRED_PERMISSIONS)
                }
            )
            binding.rvProfilePic.adapter = adapter
            getProfilePicData()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.profileListLiveData.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        val avatarList: ArrayList<AvatarInfo> = ArrayList()
                        avatarList.add(
                            AvatarInfo(
                                image = getUserImageLinkIfNotAvatar().orEmpty(),
                                default = false,
                                resourceId = R.drawable.feature_profile_ic_upload,
                                imageBitmap = null
                            )
                        )
                        avatarList.addAll(it)
                        adapter?.submitList(avatarList)
                        preSelectExistingImage()
                    },
                    onError = {errorMessage, errorCode ->
                        dismissProgressBar()
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.networkUserLiveData.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        adapter = ProfilePicAdapter(
                            user = it,
                            onChangeClicked = {
                                if (it == 0) {
                                    toggleUploadLayout(true)
                                } else {
                                    binding.rvProfilePic.smoothScrollToPosition(0)
                                }
                            },
                            onUploadClicked = {
                                permissionLauncher.launch(REQUIRED_PERMISSIONS)
                            }
                        )
                        binding.rvProfilePic.adapter = adapter
                        getProfilePicData()

                        dismissProgressBar()
                        binding.editProfileSuccessLayout.tvSuccessDes.text =
                            getCustomString(MR.strings.feature_profile_looking_good_profile_picture_updated)
                        binding.cvProfilePicContainer.slideToRevealNew(
                            viewToReveal = binding.editProfileSuccessLayout.root,
                            onAnimationEnd = {
                                binding.editProfileSuccessLayout.animTick.playAnimation()
                                uiScope.launch {
                                    analyticsHandler.postEvent(ProfileEventKey.Events.Shown_Success_ProfilePicturePopUp)
                                    delay(3000)
                                    dismissAllowingStateLoss()
                                }
                            }
                        )
                    },
                    onError = { errorMessage, errorCode ->
                        dismissProgressBar()
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.updatePhotoLiveData.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        viewModel.updateUserPicLocally(it.profilePicUrl)
                        binding.editProfileSuccessLayout.tvSuccessDes.text =
                            getCustomString(MR.strings.feature_profile_pic_updated_successfully)
                        binding.cvProfilePicContainer.slideToRevealNew(
                            viewToReveal = binding.editProfileSuccessLayout.root,
                            onAnimationEnd = {
                                binding.editProfileSuccessLayout.animTick.playAnimation()
                                uiScope.launch {
                                    analyticsHandler.postEvent(ProfileEventKey.Events.Shown_Success_ProfilePicturePopUp)
                                    delay(3000)
                                    dismissAllowingStateLoss()
                                }
                            }
                        )
                    },
                    onError = { errorMessage, errorCode ->
                        dismissProgressBar()
                        errorMessage.snackBar(binding.root)
                    }
                )
            }
        }


    }

    private fun getUserImageLinkIfNotAvatar(): String? {
        return prefs.getUserStringSync()?.let {
            serializer.decodeFromString<User>(it).profilePicUrl
        } ?: run { null }
    }

    private fun preSelectExistingImage() {
        prefs.getUserStringSync()?.let { userString ->
            val user = serializer.decodeFromString<User>(userString)
            adapter?.currentList?.indexOfFirst { it.image == user.profilePicUrl }?.let {
                uiScope.launch {
                    whenResumed {
                        binding.rvProfilePic.post {
                            binding.rvProfilePic.smoothScrollToPosition(it)
                        }
                    }
                }
            }
        }
    }

    private fun toggleSaveButton() {
        adapter?.currentList?.getOrNull(layoutManager?.currentPosition ?: -1)?.let {
            prefs.getUserStringSync()?.let { userString ->
                val user = serializer.decodeFromString<User>(userString)
                if (it.image == user.profilePicUrl && it.imageBitmap == null) {
                    binding.btnSave.isEnabled = false
                } else {
                    binding.btnSave.isEnabled = true
                }
            }
        } ?: kotlin.run {
            binding.btnSave.isEnabled = true
        }
    }

    private fun selectMiddleItem() {
        //Added state check as it is called from View.post{}, sometimes binding is null while it is being executed
        if (isBindingInitialized() && lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            //Added this to prevent recycler view from resizing on scroll as the selected image will have the largest height
            binding.rvProfilePic.minimumHeight = binding.rvProfilePic.height

            binding.rvProfilePic.forEachVisibleHolder<ProfilePicAdapter.ProfilePicViewHolder> {
                it.setSelected(
                    it.bindingAdapterPosition == layoutManager?.currentPosition,
                    imageBitmap
                )
                toggleUploadLayout(
                    layoutManager?.currentPosition == 0 && adapter?.currentList?.getOrNull(
                        0
                    )?.image.isNullOrEmpty() && adapter?.currentList?.getOrNull(
                        0
                    )?.imageBitmap == null
                )
                toggleSaveButton()
            }
        }
    }

    private fun toggleUploadLayout(showUploadOptions: Boolean) {
        if (showUploadOptions) {
            binding.btnSave.isVisible = false
            binding.tvTakeSelfie.isVisible = true
            binding.tvChooseFromGallery.isVisible = true
        } else {
            binding.btnSave.isVisible = true
            binding.tvTakeSelfie.isVisible = false
            binding.tvChooseFromGallery.isVisible = false
        }
    }

    private fun openGallery() {
        galleryPickerLauncher.launch("image/*")
    }

    private fun openCamera() {
        try {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                cameraIntent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1)
            } else {
                cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 1)
            }
            cameraResultLauncher?.launch(cameraUri)
        } catch (exception: Exception) {

        }
    }

    private fun updateImageUri(uri: Uri) {
        showProgressBar()
        Glide.with(requireContext())
            .asBitmap()
            .load(uri)
            .signature(ObjectKey(System.currentTimeMillis().toString()))
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    uiScope.launch(Dispatchers.IO) {
                        val parent =
                            File(
                                requireContext().externalCacheDir,
                                BaseConstants.COMPRESSED_DIR
                            )
                        parent.mkdirs()
                        val file = File(parent, "user_profile_pic.png")
                        file.writeBitmap(resource, Bitmap.CompressFormat.PNG, 100)
                        imageBitmap = resource
                        imageBitmap = imageCompressionUtil.compressImage(file)
                        withContext(Dispatchers.Main) {
                            adapter?.currentList?.getOrNull(0)?.let {
                                it.imageBitmap = imageBitmap?.let { bitmap ->
                                    BitmapUtils().bitmapToByteArray(
                                        bitmap
                                    )
                                }
                            }
                            binding.rvProfilePic.forEachVisibleHolder<ProfilePicAdapter.ProfilePicViewHolder> {
                                it.setUploadImage(imageBitmap)
                            }
                            toggleUploadLayout(false)
                            toggleSaveButton()
                            dismissProgressBar()
                        }
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }

    private fun registerForActivityResults() {
        val parent = File(requireContext().externalCacheDir, BaseConstants.CACHE_DIR_SHARED)
        parent.mkdirs()
        val file = File(parent, "cameraProfile.png")
        val authority = "${context?.packageName}${BaseConstants.FILE_PROVIDER_AUTHORITY}"
        cameraUri = FileProvider.getUriForFile(
            requireContext(),
            authority,
            file
        )
        cameraResultLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { success: Boolean ->
                if (success && cameraUri != null) {
                    updateImageUri(cameraUri!!)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerForActivityResults()
    }

    override fun onDestroyView() {
        binding.rvProfilePic.onFlingListener = null
        super.onDestroyView()
    }
}