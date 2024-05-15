package com.jar.app.feature_lending.impl.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.feature_lending.databinding.FragmentLendingDummyScreenBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class LendingDummyScreenFragment : BaseFragment<FragmentLendingDummyScreenBinding>(){

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLendingDummyScreenBinding
        get() = FragmentLendingDummyScreenBinding::inflate

    override fun setupAppBar() {

    }

    override fun setup(savedInstanceState: Bundle?) {
        popBackStack()
    }
}