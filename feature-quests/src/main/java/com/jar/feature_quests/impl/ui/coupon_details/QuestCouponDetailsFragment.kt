package com.jar.feature_quests.impl.ui.coupon_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.copyToClipboard
import com.jar.app.base.util.dp
import com.jar.app.base.util.getDateShortMonthNameAndYear
import com.jar.app.core_compose_ui.utils.InternalLinkMovementMethod
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.snackBar
import com.jar.app.feature_coupon_api.domain.model.coupon_details.CouponDetailsResponse
import com.jar.app.feature_quests.R
import com.jar.app.feature_quests.databinding.FragmentQuestsCouponDetailsBinding
import com.jar.feature_quests.impl.util.QuestEventKey
import com.jar.feature_quests.impl.util.createBulletList
import com.jar.feature_quests.impl.util.createNumberedList
import com.jar.internal.library.jar_core_network.api.util.collect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

@AndroidEntryPoint
internal class QuestCouponDetailsFragment: BaseFragment<FragmentQuestsCouponDetailsBinding>() {

    private val args by navArgs<QuestCouponDetailsFragmentArgs>()
    private val viewModel by viewModels<QuestCouponDetailsViewModel> { defaultViewModelProviderFactory }

    private val linkClickedListener = object : InternalLinkMovementMethod.OnLinkClickedListener {
        override fun onLinkClicked(url: String?): Boolean {
            viewModel.fireClickedEvent(QuestEventKey.Values.link)
            return false
        }
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentQuestsCouponDetailsBinding
        get() = FragmentQuestsCouponDetailsBinding::inflate

    override fun setupAppBar() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchCouponDetails(args.brandCouponId)
    }

    override fun setup(savedInstanceState: Bundle?) {
        observeLiveData()
        viewModel.fireShownEvent()
    }

    private fun observeLiveData() {
        val weakRef: WeakReference<View> = WeakReference(binding.root)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.couponDetailsFlow.collect(
                    onLoading = {
                        showProgressBar()
                    },
                    onSuccess = {
                        dismissProgressBar()
                        setupUI(it)
                    },
                    onError = { errorMessage, _ ->
                        dismissProgressBar()
                        errorMessage.snackBar(weakRef.get()!!)
                    }
                )
            }
        }
    }

    private fun setupUI(couponDetails: CouponDetailsResponse) {
        binding.apply {

            tvHeaderText.text = couponDetails.pageHeader
            Glide.with(requireContext())
                .load(couponDetails.brandIconLink)
                .override(48.dp)
                .into(ivBrandIconCouponDetails)
            tvCouponDetailsBrandTitle.text = couponDetails.brandName
            tvCouponDetailsHeading.text = couponDetails.title
            tvCouponDetailsDescription.text = couponDetails.description
            couponDetails.expiryDate?.let { expiryData->
                tvCouponDetailsValidTill.text = String.format(getString(R.string.feature_quests_coupon_validity), expiryData.getDateShortMonthNameAndYear())
            } ?: kotlin.run {
                tvCouponDetailsValidTill.isVisible = false
            }


            tvCouponDetailsCouponCodeTitle.text = couponDetails.couponCodeText
            if(couponDetails.couponCode.isNullOrBlank()){
                groupCouponDetails.isVisible = false
            }else{
                groupCouponDetails.isVisible = true
                tvCouponCode.text = couponDetails.couponCode
            }

            if (couponDetails.redeemHeader.isNullOrEmpty().not() && couponDetails.redeemDescription.isNullOrEmpty().not()) {
                tvCouponDetailsRedeemHeader.text = couponDetails.redeemHeader
                val redeemDescriptionListItems = extractListItems(couponDetails.redeemDescription.orEmpty())
                tvCouponDetailsRedeemDescription.text = createBulletList(redeemDescriptionListItems,requireContext())
                tvCouponDetailsRedeemDescription.movementMethod = object : InternalLinkMovementMethod(linkClickedListener) {}
            } else {
                groupRedeem.isVisible = false
            }

            if (couponDetails.termsAndConditionsHeader.isNullOrEmpty().not() && couponDetails.termsAndConditionsDescription.isNullOrEmpty().not()) {
                tvTermsAndConditionHeader.text = couponDetails.termsAndConditionsHeader
                val termsAndDescriptionListItem = extractListItems(couponDetails.termsAndConditionsDescription.orEmpty())
                tvTermsAndConditionDescription.text = createNumberedList(termsAndDescriptionListItem,requireContext())
                tvTermsAndConditionDescription.movementMethod = object : InternalLinkMovementMethod(linkClickedListener) {}
            } else {
                groupTNC.isVisible = false
            }

            btnCopy.setDebounceClickListener {
                viewModel.fireClickedEvent(QuestEventKey.Values.copy)
                val couponCode = binding.tvCouponCode.text.toString()
                requireContext().copyToClipboard(couponCode, String.format(getString(R.string.feature_quests_coupon_copied),couponCode))
            }
            binding.ivBack.setDebounceClickListener {
                viewModel.fireClickedEvent(QuestEventKey.Values.back_button)
                popBackStack()
            }
        }
    }

    private fun extractListItems(text: String): Pair<List<String>, List<String>> {
        val liRegex = Regex("<li>(.*?)</li>")
        val pRegex = Regex("<p>(.*?)</p>")

        val liMatches = liRegex.findAll(text)
        val pMatches = pRegex.findAll(text)

        val listItems = if (liMatches.any()) liMatches.map { it.groupValues[1] }.toList() else emptyList()
        val paragraphs = if (pMatches.any()) pMatches.map { it.groupValues[1] }.toList() else emptyList()

        return Pair(listItems, paragraphs)
    }
}