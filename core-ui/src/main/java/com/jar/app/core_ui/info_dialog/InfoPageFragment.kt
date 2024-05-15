package com.jar.app.core_ui.info_dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import com.jar.app.core_ui.databinding.CoreUiFragmentInfoPageBinding
import com.jar.app.core_base.domain.model.InfoPage
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration

class InfoPageFragment : BaseFragment<CoreUiFragmentInfoPageBinding>() {

    companion object {
        private const val INFO_PAGE = "INFO_PAGE"

        fun newInstance(infoPage: InfoPage): InfoPageFragment {
            val fragment = InfoPageFragment()
            val bundle = Bundle()
            bundle.putParcelable(INFO_PAGE, infoPage)
            fragment.arguments = bundle
            return fragment
        }
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> CoreUiFragmentInfoPageBinding
        get() = CoreUiFragmentInfoPageBinding::inflate

    private val spaceItemDecoration = SpaceItemDecoration(0.dp, 11.dp, RecyclerView.VISIBLE, true)

    private var adapter: InfoItemAdapter? = null

    private val infoPage by lazy {
        requireArguments().getParcelable<InfoPage>(INFO_PAGE)
    }

    override fun setupAppBar() {

    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
    }

    private fun setupUI() {
        binding.tvTitle.text = infoPage?.title
        binding.rvInfoItems.layoutManager = LinearLayoutManager(context)
        binding.rvInfoItems.addItemDecorationIfNoneAdded(spaceItemDecoration)
        adapter = InfoItemAdapter()
        binding.rvInfoItems.adapter = adapter
        adapter?.submitList(infoPage?.infoItems)
    }
}