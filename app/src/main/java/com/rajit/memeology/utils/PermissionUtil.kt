package com.rajit.memeology.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener
import com.karumi.dexter.listener.single.PermissionListener

class PermissionUtil {

    companion object {
        // Check if the storage permission is granted for downloading meme
        fun checkForStoragePermission(context: Context, permission: String): Boolean? {

            if (ContextCompat.checkSelfPermission(
                    context, permission
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                return true
            } else {
                val dialogPermissionListener: PermissionListener =
                    DialogOnDeniedPermissionListener.Builder
                        .withContext(context)
                        .withTitle("Permission Required")
                        .withMessage("Please allow this permission for the feature to work")
                        .withButtonText(android.R.string.ok)
                        .build()

                Dexter.withContext(context)
                    .withPermission(permission)
                    .withListener(dialogPermissionListener)
                    .check()

                return null
            }
        }
    }

}