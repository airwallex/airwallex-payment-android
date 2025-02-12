package com.airwallex.paymentacceptance.ui.widget

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.airwallex.paymentacceptance.databinding.ItemButtonBinding
import com.airwallex.paymentacceptance.ui.bean.ButtonItem


class ButtonAdapter(
    private var buttonList: List<ButtonItem>,
    private val onButtonClick: (Int) -> Unit
) : RecyclerView.Adapter<ButtonAdapter.ButtonViewHolder>() {

    inner class ButtonViewHolder(private val binding: ItemButtonBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ButtonItem) {
            binding.button.text = item.text
            binding.button.setOnClickListener { onButtonClick(item.id) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ButtonViewHolder {
        val binding = ItemButtonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ButtonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ButtonViewHolder, position: Int) {
        holder.bind(buttonList[position])
    }

    override fun getItemCount(): Int = buttonList.size

    fun updateButtons(newList: List<ButtonItem>) {
        buttonList = newList
        notifyDataSetChanged()
    }
}