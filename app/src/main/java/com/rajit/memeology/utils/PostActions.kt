package com.rajit.memeology.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat.startActivity
import com.google.android.material.snackbar.Snackbar

class PostActions {

    private val TAG = "POST ACTIONS"

    fun showSnackBarMessage(rootLayout: CoordinatorLayout, msg: String) {
        Snackbar.make(
            rootLayout,
            msg,
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction("OK") {}
            .show()
    }

    fun downloadMeme(
        rootLayout: CoordinatorLayout,
        hasInternetConnection: Boolean
    ) {
        if (hasInternetConnection) {
            showSnackBarMessage(rootLayout, "Successfully downloaded meme")

        } else {
            showSnackBarMessage(
                rootLayout,
                "Download failed. Please check the connection and try again."
            )
        }
    }

    fun shareMeme(context: Context, shareText: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                shareText
            )
            type = "text/plain"
        }

        try {
            startActivity(context, sendIntent, null)
        } catch (e: ActivityNotFoundException) {
            Log.e(TAG, "shareMeme: ${e.message}")
        }
    }

}