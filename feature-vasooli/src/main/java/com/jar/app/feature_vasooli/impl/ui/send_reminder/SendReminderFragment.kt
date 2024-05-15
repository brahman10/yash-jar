package com.jar.app.feature_vasooli.impl.ui.send_reminder

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.drawToBitmap
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.jar.app.base.ui.fragment.BaseBottomSheetDialogFragment
import com.jar.app.base.util.*
import com.jar.app.core_base.util.BaseConstants
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_utils.data.FileUtils
import com.jar.app.feature_vasooli.R
import com.jar.app.feature_vasooli.databinding.FragmentSendReminderBinding
import com.jar.app.feature_vasooli.impl.domain.VasooliEventKey
import com.jar.app.feature_vasooli.impl.domain.event.ReminderSentEvent
import com.jar.app.feature_vasooli.impl.domain.model.SendReminderRequest
import com.jar.app.feature_vasooli.impl.util.VasooliConstants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class SendReminderFragment : BaseBottomSheetDialogFragment<FragmentSendReminderBinding>() {

    companion object {
        const val MAX_LINES_COLLAPSE = 4
        const val MAX_LINES_DEFAULT = 100
    }

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    @Inject
    lateinit var fileUtils: FileUtils

    private val viewModel by viewModels<SendReminderViewModel> { defaultViewModelProviderFactory }

    private val args by navArgs<SendReminderFragmentArgs>()

    private var newImageIndex: String? = null

    private var newImageUrl: String? = null

    private var isExpanded = true

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSendReminderBinding
        get() = FragmentSendReminderBinding::inflate

    override val bottomSheetConfig: BottomSheetConfig
        get() = BottomSheetConfig()

    override fun setup() {
        setupUI()
        initClickListeners()
        observeLiveData()
    }

    private fun setupUI() {
        loadImage(args.reminder.imageUrl)

        toggleShareText()
        binding.tvMessage.text = args.reminder.reminderText
    }

    private fun loadImage(url: String) {
        newImageUrl = url
        Glide.with(requireContext())
            .asBitmap()
            .load(url)
            .transform(RoundedCorners(10.dp))
            .into(binding.ivImage)
    }


    private fun initClickListeners() {
        binding.expandableLayout.setDebounceClickListener {
            toggleShareText()
        }

        binding.tvExpand.setDebounceClickListener {
            toggleShareText()
        }

        binding.tvChangeImage.setDebounceClickListener {
            analyticsHandler.postEvent(
                VasooliEventKey.VasooliReminder.Clicked_SendReminderScreen_Vasooli,
                mapOf(
                    VasooliEventKey.Button to VasooliEventKey.ChangeImage
                )
            )
            viewModel.fetchNewImage(newImageIndex ?: args.reminder.imageId)
        }

        binding.tvCancel.setDebounceClickListener {
            analyticsHandler.postEvent(
                VasooliEventKey.VasooliReminder.Clicked_SendReminderScreen_Vasooli,
                mapOf(
                    VasooliEventKey.Button to VasooliEventKey.Cancel
                )
            )
            dismissAllowingStateLoss()
        }

        binding.btnAction.setDebounceClickListener {
            analyticsHandler.postEvent(
                VasooliEventKey.VasooliReminder.Clicked_SendReminderScreen_Vasooli,
                mapOf(
                    VasooliEventKey.Image to newImageUrl.orEmpty(),
                    VasooliEventKey.Button to VasooliEventKey.Send,
                    VasooliEventKey.Medium to if (args.reminder.showShareTray.orTrue()) VasooliConstants.SELF else VasooliConstants.JAR
                )
            )
            if (args.reminder.showShareTray.orTrue()) {
                shareContent()
            } else {
                val sendReminderRequest = SendReminderRequest(
                    loanId = args.loanId,
                    imageIndex = newImageIndex ?: args.reminder.imageId
                )
                viewModel.sendReminder(sendReminderRequest)
            }
        }
    }

    private fun shareContent() {
        binding.ivImage.post {
            val bitmap = binding.ivImage.drawToBitmap()
            uiScope.launch(Dispatchers.IO) {
                bitmap.let {
                    val parent =
                        File(requireContext().externalCacheDir, BaseConstants.CACHE_DIR_SHARED)
                    parent.mkdirs()
                    val file = File(parent, "vasooli.png")
                    file.writeBitmap(bitmap, Bitmap.CompressFormat.PNG, 90)
                    withContext(Dispatchers.Main) {
                        fileUtils.shareImage(
                            requireContext(),
                            file,
                            args.reminder.reminderText.orEmpty()
                        )
                        dismissAllowingStateLoss()
                    }
                }
            }
        }
    }

    private fun observeLiveData() {
        val weakReference: WeakReference<View> = WeakReference(getRootView())

        viewModel.newImageLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            weakReference,
            onLoading = {
                showProgressBar()
            },
            onSuccess = {
                dismissProgressBar()
                newImageIndex = it.imageId
                loadImage(it.imageUrl)
            },
            onError = {
                dismissProgressBar()
            }
        )

        viewModel.sendReminderLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            weakReference,
            onLoading = {
                showProgressBar()
            },
            onSuccessWithNullData = {
                dismissProgressBar()
                EventBus.getDefault().post(ReminderSentEvent())
                dismissAllowingStateLoss()
            },
            onError = {
                dismissProgressBar()
            }
        )
    }

    private fun toggleShareText() {
        if (isExpanded) {
            collapseText()
        } else {
            expandText()
        }
        isExpanded = !isExpanded
    }

    private fun expandText() {
        binding.tvMessage.maxLines = MAX_LINES_DEFAULT
        binding.tvExpand.text = getString(R.string.feature_vasooli_read_less)
    }

    private fun collapseText() {
        binding.tvMessage.maxLines = MAX_LINES_COLLAPSE
        binding.tvExpand.text = getString(R.string.feature_vasooli_read_more)
    }
}