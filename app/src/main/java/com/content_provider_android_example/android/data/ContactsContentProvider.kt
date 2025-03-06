package com.content_provider_android_example.android.data

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri

class ContactsContentProvider : ContentProvider() {

    private lateinit var dbHelper: ContactsDatabaseHelper
    private lateinit var database: SQLiteDatabase

    companion object {
        const val AUTHORITY = "com.content_provider_android_example.android"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/contacts")
        const val CONTACTS = 1
        const val CONTACT_ID = 2
    }

    private val uriMatcher = android.content.UriMatcher(android.content.UriMatcher.NO_MATCH)

    init {
        uriMatcher.addURI(AUTHORITY, "contacts", CONTACTS)
        uriMatcher.addURI(AUTHORITY, "contacts/#", CONTACT_ID)
    }

    override fun onCreate(): Boolean {
        dbHelper = ContactsDatabaseHelper(context!!)
        database = dbHelper.writableDatabase
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        val match = uriMatcher.match(uri)
        return when (match) {
            CONTACTS -> {
                database.query(
                    ContactsDatabaseHelper.TABLE_CONTACTS, projection, selection, selectionArgs,
                    null, null, sortOrder
                )
            }

            CONTACT_ID -> {
                val id = uri.lastPathSegment?.toLongOrNull() ?: return null
                database.query(
                    ContactsDatabaseHelper.TABLE_CONTACTS, projection,
                    "${ContactsDatabaseHelper.COLUMN_ID} = ?", arrayOf(id.toString()),
                    null, null, sortOrder
                )
            }

            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val match = uriMatcher.match(uri)
        return when (match) {
            CONTACTS -> {
                val id = database.insert(ContactsDatabaseHelper.TABLE_CONTACTS, null, values)
                Uri.withAppendedPath(uri, id.toString())
            }

            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        val match = uriMatcher.match(uri)
        return when (match) {
            CONTACT_ID -> {
                val id = uri.lastPathSegment?.toLongOrNull() ?: return 0
                database.update(
                    ContactsDatabaseHelper.TABLE_CONTACTS, values,
                    "${ContactsDatabaseHelper.COLUMN_ID} = ?", arrayOf(id.toString())
                )
            }

            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        val match = uriMatcher.match(uri)
        return when (match) {
            CONTACT_ID -> {
                val id = uri.lastPathSegment?.toLongOrNull() ?: return 0
                database.delete(
                    ContactsDatabaseHelper.TABLE_CONTACTS,
                    "${ContactsDatabaseHelper.COLUMN_ID} = ?", arrayOf(id.toString())
                )
            }

            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun getType(uri: Uri): String {
        val match = uriMatcher.match(uri)
        return when (match) {
            CONTACTS -> "vnd.android.cursor.dir/vnd.$AUTHORITY.contacts"
            CONTACT_ID -> "vnd.android.cursor.item/vnd.$AUTHORITY.contacts"
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }
}