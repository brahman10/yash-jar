package com.jar.app.feature_homepage.impl.ui.homepage.custom_cards.jarDuoV2

import android.view.View
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.VisibilityState
import com.airbnb.lottie.LottieDrawable
import com.google.android.material.tabs.TabLayoutMediator
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.base.util.asInitials
import com.jar.app.base.util.doRepeatingTask
import com.jar.app.core_ui.dynamic_cards.base.CustomViewBindingEpoxyModel
import com.jar.app.core_base.domain.model.card_library.CardEventData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionData
import com.jar.app.core_base.domain.model.card_library.PrimaryActionType
import com.jar.app.core_ui.extension.playLottieWithUrlAndExceptionHandling
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.util.DynamicCardEventKey
import com.jar.app.core_ui.dynamic_cards.model.HomeFeedCard
import com.jar.app.feature_homepage.shared.domain.model.JarDuoData
import com.jar.app.feature_jar_duo.R
import com.jar.app.feature_jar_duo.databinding.FeatureDuoCellHomepageCardV2Binding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import com.jar.app.core_ui.R as R1

internal class JarDuoEpoxyModelV2(
    private val uiScope: CoroutineScope,
    private val jarDuoData: JarDuoData,
    private val onCardShown: (cardEventData: CardEventData) -> Unit,
    private val onActionClick: (primaryActionData: PrimaryActionData, cardEventData: CardEventData) -> Unit,
    private val hasContactPermission: Boolean,
    override var cardShownEventJob: Job? = null
) : HomeFeedCard,
    CustomViewBindingEpoxyModel<FeatureDuoCellHomepageCardV2Binding>(
        R.layout.feature_duo_cell_homepage_card_v2
    ) {

    private var job: Job? = null

    private val eventData by lazy {
        CardEventData(
            mutableMapOf(
                DynamicCardEventKey.CardType to jarDuoData.cardType,
                DynamicCardEventKey.FeatureType to jarDuoData.featureType,
            )
        )
    }

    private var binding: FeatureDuoCellHomepageCardV2Binding? = null

    override fun bindItem(binding: FeatureDuoCellHomepageCardV2Binding) {

        this.binding = binding

        binding.duoHomePageBikeLottie.playLottieWithUrlAndExceptionHandling(
            binding.root.context, BaseConstants.LottieUrls.DUO_BIKE
        ).apply {
            repeatCount = LottieDrawable.INFINITE
        }
        setupHomeCard()
        val duoHomeViewPagerAdapter = DuoHomeViewPagerAdapter()
        binding.duoCarouselViewPager.apply {
            adapter = duoHomeViewPagerAdapter

        }
        val duoCardViewPagerTexts = listOf(
            binding.root.context.resources.getString(R.string.more_friends_more_savings),
            binding.root.context.resources.getString(R.string.find_your_friends_start_saving_together)
        )
        TabLayoutMediator(binding.tabLayout, binding.duoCarouselViewPager) { tab, position ->
            tab.id = position
        }.attach()
        duoHomeViewPagerAdapter.submitList(duoCardViewPagerTexts)

        binding.ivInfo.setDebounceClickListener {
            onActionClick.invoke(
                PrimaryActionData(
                    type = PrimaryActionType.DEEPLINK,
                    value = "${BaseConstants.BASE_EXTERNAL_DEEPLINK}${BaseConstants.ExternalDeepLinks.JAR_DUO_ONBOARDING}",
                    order = jarDuoData.getSortKey(),
                    cardType = jarDuoData.getCardType(),
                    featureType = jarDuoData.featureType
                ),
                eventData
            )

        }
    }

    private fun setupHomeCard() {
        val duoContactsMetaData = jarDuoData.duoContactsMetaData
        when {
            duoContactsMetaData?.totalPendingInvites?.isNotEmpty() == true -> {
                hideCarouselView()
                val pendingInvites = duoContactsMetaData.totalPendingInvites
                hideContactMessageLayout()
                showPendingInviteLayout(pendingInvites)
            }
            duoContactsMetaData?.totalGroupCount?.isNotEmpty() == true -> {
                hideCarouselView()
                hidePendingInviteLayout()
                showDuoGroupMessageLayout()
            }
            duoContactsMetaData?.totalContactsToBeInvited != null && (duoContactsMetaData.totalContactsToBeInvited
                ?: 0) > 0 -> {
                hideCarouselView()
                hidePendingInviteLayout()
                showContactMessageLayout((((duoContactsMetaData.totalContactsToBeInvited?:0) / 10f) * 10).toInt())
            }
            else -> {
                showCarouselView()
                binding?.apply {
                    duoHomeCardMessageLayout.visibility = View.GONE
                    btnDuoAction.setText(root.resources.getString(R.string.feature_duo_home_card_find_your_friends))
                }
            }
        }

        binding?.btnDuoAction?.setDebounceClickListener {
            val buttonText = binding?.btnDuoAction?.getText().orEmpty()
            eventData.map[DynamicCardEventKey.Data] = buttonText
            onActionClick.invoke(
                PrimaryActionData(
                    type = PrimaryActionType.DEEPLINK,
                    value = "${BaseConstants.BASE_EXTERNAL_DEEPLINK}${BaseConstants.ExternalDeepLinks.JAR_DUO}",
                    order = jarDuoData.getSortKey(),
                    cardType = jarDuoData.getCardType(),
                    featureType = jarDuoData.featureType
                ),
                eventData
            )
        }
    }

    private fun showDuoGroupMessageLayout(){
        binding?.apply {
            homeMessageLayout.visibility = View.VISIBLE
            duoHomeCardMessageIcon.visibility = View.VISIBLE
            duoHomeCardContactMessage.visibility = View.VISIBLE
            duoHomeCardMessageLayout.visibility = View.VISIBLE
            duoHomeCardMessageLayout.setBackgroundColor(ContextCompat.getColor(root.context, R1.color.color_704E8A))
            duoHomeCardMessageIcon.background = ContextCompat.getDrawable(root.context, R.drawable.feature_duo_ic_homecard_bell)
            duoHomeCardContactMessage.text = root.context.resources.getString(R.string.new_duo_activity_message)
            btnDuoAction.setText(root.context.resources.getString(R.string.feature_duo_view_duos))
        }
    }
    private fun hideDuoGroupMessageLayout(){
        binding?.apply {
            duoHomeCardMessageIcon.visibility = View.GONE
            duoHomeCardContactMessage.visibility = View.GONE
        }
    }

    private fun showContactMessageLayout(noOfContacts: Int) {
        binding?.apply {
            homeMessageLayout.visibility = View.VISIBLE
            duoHomeCardMessageLayout.setBackgroundColor(
                ContextCompat.getColor(
                    root.context,
                    com.jar.app.core_ui.R.color.color_40796C
                )
            )
            duoHomeCardMessageLayout.visibility = View.VISIBLE
            duoHomeCardMessageIcon.visibility = View.VISIBLE
            duoHomeCardMessageIcon.background = ContextCompat.getDrawable(
                root.context,
                R.drawable.feature_duo_contact_green_bg
            )
            duoHomeCardContactMessage.visibility = View.VISIBLE
            duoHomeCardContactMessage.text = String.format(
                root.context.resources.getString(R.string.feature_duo_no_of_contact_to_be_synced_msg),
                noOfContacts

            )
            btnDuoAction.setText(root.context.resources.getString(R.string.feature_duo_invite_friends))
        }
    }

    private fun hideContactMessageLayout() {
        binding?.apply {
           homeMessageLayout.visibility = View.GONE
        }
    }

    private fun hidePendingInviteLayout(){
        binding?.apply {
            pendingInviteHomeLayout.visibility = View.GONE
        }
    }
    private fun showPendingInviteLayout(pendingInvites: List<com.jar.app.feature_contact_sync_common.shared.domain.model.PendingInviteData>?){
        binding?.apply {
            duoHomeCardMessageLayout.setBackgroundColor(
                ContextCompat.getColor(
                    this.root.context,
                    R1.color.color_646EA7
                )
            )
            pendingInviteHomeLayout.visibility = View.VISIBLE
            duoHomeCardMessageLayout.visibility = View.VISIBLE
            user1Initials.visibility = View.VISIBLE
            user2Initials.visibility = View.VISIBLE
            duoHomeCardPendingMessage.visibility = View.VISIBLE
            if(pendingInvites?.size ==1){
                duoHomeCardPendingMessage.text = String.format(
                    this.root.context.resources.getString(R.string.feature_duo_pending_invite),
                    pendingInvites.size
                )
            }else{
            duoHomeCardPendingMessage.text = String.format(
                this.root.context.resources.getString(R.string.feature_duo_pending_invites),
               pendingInvites?.size
            )}

            when (pendingInvites?.size) {
                1 -> {
                    user1Initials.text = pendingInvites.getOrNull(0)?.name.orEmpty().asInitials()
                    user2Initials.visibility = View.GONE

                }
                2 -> {
                    user1Initials.text = pendingInvites.getOrNull(0)?.name.orEmpty().asInitials()
                    user2Initials.text = pendingInvites.getOrNull(1)?.name.orEmpty().asInitials()
                }
                else -> {
                    user1Initials.text = pendingInvites?.first()?.name?.asInitials()
                    user2Initials.text = "+${
                        jarDuoData.duoContactsMetaData?.totalPendingInvites?.size?.minus(
                            1
                        )
                    }"
                }
            }
            btnDuoAction.setText(this.root.context.resources.getString(R.string.feature_duo_view_invites))
        }
    }

    private fun hideCarouselView() {
        binding?.duoCarouselViewPager?.visibility = View.GONE
        binding?.duoTitleTextCarousel?.visibility = View.GONE
        binding?.tabLayout?.visibility = View.GONE
    }

    private fun showCarouselView() {
        binding?.duoCarouselViewPager?.visibility = View.VISIBLE
        binding?.duoTitleTextCarousel?.visibility = View.VISIBLE
        binding?.tabLayout?.visibility = View.VISIBLE
    }

    override fun onVisibilityStateChanged(visibilityState: Int, view: View) {
        super.onVisibilityStateChanged(visibilityState, view)
        if (visibilityState == VisibilityState.FULL_IMPRESSION_VISIBLE) {
            if (binding != null) {
                var counter = -1
                job?.cancel()
                job = uiScope.doRepeatingTask(repeatInterval = 3000) {
                    counter++
                    when (counter % 3) {
                        0 -> {
                            // autoSlideToNext(binding!!.clTab1, binding!!.clTab2)
                            binding!!.tabLayout.selectTab(binding!!.tabLayout.getTabAt(1), true)
                        }
                        1 -> {
                            // autoSlideToNext(binding!!.clTab2, binding!!.clTab3)
                            binding!!.tabLayout.selectTab(binding!!.tabLayout.getTabAt(0), true)
                        }
                    }
                }
            }
            onCardShown.invoke(eventData)
        } else if (visibilityState == VisibilityState.INVISIBLE)
            job?.cancel()
    }

    override fun getBinding(view: View): FeatureDuoCellHomepageCardV2Binding {
        return FeatureDuoCellHomepageCardV2Binding.bind(view)
    }

}