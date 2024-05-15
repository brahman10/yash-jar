package com.jar.app.core_ui.info_dialog

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jar.app.core_base.domain.model.InfoPage

class InfoPageAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val infoPages: List<InfoPage>
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return infoPages.size
    }

    override fun createFragment(position: Int): Fragment {
        return InfoPageFragment.newInstance(infoPages[position])
    }

}