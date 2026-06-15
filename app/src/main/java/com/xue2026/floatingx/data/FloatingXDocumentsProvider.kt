package com.xue2026.floatingx.data

import android.content.ContentProvider, android.content.ContentValues, android.database.Cursor
import android.net.Uri, android.os.ParcelFileDescriptor
import java.io.File, java.io.FileNotFoundException

class FloatingXDocumentsProvider : ContentProvider() {
    override fun onCreate() = true
    @Throws(FileNotFoundException::class)
    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {
        val f = File(context?.filesDir, uri.path ?: ""); return ParcelFileDescriptor.open(f, ParcelFileDescriptor.MODE_READ_ONLY)
    }
    override fun query(uri: Uri, p: Array<String>?, s: String?, a: Array<String>?, o: String?) = null
    override fun getType(uri: Uri) = null; override fun insert(uri: Uri, v: ContentValues?) = null
    override fun delete(uri: Uri, s: String?, a: Array<String>?) = 0
    override fun update(uri: Uri, v: ContentValues?, s: String?, a: Array<String>?) = 0
}