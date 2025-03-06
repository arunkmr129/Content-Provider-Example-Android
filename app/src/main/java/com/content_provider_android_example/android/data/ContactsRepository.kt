package com.content_provider_android_example.android.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import com.content_provider_android_example.android.model.Contacts

class ContactsRepository(private val context: Context) {

    private val contentUri: Uri = ContactsContentProvider.CONTENT_URI

    fun loadContacts(): List<Contacts> {
        val cursor: Cursor? = context.contentResolver.query(contentUri, null, null, null, null)
        val contacts = mutableListOf<Contacts>()

        cursor?.let {
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndex("_id"))   // Get ID as Long
                val name = it.getString(it.getColumnIndex("name"))
                val phone = it.getString(it.getColumnIndex("phone"))
                contacts.add(Contacts(id, name, phone))
            }
            it.close()
        }
        return contacts
    }

    fun addContact(name: String, phoneNumber: String) {
        val values = ContentValues().apply {
            put("name", name)
            put("phone", phoneNumber)
        }
        context.contentResolver.insert(contentUri, values)
    }

    fun updateContact(contact: Contacts) {
        val contactId: Long = contact.id
        val contentValues = ContentValues().apply {
            put("name", contact.name)
            put("phone", contact.phoneNumber)
        }
        val selection = "${ContactsDatabaseHelper.COLUMN_ID} = ?"
        val selectionArgs = arrayOf(contactId.toString())
        val updateContactUri =
            Uri.withAppendedPath(contentUri, contact.id.toString())  // Create URI with contact ID
        context.contentResolver.update(updateContactUri, contentValues, selection, selectionArgs)
    }

    fun deleteContact(contacts: Contacts) {
        val deleteContactUri =
            Uri.withAppendedPath(contentUri, contacts.id.toString())  // Create URI with contact ID
        context.contentResolver.delete(deleteContactUri, null, null)
    }
}
