package com.jar.app.feature_payment.impl.ui.add_card

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import com.jar.app.base.data.event.UpdateAppBarEvent
import com.jar.app.base.data.model.AppBarData
import com.jar.app.base.data.model.ToolbarNone
import com.jar.app.base.ui.fragment.BaseFragment
import com.jar.app.core_network.util.toJSONObject
import com.jar.app.core_ui.extension.hideKeyboard
import com.jar.app.core_ui.extension.setDebounceClickListener
import com.jar.app.core_ui.widget.credit_card_view.CreditCardView
import com.jar.app.feature_payment.R
import com.jar.app.feature_payment.databinding.FragmentAddCardBinding
import com.jar.app.feature_one_time_payments.shared.domain.event.CardInfoEvent
import com.jar.app.feature_one_time_payments.shared.domain.event.InitiateGetCardInfoWithJuspay
import com.jar.app.feature_one_time_payments.shared.domain.event.InitiateNewCardPaymentWithJuspay
import com.jar.app.feature_one_time_payments.shared.domain.model.juspay.CardInfo
import com.jar.app.feature_one_time_payments.shared.domain.model.juspay.GetCardInfoPayload
import com.jar.app.feature_one_time_payments.shared.domain.model.juspay.InitiateNewCardPaymentPayload
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

