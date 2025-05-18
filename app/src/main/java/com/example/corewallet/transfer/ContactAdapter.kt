package com.example.corewallet.transfer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.corewallet.api.ContactResponse
import com.example.corewallet.databinding.ItemContactBinding

class ContactAdapter(
    private var items: List<ContactResponse> = emptyList(),
    private val onClick: (ContactResponse) -> Unit
) : RecyclerView.Adapter<ContactAdapter.Holder>() {

    fun updateList(newList: List<ContactResponse>) {
        items = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemContactBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: Holder, position: Int) =
        holder.bind(items[position])

    inner class Holder(private val b: ItemContactBinding)
        : RecyclerView.ViewHolder(b.root) {

        fun bind(c: ContactResponse) {
            // ViewBinding fields are camelCase versions of your XML ids:
            b.tvContactName.text = c.name
            b.tvContactAccountNumber.text = c.accountNumber
            b.root.setOnClickListener { onClick(c) }
        }
    }
}
