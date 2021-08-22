package com.rajit.memeology.bindingadapters

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestListener
import com.rajit.memeology.R

class FavouritesBA {

    companion object {

        // loadImage Working Perfectly
        @BindingAdapter("android:loadImageFromFavourites")
        @JvmStatic
        fun loadImageFromFavourites(view: ImageView, memeImgUrl: String?) {
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