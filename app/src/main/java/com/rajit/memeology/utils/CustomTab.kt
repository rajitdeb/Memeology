package com.rajit.memeology.utils

import android.content.Context
import android.net.Uri
import androidx.appcompat.content.res.AppCompatResources
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.rajit.memeology.R

object CustomTab {

    /**
     * Load @param [url] in the Custom Tav
     * [CustomTabsIntent.Builder] is used to build the custom tab along with the specified customizations
     **/
    fun loadURL(context: Context, url: String) {

        val customCloseIcon = AppCompatResources.getDrawable(context, R.drawable.ic_custom_tab_back)

        // Light Mode Toolbar Color
        val lightModeColor = ContextCompat.getColor(
            context,
            R.color.actionBarColor
        )

        // Dark Mode Toolbar Color
        val darkModeColor = ContextCompat.getColor(
            context,
            R.color.actionBarColor
        )

        // Setting the Custom Tab Toolbar Configuration for Light Mode
        val customTabToolbarColorLight = CustomTabColorSchemeParams
            .Builder()
            .setToolbarColor(lightModeColor)
            .build()

        // Setting the Custom Tab Toolbar Configuration for Dark Mode
        val customTabToolbarColorDark = CustomTabColorSchemeParams
            .Builder()
            .setToolbarColor(darkModeColor)
            .build()

        CustomTabsIntent.Builder().apply {

            // Show Title of the Website in Custom Tab ToolBar
            setShowTitle(true)

            // Set Custom Close Button for Custom Tab
            setCloseButtonIcon(customCloseIcon!!.toBitmap())

            // applying light mode color
            setDefaultColorSchemeParams(customTabToolbarColorLight)

            // applying dark mode color
            setColorSchemeParams(
                CustomTabsIntent.COLOR_SCHEME_DARK,
                customTabToolbarColorDark
            )

            // Build & Launch URL in Custom Tab
            build().launchUrl(
                context,
                Uri.parse(url)
            )

        }

    }

}