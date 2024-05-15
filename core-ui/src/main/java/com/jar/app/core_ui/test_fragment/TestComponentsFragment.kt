package com.jar.app.core_ui.test_fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_base.domain.model.ExpandableDataItem
import com.jar.app.core_ui.R
import com.jar.app.core_ui.databinding.FragmentTestBinding
import com.jar.app.core_ui.expandable_rv.ExpandableItemRVAdapter
import com.jar.app.core_ui.extension.setDebounceClickListener
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference

@AndroidEntryPoint
internal class TestComponentsFragment : BaseFragment<FragmentTestBinding>() {

    private var activityRef: WeakReference<FragmentActivity>? = null

    var expandableItemRVAdapter: ExpandableItemRVAdapter? = null

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentTestBinding
        get() = FragmentTestBinding::inflate

    override fun setupAppBar() {}

    override fun setup(savedInstanceState: Bundle?) {
        activityRef = WeakReference(requireActivity())
        setupUI()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun setupUI() {
        setupExpandableRV()
        setupListeners()
    }

    private fun setupListeners() {
        binding.navigateToCompose.setDebounceClickListener {
            findNavController().navigate(Uri.parse("android-app://com.jar.app/composeFragment/"))
        }
    }

    private fun setupExpandableRV() {
        binding.expandableRv.apply {
            expandableItemRVAdapter = ExpandableItemRVAdapter {
                updateMoreInfoList(it)
            }
            this.adapter = expandableItemRVAdapter
            this.layoutManager = LinearLayoutManager(requireContext())
            expandableItemRVAdapter?.submitList(generateTestContent())
        }
    }

    private fun generateTestContent(): MutableList<ExpandableDataItem> {
        val list = mutableListOf<ExpandableDataItem>()
        list.add(ExpandableDataItem.DefaultBannerWithBGIsExpandedDataType("DefaultBannerWithBGIsExpandedDataType", "Answer"))
        list.add(ExpandableDataItem.CardHeaderIsExpandedDataType("CardHeaderIsExpandedDataType", "Answer"))
        list.add(ExpandableDataItem.LeftIconIsExpandedDataType(R.drawable.ic_single_gold_coin, "LeftIconIsExpandedDataType", answer = "Answer", imageUrl = null))
        return list
    }

    private fun updateMoreInfoList(position: Int) {
        expandableItemRVAdapter?.let {
            val newList = expandableItemRVAdapter?.currentList?.toMutableList() ?: generateTestContent()
            val item = newList[position]
            item.isExpanded = !item.isExpanded
            newList?.forEach { it.isExpanded = false }
            newList[position] = item
            it?.submitList(newList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activityRef = null
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()

    }
}