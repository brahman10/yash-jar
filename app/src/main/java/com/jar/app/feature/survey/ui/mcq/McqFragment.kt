package com.jar.app.feature.survey.ui.mcq

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.jar.app.core_ui.BaseEdgeEffectFactory
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_ui.item_decoration.SpaceItemDecoration
import com.jar.app.databinding.FragmentMcqBinding
import com.jar.app.feature.survey.ui.SurveyViewModel
import com.jar.app.base.util.addItemDecorationIfNoneAdded
import com.jar.app.base.util.dp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class McqFragment : BaseFragment<FragmentMcqBinding>() {
    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentMcqBinding
        get() = FragmentMcqBinding::inflate

    private var position = 0

    private val viewModel by activityViewModels<SurveyViewModel> { defaultViewModelProviderFactory }

    private val baseEdgeEffectFactory = BaseEdgeEffectFactory()
    private val spaceItemDecoration = SpaceItemDecoration(20.dp, 6.dp)
    private var mcqAdapter: McqAdapter? = null

    companion object {
        private const val EXTRA_POSITION = "EXTRA_POSITION"
        fun newInstance(position: Int) = McqFragment().apply {
            arguments = Bundle().apply {
                putInt(EXTRA_POSITION, position)
            }
        }
    }

    override fun setupAppBar() {

    }

    override fun setup(savedInstanceState: Bundle?) {
        getData()
        setupUI()
        setupListeners()
        observeLiveData()
    }

    private fun getData() {
        position = requireArguments().getInt(EXTRA_POSITION)
    }

    private fun setupUI() {
        mcqAdapter = McqAdapter { pos, choice ->
            viewModel.choiceSelected(this.position, pos, mcqAdapter!!.currentList)
        }
        binding.rvMcq.layoutManager = LinearLayoutManager(context)
        binding.rvMcq.adapter = mcqAdapter
        binding.rvMcq.edgeEffectFactory = baseEdgeEffectFactory
        binding.rvMcq.addItemDecorationIfNoneAdded(spaceItemDecoration)

        if (viewModel.getSurveyListSize() > 0)
            binding.tvQuestion.text = viewModel.getSurveyQuestionByPosition(position)
    }

    private fun setupListeners() {

    }

    private fun observeLiveData() {
        viewModel.choiceLiveData.observe(viewLifecycleOwner) {
            it?.let {
                mcqAdapter?.submitList(it)
            }
        }

//        viewModel.adapterPositionLiveData.observe(viewLifecycleOwner) {
//            binding.tvQuestion.text = viewModel.getSurveyQuestionByPosition(it)
//        }
    }
}