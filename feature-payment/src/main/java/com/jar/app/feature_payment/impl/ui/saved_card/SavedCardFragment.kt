package com.jar.app.feature_payment.impl.ui.saved_card

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.base.util.textChanges
import com.jar.app.core_network.util.toJSONObject
import com.jar.app.core_ui.extension.hideKeyboard
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.extension.setOnImeActionDoneListener
import com.jar.app.core_ui.extension.showKeyboard
import com.jar.app.core_ui.widget.credit_card_view.CreditCardView
import com.jar.app.feature_one_time_payments.shared.domain.event.InitiateSavedCardPaymentWithJuspay
import com.jar.app.feature_one_time_payments.shared.domain.model.juspay.InitiateSavedCardPaymentPayload
import com.jar.app.feature_payment.R
import com.jar.app.feature_payment.databinding.FragmentSavedCardBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
class SavedCardFragment : BaseFragment<FragmentSavedCardBinding>(),
    CreditCardView.OnCardDetailsChangedListener {

    private var isCvvVisible = false

    private val args by navArgs<SavedCardFragmentArgs>()

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentSavedCardBinding
        get() = FragmentSavedCardBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault().post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        binding.tvTitle.text = getString(R.string.feature_payment_to_pay_n, args.amount)

        binding.creditCardView.setCardNumber(
            args.savedCard.cardNumber
                .replace("XXXXXXXX", "**** ****")
                .replace("-", " ")
        )

        binding.creditCardView.setCardName(args.savedCard.nameOnCard)

        binding.creditCardView.setCardExpiry(args.savedCard.getFormattedExpiryDate())

        val cardBrand =
            CreditCardView.CardBrand.values().find { it.name == args.savedCard.cardBrand }
                ?: CreditCardView.CardBrand.OTHER
        binding.creditCardView.setCardBrand(cardBrand)
        binding.creditCardView.setBankName(args.savedCard.cardIssuer)

        uiScope.launch {
            delay(500)
            binding.etCustomCvv.requestLayout()
            binding.etCustomCvv.showKeyboard()
        }

    }

    private fun setupListeners() {
        binding.btnTogglePassword.setOnClickListener {
            if (isCvvVisible) {
                isCvvVisible = false
                binding.etCustomCvv.transformationMethod = PasswordTransformationMethod()
                binding.etCustomCvv.setSelection(binding.etCustomCvv.text?.length ?: 0)
            } else {
                isCvvVisible = true
                binding.etCustomCvv.transformationMethod = null
                binding.etCustomCvv.setSelection(binding.etCustomCvv.text?.length ?: 0)
            }
        }

        binding.creditCardView.setOnCardDetailsChangedListener(this)

        binding.btnPay.setDebounceClickListener {
            binding.creditCardView.flipCard(CreditCardView.CardSide.FRONT)
            makePayment()
        }

        binding.etCustomCvv.textChanges()
            .onEach {
                binding.creditCardView.setCvv(it?.toString().orEmpty())
            }
            .launchIn(uiScope)

        binding.etCustomCvv.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && isBindingInitialized() && lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED))
                binding.creditCardView.flipCard(CreditCardView.CardSide.BACK)
        }

        binding.etCustomCvv.setOnImeActionDoneListener {
            binding.etCustomCvv.hideKeyboard()
            binding.creditCardView.flipCard(CreditCardView.CardSide.FRONT)
        }

        binding.btnBack.setDebounceClickListener {
            popBackStack()
        }

    }

    override fun onCardNumberChangedListener(number: CharSequence?) {

    }

    override fun onCardNameChangedListener(number: CharSequence?) {

    }

    override fun onCardExpiryChangedListener(number: CharSequence?) {

    }

    override fun onCardCvvChangedListener(cvv: CharSequence?) {
        updateButtonState()
    }

    override fun onCardFlipListener(cardSide: CreditCardView.CardSide) {

    }

    private fun updateButtonState() {
        binding.btnPay.alpha = if (binding.creditCardView.isCvvValid()) 1f else 0.5f
    }

    private fun makePayment() {
        if (binding.creditCardView.isCvvValid()) {
            //Initiate Saved Card Payment Request
            EventBus.getDefault().post(
                InitiateSavedCardPaymentWithJuspay(
                    InitiateSavedCardPaymentPayload(
                        requestId = args.requestId,
                        orderId = args.initiatePaymentPayload.orderId,
                        endUrl = args.initiatePaymentPayload.callbackUrl,
                        paymentMethod = args.savedCard.cardBrand,
                        cardToken = args.savedCard.cardToken,
                        cardSecurityCode = binding.creditCardView.getCvv()!!,
                        clientAuthToken = args.initiatePaymentPayload.clientAuthToken,
                        showLoader = true
                    )
                )
            )
            uiScope.launch {
                delay(1500)
                popBackStack()
            }
        }
    }
}