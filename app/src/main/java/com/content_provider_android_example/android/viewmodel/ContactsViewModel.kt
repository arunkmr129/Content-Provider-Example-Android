package com.content_provider_android_example.android.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.content_provider_android_example.android.model.Contacts
import com.content_provider_android_example.android.data.ContactsRepository

class ContactsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ContactsRepository = ContactsRepository(application)
    private val _contactsLiveData: MutableLiveData<List<Contacts>> = MutableLiveData()
    val contactsLiveData: LiveData<List<Contacts>> get() = _contactsLiveData

    init {
        loadContacts()
    }

    private fun loadContacts() {
        _contactsLiveData.value = repository.loadContacts()
    }

    fun addNewContact(name: String, phoneNumber: String) {
        repository.addContact(name, phoneNumber)
        loadContacts()
    }

    fun updateContact(contact: Contacts) {
        repository.updateContact(contact)
        loadContacts()
    }

    fun deleteContact(contact: Contacts) {
        repository.deleteContact(contact)
        loadContacts()
    }

}
