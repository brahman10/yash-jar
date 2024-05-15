package com.jar.app.core_ui.view_holder

import androidx.core.view.isVisible
import androidx.paging.LoadState
import com.jar.app.core_ui.databinding.CellLoadStateFooterBinding
import com.jar.app.core_ui.extension.setDebounceClickListener

class LoadStateViewHolder(
    private val binding: CellLoadStateFooterBinding,
    retry: () -> Unit
) :
    BaseViewHolder(binding.root) {

    init {
        binding.btnRetry.setDebounceClickListener {
            retry.invoke()
        }
    }

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            binding.tvErrorMessage.text = loadState.error.localizedMessage
        }
        binding.progressBar.isVisible = loadState is LoadState.Loading
        binding.btnRetry.isVisible = loadState is LoadState.Error
        binding.tvErrorMessage.isVisible = loadState is LoadState.Error
    }
}