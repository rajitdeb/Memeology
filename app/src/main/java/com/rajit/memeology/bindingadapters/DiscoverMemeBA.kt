package com.rajit.memeology.bindingadapters

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.rajit.memeology.R

class DiscoverMemeBA {

    companion object {
        // loadImage Working Perfectly
        @BindingAdapter("android:loadImageForDiscover")
        @JvmStatic
        fun loadImageForDiscover(view: ImageView, memeImgUrl: String?) {
            if(memeImgUrl != null){
                Glide.with(view)
                    .load(Uri.parse(memeImgUrl))
                    .error(R.drawable.placeholder)
                    .into(view)
            }else{
                view.setImageResource(R.drawable.placeholder)
            }
        }
    }

}