package com.rajit.memeology.bindingadapters

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.rajit.memeology.R

class RandomMemeVisibilityBA {

    companion object {

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