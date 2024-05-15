package com.jar.app.core_ui.view_holder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.jar.app.core_ui.databinding.CellLoadStateFooterBinding

class LoadStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<LoadStateViewHolder>() {
    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): LoadStateViewHolder {
        val binding =
            CellLoadStateFooterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadStateViewHolder(binding, retry)
    }
}