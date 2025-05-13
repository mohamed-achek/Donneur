package com.example.donneur.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class Post1(
    var name: String,
    var username: String,
    var time: Int,
    @StringRes var content: Int,
    @DrawableRes var imageResourceId : Int,
)
