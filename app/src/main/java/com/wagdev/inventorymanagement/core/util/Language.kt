package com.wagdev.inventorymanagement.core.util

import androidx.annotation.DrawableRes

data class Language(
    val code: String,
    val name: String,
    @DrawableRes val flag: Int
)