package com.jar.app.feature.onboarding.ui.onboarding_story

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.databinding.FragmentStoryAnimationBinding
import com.jar.app.feature.onboarding.NewOnboardingViewModelAndroid
import com.jar.app.feature_onboarding.shared.domain.model.OnboardingStory
import org.greenrobot.eventbus.EventBus

class StoryAnimationFragment : BaseFragment<FragmentStoryAnimationBinding>() {

    companion object {
        private const val ONBOARDING_STORY = "ONBOARDING_STORY"

        fun newInstance(onboardingStory: com.jar.app.feature_onboarding.shared.domain.model.OnboardingStory): StoryAnimationFragment {
            val fragment = StoryAnimationFragment()
            val bundle = bundleOf(ONBOARDING_STORY to onboardingStory)
            fragment.arguments = bundle
            return fragment
        }
    }

    private val viewModelProvider by activityViewModels<NewOnboardingViewModelAndroid>()

    private val newOnboardingViewModel by lazy{
        viewModelProvider.getInstance()
    }

    private val onboardingStory by lazy {
        requireArguments().getParcelable<OnboardingStory>(ONBOARDING_STORY)!!
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentStoryAnimationBinding
        get() = FragmentStoryAnimationBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        Glide.with(this).load(onboardingStory.imageUrl).into(binding.ivImage)
        binding.tvTitle.text = onboardingStory.title
        setupListeners()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupListeners() {
        binding.root.setOnTouchListener { _, motionEvent ->
            newOnboardingViewModel.updateStoryTouchEvent(motionEvent.action)
            return@setOnTouchListener true
        }
    }
}