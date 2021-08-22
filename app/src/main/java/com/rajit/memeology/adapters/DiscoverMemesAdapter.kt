package com.rajit.memeology.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rajit.memeology.R
import com.rajit.memeology.data.local.entities.FavouritesEntity
import com.rajit.memeology.databinding.ItemDiscoverMemeBinding
import com.rajit.memeology.models.Meme
import com.rajit.memeology.utils.MemesDiffUtil
import com.rajit.memeology.utils.PostActions
import com.rajit.memeology.viewmodels.MainViewModel

class DiscoverMemesAdapter(private val mainViewModel: MainViewModel) :
    RecyclerView.Adapter<DiscoverMemesAdapter.MyViewHolder>() {

    private var mDiscoverMemesList: List<Meme> = mutableListOf()

    class MyViewHolder(private val binding: ItemDiscoverMemeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val postActions = PostActions()

        fun bind(mainViewModel: MainViewModel, currentMeme: Meme) {
            binding.discoverMemeData = currentMeme

            binding.itemFavourites.setOnClickListener {
                val favouritesEntity = FavouritesEntity(currentMeme)
                binding.itemFavourites.setImageResource(R.drawable.ic_bookmark)
                mainViewModel.saveMemes(favouritesEntity)
            }

            binding.itemDownload.setOnClickListener {
                Toast.makeText(it.context, "Download Clicked!", Toast.LENGTH_SHORT).show()
            }

            binding.itemShare.setOnClickListener {
                val shareText = it.context.getString(
                    R.string.share_text,
                    currentMeme.postLink,
                    currentMeme.url
                )
                postActions.shareMeme(it.context, shareText)
            }
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemDiscoverMemeBinding.inflate(inflater, parent, false)
                return MyViewHolder(binding)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentMeme = mDiscoverMemesList[position]
        holder.bind(mainViewModel, currentMeme)
    }

    override fun getItemCount(): Int {
        return mDiscoverMemesList.size
    }

    fun setData(newDiscoverList: List<Meme>) {
        val discoverDiffUtil = MemesDiffUtil(mDiscoverMemesList, newDiscoverList)
        val diffUtilResult = DiffUtil.calculateDiff(discoverDiffUtil)
        mDiscoverMemesList = newDiscoverList
        Log.i("FavouritesAdapter", "setData: $newDiscoverList")
        diffUtilResult.dispatchUpdatesTo(this)
    }

}