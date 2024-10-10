package com.wagdev.inventorymanagement.core.util

import android.net.Uri
import androidx.room.TypeConverter

class UriConverter {

    @TypeConverter
    fun fromUri(uri: Uri?): String? {
        return uri?.toString()
    }

    @TypeConverter
    fun toUri(uriString: String?): Uri? {
        return uriString?.let { Uri.parse(it) }
    }
}
