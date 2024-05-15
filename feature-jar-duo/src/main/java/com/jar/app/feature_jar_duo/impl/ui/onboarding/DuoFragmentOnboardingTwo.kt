package com.jar.app.feature_jar_duo.impl.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.feature_jar_duo.databinding.FeatureDuoFragmentOnboardingTypeTwoBinding
import com.jar.app.feature_jar_duo.shared.domain.model.OnboardingData

internal class DuoFragmentOnboardingTwo:BaseFragment<FeatureDuoFragmentOnboardingTypeTwoBinding>() {
    companion object{
        private const val ONBOARDING_DATA = "OnboardingData"
        fun newInstance(onboardingData: com.jar.app.feature_jar_duo.shared.domain.model.OnboardingData): DuoFragmentOnboardingTwo {
            val fragment = DuoFragmentOnboardingTwo()
            val bundle = Bundle()
            bundle.putParcelable(ONBOARDING_DATA, onboardingData)
            fragment.arguments = bundle
            return fragment
        }
    }
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureDuoFragmentOnboardingTypeTwoBinding
        get() = FeatureDuoFragmentOnboardingTypeTwoBinding::inflate

    override fun setupAppBar() {

    }

    override fun setup(savedInstanceState: Bundle?) {
        val data = arguments?.getParcelable<com.jar.app.feature_jar_duo.shared.domain.model.OnboardingData>(ONBOARDING_DATA)
        Glide.with(this).load(data?.imageUrl).into(binding.centerImage)
        binding.tvIntro.text = data?.title
    }
}