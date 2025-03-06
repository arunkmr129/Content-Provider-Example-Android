package com.content_provider_android_example.android.ui.contact

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.content_provider_android_example.android.ContactAction
import com.content_provider_android_example.android.model.Contacts

class ContactAdapter(
    private val onContactAction: (ContactAction, Contacts) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    private val contacts = mutableListOf<Contacts>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return ContactViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        var srNum = position;
        holder.textView.text =
            "${++srNum}.   ${contacts[position].name} ${contacts[position].phoneNumber}"
        holder.itemView.setOnLongClickListener {
            onContactAction(ContactAction.ShowMenu, contacts[position])
            true  // Return true to indicate the event was handled
        }
    }

    override fun getItemCount(): Int = contacts.size

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(newContacts: List<Contacts>) {
        contacts.clear()
        contacts.addAll(newContacts)
        notifyDataSetChanged()
    }

    class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(android.R.id.text1)
    }

}
