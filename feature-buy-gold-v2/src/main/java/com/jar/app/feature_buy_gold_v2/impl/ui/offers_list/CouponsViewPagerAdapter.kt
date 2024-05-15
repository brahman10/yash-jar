package com.jar.app.feature_buy_gold_v2.impl.ui.offers_list

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jar.app.feature_buy_gold_v2.impl.ui.offers_list.brand_coupons.BrandCouponsFragment
import com.jar.app.feature_buy_gold_v2.impl.ui.offers_list.jar_coupons.JarCouponsFragment

internal class CouponsViewPagerAdapter(fm: FragmentActivity) : FragmentStateAdapter(fm) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return JarCouponsFragment()
            1 -> return BrandCouponsFragment()
        }
        return JarCouponsFragment()
    }
}