package com.rajit.memeology.utils

import android.os.Build

inline fun <T> sdk29AndUp(onSdk29: () -> T): T? {
    return if(Build.VERSION.SDK_INT >= 29) {
        onSdk29()
    }else null
}