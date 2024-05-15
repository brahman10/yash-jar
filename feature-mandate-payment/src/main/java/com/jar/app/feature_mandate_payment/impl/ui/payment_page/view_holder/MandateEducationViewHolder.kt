package com.jar.app.feature_mandate_payment.impl.ui.payment_page.view_holder

import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.view_holder.BaseViewHolder
import com.jar.app.feature_mandate_payment.databinding.FeatureMandatePaymentCellPaymentPageMandateEducationBinding
import com.jar.app.feature_mandate_payments_common.shared.domain.model.payment_page.MandateEducationPageItem
import com.jar.app.feature_mandate_payment.impl.ui.payment_page.view_holder.description.EducationItemAdapter

internal class MandateEducationViewHolder(
    private val binding: FeatureMandatePaymentCellPaymentPageMandateEducationBinding,
    private val onVideoClicked: (videoUrl: String) -> Unit
) : BaseViewHolder(binding.root) {

    private var adapter: EducationItemAdapter? = null

    private var mandateEducationPageItem: MandateEducationPageItem? = null

    init {
        binding.tvTitle.setOnClickListener {
            binding.elMandateDetails.isExpanded = !binding.elMandateDetails.isExpanded
            binding.ivExpand.animate()
                .rotation(if (binding.elMandateDetails.isExpanded) 180f else 0f).start()
        }
        binding.ivExpand.setOnClickListener {
            binding.elMandateDetails.isExpanded = !binding.elMandateDetails.isExpanded
            binding.ivExpand.animate()
                .rotation(if (binding.elMandateDetails.isExpanded) 180f else 0f).start()
        }
        binding.btnWatchVideo.setDebounceClickListener {
            mandateEducationPageItem?.videoUrl?.let { onVideoClicked.invoke(it) }
        }
    }

    fun setMandateEducationDetails(mandateEducationPageItem: MandateEducationPageItem) {
        this.mandateEducationPageItem = mandateEducationPageItem
        binding.elMandateDetails.isExpanded = mandateEducationPageItem.isExpanded
        binding.tvTitle.text = mandateEducationPageItem.header
        binding.tvWatchAVideoInstead.isVisible =
            mandateEducationPageItem.videoUrl != null
        binding.btnWatchVideo.isVisible =
            mandateEducationPageItem.videoUrl != null
        binding.ivExpand.animate()
            .rotation(if (binding.elMandateDetails.isExpanded) 180f else 0f).start()
        setupAdapter(mandateEducationPageItem)
    }

    private fun setupAdapter(mandateEducationPageItem: MandateEducationPageItem) {
        binding.rvEducation.layoutManager = LinearLayoutManager(binding.root.context)
        adapter = EducationItemAdapter()
        binding.rvEducation.adapter = adapter
        adapter?.submitList(mandateEducationPageItem.mandateEducationList)
    }
}