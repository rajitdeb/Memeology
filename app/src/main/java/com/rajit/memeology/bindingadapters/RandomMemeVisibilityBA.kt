package com.rajit.memeology.bindingadapters

import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.rajit.memeology.R

class RandomMemeVisibilityBA {

    companion object {

        @BindingAdapter("custom:noInternetViewVisibility")
        @JvmStatic
        fun setNoInternetViewVisibility(view: View, hasInternet: Boolean) {
            when (view) {
                is MaterialCardView ->
                    if (!hasInternet) view.visibility = View.VISIBLE else view.visibility = View.INVISIBLE
                is RecyclerView ->
                    if (!hasInternet) view.visibility = View.INVISIBLE else view.visibility = View.VISIBLE
            }
            Toast.makeText(view.context, "hasInternet: $hasInternet", Toast.LENGTH_SHORT).show()
        }

        // loadImage Working Perfectly
        @BindingAdapter("android:loadImageForRandomMeme")
        @JvmStatic
        fun loadImageForRandomMeme(view: ImageView, memeImgUrl: String?) {
            /**
             * Glide has been used instead of COIL because of GIF support
             * it uses less code for GIF support than COIL
             * COIL needs separate image loaders and extension function for having GIF support
             * */
            if(memeImgUrl != null){
                Glide.with(view)
                    .load(Uri.parse(memeImgUrl))
                    .into(view)
            }else{
                view.setImageResource(R.drawable.placeholder)
            }
        }

    }

}