package com.content_provider_android_example.android.ui

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.content_provider_android_example.android.ContactAction
import com.content_provider_android_example.android.R
import com.content_provider_android_example.android.model.Contacts
import com.content_provider_android_example.android.ui.contact.ContactAdapter
import com.content_provider_android_example.android.viewmodel.ContactsViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ContactAdapter
    private val contactsViewModel: ContactsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ContactAdapter { action, contact ->
            when (action) {
                ContactAction.ShowMenu -> showContextMenu(contact)
                ContactAction.Edit -> editContact(contact)
                ContactAction.Delete -> deleteContact(contact)
            }
        }
        recyclerView.adapter = adapter

        contactsViewModel.contactsLiveData.observe(this, Observer { contacts ->
            adapter.setItems(contacts)
        })

        // Button to add a new contact
        val addButton: FloatingActionButton = findViewById(R.id.addButton)
        addButton.setOnClickListener {
            showAddContactDialog()
        }
    }

    private fun showContextMenu(contact: Contacts) {
        val contextMenu = AlertDialog.Builder(this)
            .setTitle("Contact Options")
            .setItems(arrayOf("Edit", "Delete", "Cancel")) { dialog, which ->
                when (which) {
                    0 -> editContact(contact)  // Edit
                    1 -> deleteContact(contact) // Delete
                    2 -> dialog.dismiss()       // Cancel
                }
            }
            .create()
        contextMenu.show()
    }

    private fun editContact(contact: Contacts) {
        // Show a dialog to edit the contact
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_contact, null)
        val nameEditText: EditText = dialogView.findViewById(R.id.editTextName)
        val phoneEditText: EditText = dialogView.findViewById(R.id.editTextPhone)

        nameEditText.setText(contact.name)
        phoneEditText.setText(contact.phoneNumber)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Edit Contact")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val name = nameEditText.text.toString()
                val phone = phoneEditText.text.toString()

                if (name.isNotEmpty() && phone.isNotEmpty()) {
                    contactsViewModel.updateContact(contact.copy(name = name, phoneNumber = phone))
                    Toast.makeText(this, "Contact updated", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        this,
                        "Please enter both name and phone number",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun deleteContact(contact: Contacts) {
        contactsViewModel.deleteContact(contact)
        Toast.makeText(this, "Contact deleted", Toast.LENGTH_SHORT).show()
    }

    private fun showAddContactDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_contact, null)

        val nameEditText: EditText = dialogView.findViewById(R.id.editTextName)
        val phoneEditText: EditText = dialogView.findViewById(R.id.editTextPhone)

        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.add_new_contact_title)
            .setView(dialogView)
            .setPositiveButton(R.string.save_button_text) { _, _ ->
                val name = nameEditText.text.toString()
                val phone = phoneEditText.text.toString()

                if (name.isNotEmpty() && phone.isNotEmpty()) {
                    contactsViewModel.addNewContact(name, phone)
                    Toast.makeText(this, R.string.new_contact_added, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        this,
                        R.string.enter_name_phone,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton(R.string.cancel_button_text, null)
            .create()

        dialog.show()
    }
}
