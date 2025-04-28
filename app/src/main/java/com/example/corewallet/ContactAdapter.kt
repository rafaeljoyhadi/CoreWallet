package com.example.corewallet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter(
    var contacts: MutableList<Contact>, // Public and mutable list
    private val onItemClick: (Contact) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    // Update the list and notify the adapter
    fun updateList(newContacts: List<Contact>) {
        contacts.clear()
        contacts.addAll(newContacts)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contacts[position]
        holder.bind(contact)
    }

    override fun getItemCount(): Int = contacts.size

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvContactName = itemView.findViewById<TextView>(R.id.tv_contact_name)
        private val tvContactAccountNumber = itemView.findViewById<TextView>(R.id.tv_contact_account_number)

        fun bind(contact: Contact) {
            tvContactName.text = contact.name
            tvContactAccountNumber.text = contact.accountNumber

            itemView.setOnClickListener { onItemClick(contact) }
        }
    }
}