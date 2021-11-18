package com.airwallex.android.view

import android.content.Context
import android.content.res.Resources
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
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior

class PaymentBankBottomSheetDialog : BottomSheetDialog<DialogBankBinding>() {

    var onCompleted: ((bank: Bank) -> Unit)? = null

    companion object {
        private const val TITLE = "title"
        private const val BANKS = "banks"

        fun newInstance(
            title: String,
            banks: List<Bank>
        ): PaymentBankBottomSheetDialog {
            val args = Bundle()
            args.putString(TITLE, title)
            args.putParcelableArrayList(BANKS, ArrayList(banks))
            val fragment = PaymentBankBottomSheetDialog()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.title.text = arguments?.getString(TITLE)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = BottomDialogAdapter(
                arguments?.getParcelableArrayList(BANKS) ?: mutableListOf()
            )
        }
    }

    override fun onStart() {
        super.onStart()
        BottomSheetBehavior.from(requireView().parent as View).apply {
            peekHeight = Resources.getSystem().displayMetrics.heightPixels / 2
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
            Glide.with(this@PaymentBankBottomSheetDialog)
                .load(bank.resources?.logos?.png)
                .into(viewBinding.bankLogo)
            viewBinding.bankItem.setOnSingleClickListener {
                onCompleted?.invoke(bank)
                dismiss()
            }
        }
    }

    override fun bindFragment(inflater: LayoutInflater, container: ViewGroup): DialogBankBinding {
        return DialogBankBinding.inflate(inflater, container, true)
    }
}
