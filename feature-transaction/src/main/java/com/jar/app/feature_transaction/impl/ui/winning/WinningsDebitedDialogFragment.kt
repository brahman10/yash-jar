package com.jar.app.feature_transaction.impl.ui.winning

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jar.app.base.ui.fragment.BaseDialogFragment
import com.jar.app.base.util.decodeUrl
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.feature_transaction.R
import com.jar.app.feature_transaction.databinding.FeatureTransactionFragmentWinnigsDebitedDialogBinding
import com.jar.internal.library.jar_core_network.api.util.Serializer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WinningsDebitedDialogFragment :
    BaseDialogFragment<FeatureTransactionFragmentWinnigsDebitedDialogBinding>() {

    @Inject
    lateinit var serilizer: Serializer

    private val args by navArgs<WinningsDebitedDialogFragmentArgs>()

    private val winningData by lazy {
        val encoded = args.winningData
        serilizer.decodeFromString<com.jar.app.feature_transaction.shared.domain.model.WinningData>(decodeUrl(encoded))
    }

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FeatureTransactionFragmentWinnigsDebitedDialogBinding
        get() = FeatureTransactionFragmentWinnigsDebitedDialogBinding::inflate
    override val dialogConfig: DialogFragmentConfig
        get() = DEFAULT_CONFIG

    override fun setup() {
        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        /* Glide.with(requireActivity())
             .load(winningData.iconLink)
             .into((binding.ivTransactionType))*/

        binding.tvTitle.text = winningData.title
        binding.tvVolume.text = getString(R.string.feature_transaction_rs_value, winningData.amount)
        binding.tvAmount.text =
            getString(R.string.feature_transaction_rs_value, winningData.goldPurchaseAmount)
        binding.tvPartner.text = winningData.nestedCardName
//        binding.tvVolumeBuying.text = winningData.subTitle
        binding.tvStatus.text = winningData.status
        binding.tvStatus.setTextColor(winningData.getColorForStatus().getColor(requireContext()))
        binding.tvDate.text = winningData.date
    }

    private fun setupListeners() {
        binding.ivClose.setDebounceClickListener {
            dismiss()
        }

        binding.btnShowDetails.setDebounceClickListener {
            findNavController().navigate(Uri.parse("android-app://com.jar.app/transactionDetail/${winningData.orderId}/${winningData.assetTxnId}/${winningData.assetSourceType}"))
        }
    }
}