package com.example.edukid_android.utils

import android.content.Context

fun getAvatarResource(context: Context, avatarName: String): Int {
    return context.resources.getIdentifier(avatarName, "drawable", context.packageName)
}
