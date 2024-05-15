package com.jar.app.feature_buy_gold_v2.impl.ui.offers_list.coupon_details_page

import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.jar.app.base.ui.BaseResources
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.copyToClipboard
import com.jar.app.base.util.dp
import com.jar.app.base.util.getDateShortMonthNameAndYear
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.observeNetworkResponse
import com.jar.app.feature_buy_gold_v2.R
import com.jar.app.feature_buy_gold_v2.databinding.FragmentCouponDetailsPageBinding
import com.jar.app.feature_buy_gold_v2.impl.ui.offers_list.jar_coupons.JarCouponViewModel
import com.jar.app.feature_buy_gold_v2.impl.util.createBulletList
import com.jar.app.feature_buy_gold_v2.impl.util.createNumberedList
import com.jar.app.feature_buy_gold_v2.shared.MR
import com.jar.app.feature_buy_gold_v2.shared.util.RewardsEventsKey
import com.jar.app.feature_coupon_api.domain.model.coupon_details.CouponDetailsResponse
import com.jar.internal.library.jarcoreanalytics.api.AnalyticsApi
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
internal class CouponDetailsFragment : BaseFragment<FragmentCouponDetailsPageBinding>(), BaseResources {

    @Inject
    lateinit var analyticsHandler: AnalyticsApi

    private val args by navArgs<CouponDetailsFragmentArgs>()
    private val viewModel by viewModels<JarCouponViewModel> { defaultViewModelProviderFactory }
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentCouponDetailsPageBinding
        get() = FragmentCouponDetailsPageBinding::inflate

    override fun setupAppBar() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchCouponDetails(args.brandCouponId)
    }

    override fun setup(savedInstanceState: Bundle?) {

        observeLiveData()
        setupListeners()

    }

    private fun setupListeners() {


    }

    private fun observeLiveData() {

        viewModel.couponDetailsLiveData.observeNetworkResponse(
            viewLifecycleOwner,
            WeakReference(binding.root),
            onLoading = {
                showProgressBar()
            },
            onSuccess = { couponDetails ->
                dismissProgressBar()
                setupUI(couponDetails)
            }, onError = {

            })


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
                tvCouponDetailsValidTill.text = String.format(getCustomString(MR.strings.coupon_validity), expiryData.getDateShortMonthNameAndYear())
            }


            tvCouponDetailsCouponCodeTitle.text = couponDetails.couponCodeText
            if(couponDetails.couponCode.isNullOrBlank()){
                groupCouponDetails.isVisible = false
            }else{
                groupCouponDetails.isVisible = true
                tvCouponCode.text = couponDetails.couponCode
            }

            tvCouponDetailsRedeemHeader.text = couponDetails.redeemHeader
            couponDetails.redeemDescription?.let { redeemHeader->

            val redeemDescriptionListItems = extractListItems(redeemHeader)
            tvCouponDetailsRedeemDescription.text = createBulletList(redeemDescriptionListItems,requireContext())
            tvCouponDetailsRedeemDescription.movementMethod = LinkMovementMethod.getInstance()
            tvTermsAndConditionHeader.text = couponDetails.termsAndConditionsHeader
            }
            couponDetails.termsAndConditionsDescription?.let { tC->
                val termsAndDescriptionListItem =
                    extractListItems(tC)
                tvTermsAndConditionDescription.text = createNumberedList(termsAndDescriptionListItem,requireContext())
                tvTermsAndConditionDescription.movementMethod = LinkMovementMethod.getInstance()
            }

            ivShare.setDebounceClickListener {
                analyticsHandler.postEvent(
                    RewardsEventsKey.ClickedButtonRewardsSection,
                    mapOf(
                        RewardsEventsKey.Screen to RewardsEventsKey.Screen_Name,
                        RewardsEventsKey.Brand to couponDetails.brandName.orEmpty(),
                        RewardsEventsKey.CTA to RewardsEventsKey.Share,
                        RewardsEventsKey.Coupon to couponDetails.couponCode.orEmpty(),
                        RewardsEventsKey.Title to couponDetails.title.orEmpty(),
                        )
                )

                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    val message = couponDetails.shareMsg
                    putExtra(Intent.EXTRA_TEXT, message)
                }
                val chooserIntent = Intent.createChooser(intent, getCustomString(MR.strings.share_with))
                startActivity(chooserIntent)
            }

            btnCopy.setDebounceClickListener {
                analyticsHandler.postEvent(
                    RewardsEventsKey.ClickedButtonRewardsSection,
                    mapOf(
                        RewardsEventsKey.Screen to RewardsEventsKey.Screen_Name,
                        RewardsEventsKey.Brand to couponDetails.brandName.orEmpty(),
                        RewardsEventsKey.CTA to RewardsEventsKey.CopyCode,
                        RewardsEventsKey.Coupon to couponDetails.couponCode.orEmpty(),
                        RewardsEventsKey.Title to couponDetails.title.orEmpty(),

                    )
                )
                val couponCode = binding.tvCouponCode.text.toString()
                requireContext().copyToClipboard(couponCode, String.format(getCustomString(MR.strings.coupon_copied),couponCode))
            }
            binding.ivBack.setDebounceClickListener {
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