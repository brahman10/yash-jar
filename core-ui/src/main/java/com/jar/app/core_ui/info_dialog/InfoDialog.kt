package com.jar.app.core_ui.info_dialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.jar.app.base.ui.fragment.BaseDialogFragment
import com.jar.internal.library.jar_core_network.api.util.Serializer
import com.jar.app.base.util.decodeUrl
import com.jar.app.core_base.util.orZero
import com.jar.app.core_ui.databinding.CoreUiDialogInfoBinding
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_base.domain.model.InfoDialogData
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class InfoDialog : BaseDialogFragment<CoreUiDialogInfoBinding>() {

    @Inject
    lateinit var serializer: Serializer

    private val args by navArgs<InfoDialogArgs>()

    private var infoDialogData: InfoDialogData? = null

    private var adapter: InfoPageAdapter? = null

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> CoreUiDialogInfoBinding
        get() = CoreUiDialogInfoBinding::inflate

    override val dialogConfig: DialogFragmentConfig
        get() = DialogFragmentConfig(isCancellable = true)

    override fun setup() {
        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        val encoded = args.infoDialogData
        infoDialogData = serializer.decodeFromString(decodeUrl(encoded))

        binding.tvHeader.text = infoDialogData?.title
        binding.tvHeader.isVisible = infoDialogData?.title.isNullOrBlank().not()

        Glide.with(this).load(infoDialogData?.icon).into(binding.ivIcon)

        adapter =
            InfoPageAdapter(
                childFragmentManager,
                viewLifecycleOwner.lifecycle,
                infoDialogData?.infoPages.orEmpty()
            )
        binding.viewPager.adapter = adapter
        binding.viewPager.offscreenPageLimit = infoDialogData?.infoPages?.size ?: 1

        if (infoDialogData?.infoPages?.size.orZero() > 1)
            TabLayoutMediator(binding.indicator, binding.viewPager) { _, _ -> }.attach()
    }

    private fun setupListeners() {
        binding.btnClose.setDebounceClickListener {
            dismissAllowingStateLoss()
        }

        binding.root.setDebounceClickListener {
            dismissAllowingStateLoss()
        }

        binding.viewPager.setPageTransformer { page, position ->
            updatePagerHeightForChild(page, binding.viewPager)
        }
    }

    private fun updatePagerHeightForChild(view: View, pager: ViewPager2) {
        view.post {
            val wMeasureSpec =
                View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY)
            val hMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            view.measure(wMeasureSpec, hMeasureSpec)
            pager.layoutParams = (pager.layoutParams).also { lp -> lp.height = view.measuredHeight }
            pager.invalidate()
        }
    }
}