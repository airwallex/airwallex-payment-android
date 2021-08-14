package com.airwallex.android.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airwallex.android.core.extension.setOnSingleClickListener
import com.airwallex.android.core.model.Bank
import com.airwallex.android.databinding.DialogBankBinding
import com.airwallex.android.databinding.DialogBankItemBinding
import com.airwallex.android.dto.drawableRes

class PaymentBankBottomSheetDialog : BottomSheetDialog() {

    var onCompleted: ((bank: Bank) -> Unit)? = null

    companion object {
        private const val TITLE = "title"
        private const val CURRENCY = "currency"

        fun newInstance(
            title: String,
            currency: String
        ): PaymentBankBottomSheetDialog {
            val args = Bundle()
            args.putString(TITLE, title)
            args.putString(CURRENCY, currency)
            val fragment = PaymentBankBottomSheetDialog()
            fragment.arguments = args
            return fragment
        }
    }

    private val viewBinding: DialogBankBinding by lazy {
        DialogBankBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.title.text = arguments?.getString(TITLE)
        viewBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = BottomDialogAdapter(Bank.values().filter { it.currency == arguments?.getString(CURRENCY) }.toMutableList())
        }
    }

    inner class BottomDialogAdapter(
        private val values: MutableList<Bank>
    ) :
        RecyclerView.Adapter<BottomDialogHolder>() {

        override fun getItemCount() = values.size

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): BottomDialogHolder {
            return BottomDialogHolder(parent.context, parent)
        }

        override fun onBindViewHolder(holder: BottomDialogHolder, position: Int) {
            holder.bindView(values[position])
        }
    }

    inner class BottomDialogHolder(
        val viewBinding: DialogBankItemBinding
    ) : RecyclerView.ViewHolder(viewBinding.root) {
        constructor(context: Context, parent: ViewGroup) : this(
            DialogBankItemBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )

        fun bindView(bank: Bank) {
            viewBinding.bankName.text = bank.displayName
            viewBinding.bankLogo.setImageResource(bank.drawableRes)

            viewBinding.bankItem.setOnSingleClickListener {
                onCompleted?.invoke(bank)
                dismiss()
            }
        }
    }
}
