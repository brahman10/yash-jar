package com.jar.app.feature_jar_duo.impl.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.util.BaseConstants
import com.jar.app.feature_jar_duo.databinding.FeatureDuoFragmentOnboardingTypeOneBinding
import com.jar.app.feature_jar_duo.shared.domain.model.OnboardingData
import org.greenrobot.eventbus.EventBus

internal class DuoFragmentOnboardingOne : BaseFragment<FeatureDuoFragmentOnboardingTypeOneBinding>() {

    companion object {
        private const val ONBOARDING_DATA = "OnboardingData"
        fun newInstance(onboardingData: com.jar.app.feature_jar_duo.shared.domain.model.OnboardingData): DuoFragmentOnboardingOne {
            val fragment = DuoFragmentOnboardingOne()
            val bundle = Bundle()
            bundle.putParcelable(ONBOARDING_DATA, onboardingData)
            fragment.arguments = bundle
            return fragment
        }
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureDuoFragmentOnboardingTypeOneBinding
        get() = FeatureDuoFragmentOnboardingTypeOneBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        val data = arguments?.getParcelable<com.jar.app.feature_jar_duo.shared.domain.model.OnboardingData>(ONBOARDING_DATA)
        Glide.with(this).load(data?.imageUrl).into(binding.userImage)
        binding.tvIntro.text = data?.title
        binding.tvDetail.text = data?.desc
    }
}