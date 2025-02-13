import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.airwallex.android.core.Environment
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.paymentacceptance.R
import com.airwallex.paymentacceptance.Settings
import com.airwallex.paymentacceptance.databinding.DialogDemoBinding

class DemoCardDialog(context: Context) : Dialog(context) {

    private lateinit var binding: DialogDemoBinding
    private var cardInfo: PaymentMethod.Card? = null
    private var isEnabled: Boolean = true
    private var payCallback: ((card: PaymentMethod.Card) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        cardInfo?.let { applyCardInfo(it) }
        payCallback?.let { applyPayCallback(it) }
    }

    fun setCardInfo(card: PaymentMethod.Card, isEnabled: Boolean = true): DemoCardDialog = apply {
        this.cardInfo = card
        this.isEnabled = isEnabled
        if (::binding.isInitialized) {
            applyCardInfo(card)
        }
    }

    private fun applyCardInfo(card: PaymentMethod.Card) {
        when (Settings.getEnvironment()) {
            Environment.PRODUCTION -> {
                binding.tvEnvironment.text = "PRODUCTION"
            }

            else -> {
                binding.etCardNumber.setText(card.number.toString())
                binding.etCardName.setText(card.name.toString())
                binding.etExpiresYY.setText(card.expiryYear.toString())
                binding.etExpiresMM.setText(card.expiryMonth.toString())
                binding.etCVC.setText(card.cvc.toString())
                binding.etCardNumber.isEnabled = this.isEnabled
                binding.etCardName.isEnabled = this.isEnabled
                binding.etExpiresYY.isEnabled = this.isEnabled
                binding.etExpiresMM.isEnabled = this.isEnabled
                binding.etCVC.isEnabled = this.isEnabled
                binding.tvEnvironment.text =
                    if (Settings.getEnvironment() == Environment.STAGING) "STAGING" else "DEMO"
            }
        }
    }

    fun setPayCallBack(callback: (card: PaymentMethod.Card) -> Unit): DemoCardDialog = apply {
        this.payCallback = callback
        if (::binding.isInitialized) {
            applyPayCallback(callback)
        }
    }

    private fun applyPayCallback(callback: (card: PaymentMethod.Card) -> Unit) {
        binding.btnPay.setOnClickListener {
            if (checkInputFields()) {
                callback(
                    PaymentMethod.Card.Builder()
                        .setNumber(binding.etCardNumber.text.toString())
                        .setName(binding.etCardName.text.toString())
                        .setExpiryMonth(binding.etExpiresMM.text.toString())
                        .setExpiryYear(binding.etExpiresYY.text.toString())
                        .setCvc(binding.etCVC.text.toString())
                        .build()
                )
                dismiss()
            }
        }
    }

    private fun checkInputFields(): Boolean {
        val errors = arrayOf(
            Pair(binding.etCardNumber, "Card number is required"),
            Pair(binding.etCardName, "Host name is required"),
            Pair(binding.etCVC, "CVC is required"),
            Pair(binding.etExpiresYY, "Expires year is required"),
            Pair(binding.etExpiresMM, "Expires month is required")
        )

        return errors.all { checkNotEmpty(it.first, it.second) }
    }

    private fun checkNotEmpty(editText: EditText, errorMessage: String): Boolean {
        return if (editText.text.isNullOrEmpty()) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            false
        } else {
            true
        }
    }
}