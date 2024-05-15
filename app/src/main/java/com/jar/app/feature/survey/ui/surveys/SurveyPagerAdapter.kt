package com.jar.app.feature.survey.ui.surveys

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jar.app.feature.survey.domain.model.SurveyQuestion
import com.jar.app.feature.survey.ui.mcq.McqFragment

class SurveyPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    val fragments = ArrayList<Fragment>()

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int) = fragments[position]

}