@AndroidEntryPoint
class AddCardFragment : BaseFragment<FragmentAddCardBinding>(),
    CreditCardView.OnCardDetailsChangedListener, CreditCardView.OnCardItemFocusListener {

    private var cardInfo: CardInfo? = null

    private val args by navArgs<AddCardFragmentArgs>()

    private var job: Job? = null

    private var cardBin: String? = null

    override val customBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentAddCardBinding
        get() = FragmentAddCardBinding::inflate

    override fun setupAppBar() {
        EventBus.getDefault()
            .post(UpdateAppBarEvent(AppBarData(ToolbarNone)))
    }

    override fun setup(savedInstanceState: Bundle?) {
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            popBackStack()
        }

        binding.btnNext.setDebounceClickListener {
            val isValid = validateCardDetails()
            if (isValid) {

                binding.creditCardView.flipCard(CreditCardView.CardSide.FRONT)
                binding.creditCardView.hideKeyboard()
                //Store Card Details
                makePayment(
                    InitiateNewCardPaymentPayload(
                        requestId = args.requestId,
                        orderId = args.initiatePaymentPayload.orderId,
                        endUrl = args.initiatePaymentPayload.callbackUrl,
                        paymentMethod = cardInfo?.brand!!,
                        cardNumber = binding.creditCardView.getRawCardNumber()!!,
                        nameOnCard = binding.creditCardView.getCardName()!!,
                        cardExpMonth = binding.creditCardView.getCardExpiryMonth().toString(),
                        cardExpYear = binding.creditCardView.getCardExpiryYear().toString(),
                        cardSecurityCode = binding.creditCardView.getCvv()!!,
                        saveToLocker = true,
                        clientAuthToken = args.initiatePaymentPayload.clientAuthToken,
                        showLoader = true
                    )
                )
            }
        }

        binding.btnEditPreviousDetails.setDebounceClickListener {
            binding.creditCardView.flipCard(CreditCardView.CardSide.FRONT)
        }

        binding.creditCardView.setOnCardDetailsChangedListener(this)

        binding.creditCardView.setOnCardItemFocusListener(this)
    }

    private fun validateCardDetails(): Boolean {
        if (!binding.creditCardView.isCardNumberValid() || !isCardValidFromApi()) {
            binding.creditCardView.focusView(CreditCardView.CardViewElements.NUMBER)
            return false
        }
        if (!binding.creditCardView.isCardNameValid()) {
            binding.creditCardView.focusView(CreditCardView.CardViewElements.NAME)
            return false
        }
        if (!binding.creditCardView.isCardExpiryValid()) {
            binding.creditCardView.focusView(CreditCardView.CardViewElements.EXPIRY)
            return false
        }
        if (!binding.creditCardView.isCvvValid()) {
            binding.creditCardView.focusView(CreditCardView.CardViewElements.CVV)
            return false
        }
        return true
    }

    override fun onDestroyView() {
        binding.creditCardView.onDestroy()
        super.onDestroyView()
    }

    override fun onCardNumberChangedListener(number: CharSequence?) {
        if (binding.creditCardView.isCardNumberValid() && isCardValidFromApi()) {
            binding.btnNext.alpha = 1f
        } else {
            binding.btnNext.alpha = 0.5f
        }

        val bin = if (number?.length ?: 0 >= 6) number?.substring(0, 6) else null

        when {
            cardBin != bin -> {
                cardBin = bin
                fetchCardInfo(cardBin)
            }

            number?.length ?: 0 < 6 -> {
                binding.creditCardView.setCardBrand(CreditCardView.CardBrand.OTHER)
                binding.creditCardView.setBankName(null)
            }

            number.isNullOrBlank() -> {
                updateCardState(true)
            }
        }
    }

    override fun onCardNameChangedListener(number: CharSequence?) {
        binding.btnNext.alpha = if (binding.creditCardView.isCardNameValid()) 1f else 0.5f
    }

    override fun onCardExpiryChangedListener(number: CharSequence?) {
        binding.btnNext.alpha = if (binding.creditCardView.isCardExpiryValid()) 1f else 0.5f
        updateCardState(
            binding.creditCardView.isCardExpiryValid(),
            getString(R.string.feature_payment_invalid_expiry_date_please_try_agin)
        )
    }

    override fun onCardCvvChangedListener(cvv: CharSequence?) {
        binding.btnNext.alpha = if (binding.creditCardView.isCvvValid()) 1f else 0.5f
    }

    override fun onCardFlipListener(cardSide: CreditCardView.CardSide) {
        when (cardSide) {
            CreditCardView.CardSide.FRONT -> {
                binding.btnEditPreviousDetails.isVisible = false
                binding.creditCardView.focusView(CreditCardView.CardViewElements.NUMBER)
            }

            CreditCardView.CardSide.BACK -> {
                binding.btnEditPreviousDetails.isVisible = true
            }
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    private fun updateCardBinDetails(cardInfo: CardInfo) {
        val isValid = !cardInfo.bank.isNullOrBlank()
        binding.creditCardView.setCardValidFromApi(isValid)
        if (isValid && binding.creditCardView.getCardNumber()?.length ?: 0 >= 6) {
            val cardBrand = CreditCardView.CardBrand.values().find { it.name == cardInfo.brand }
            binding.creditCardView.setCardBrand(cardBrand ?: CreditCardView.CardBrand.OTHER)
            binding.creditCardView.setBankName(cardInfo.bank)
        }

        if ((binding.creditCardView.getCardNumber()?.length ?: 0) >= 6) {
            updateCardState(
                isValid,
                getString(R.string.feature_payment_invalid_card_number_please_try_agin)
            )
        }
    }

    private fun makePayment(initiateNewCardPaymentPayload: InitiateNewCardPaymentPayload) {
        EventBus.getDefault().post(InitiateNewCardPaymentWithJuspay(initiateNewCardPaymentPayload))

        uiScope.launch {
            delay(1500)
            popBackStack()
        }

    }

    private fun updateCardState(isValid: Boolean, errorMessage: String? = null) {
        if (isValid) {
            binding.clCard.setBackgroundResource(0)
            binding.tvError.isVisible = false
        } else {
            binding.clCard.setBackgroundResource(R.drawable.feature_payment_bg_border_22dp)
            binding.tvError.text = errorMessage
            binding.tvError.isVisible = true
        }
    }

    private fun fetchCardInfo(number: CharSequence?) {
        if (number.isNullOrBlank())
            return
        job?.cancel()
        job = uiScope.launch {
            EventBus.getDefault().post(
                InitiateGetCardInfoWithJuspay(
                    GetCardInfoPayload(
                        requestId = args.requestId,
                        cardBin = number.toString(),
                        clientAuthToken = args.initiatePaymentPayload.clientAuthToken,
                    )
                )
            )
        }
    }

    private fun isCardValidFromApi() = !cardInfo?.bank.isNullOrBlank()

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onCardInfoEvent(cardInfoEvent: CardInfoEvent) {
        EventBus.getDefault().removeStickyEvent(cardInfoEvent)
        cardInfo = cardInfoEvent.cardInfo
        updateCardBinDetails(cardInfoEvent.cardInfo)
    }

    override fun onCardNumberFocus() {
        binding.btnNext.alpha = if (binding.creditCardView.isCardNumberValid()) 1f else 0.5f
    }

    override fun onCardNameFocus() {
        binding.btnNext.alpha = if (binding.creditCardView.isCardNameValid()) 1f else 0.5f
    }

    override fun onCardExpiryFocus() {
        binding.btnNext.alpha = if (binding.creditCardView.isCardExpiryValid()) 1f else 0.5f
    }

    override fun onCardCvvFocus() {
        binding.btnNext.alpha = if (binding.creditCardView.isCvvValid()) 1f else 0.5f
    }

}