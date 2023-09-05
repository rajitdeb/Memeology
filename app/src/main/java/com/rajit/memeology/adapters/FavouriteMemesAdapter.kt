package com.rajit.memeology.adapters

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rajit.memeology.R
import com.rajit.memeology.data.local.entities.FavouritesEntity
import com.rajit.memeology.databinding.ItemFavouriteRowBinding
import com.rajit.memeology.utils.DownloadUtil
import com.rajit.memeology.utils.MemesDiffUtil
import com.rajit.memeology.utils.PermissionUtil
import com.rajit.memeology.utils.PostActions
import com.rajit.memeology.utils.sdk29AndUp
import com.rajit.memeology.viewmodels.MainViewModel

class FavouriteMemesAdapter(
    private val requireActivity: FragmentActivity,
    private val rootLayout: CoordinatorLayout,
    private val mainViewModel: MainViewModel
) : RecyclerView.Adapter<FavouriteMemesAdapter.FavouritesViewHolder>(), ActionMode.Callback {

    private var favouriteMemesList = emptyList<FavouritesEntity>()

    private var multiSelection = false

    private var selectedMemesList = arrayListOf<FavouritesEntity>()

    private var myViewHolders = arrayListOf<FavouritesViewHolder>()

    private lateinit var mActionMode: ActionMode

    private lateinit var rootView: View

    private lateinit var recyclerViewLayout: RecyclerView

    private val postActions = PostActions()

    class FavouritesViewHolder(val binding: ItemFavouriteRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(favouriteMeme: FavouritesEntity) {

            binding.favouritesEntity = favouriteMeme
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): FavouritesViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val layoutBinding = ItemFavouriteRowBinding.inflate(
                    inflater, parent, false
                )
                return FavouritesViewHolder(layoutBinding)
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        recyclerViewLayout = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouritesViewHolder {
        return FavouritesViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: FavouritesViewHolder, position: Int) {

        myViewHolders.add(holder)
        rootView = holder.binding.root

        val currentFavourite = favouriteMemesList[position]
        holder.bind(currentFavourite)

        holder.binding.itemDownload.setOnClickListener {

            if (mainViewModel.hasInternetConnection()) {
                val memeFileName = currentFavourite.meme.url.subSequence(
                    currentFavourite.meme.url.lastIndexOf("/") + 1,
                    currentFavourite.meme.url.lastIndex + 1
                )

                sdk29AndUp {
                    DownloadUtil.downloadMeme(
                        recyclerViewLayout.context,
                        memeFileName.toString(),
                        holder.binding.itemMemeImage.drawable,
                        currentFavourite.meme.url
                    )

                    Toast.makeText(
                        rootLayout.context,
                        "Meme downloaded successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                } ?: if (ContextCompat.checkSelfPermission(
                        recyclerViewLayout.context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {

                    DownloadUtil.downloadMeme(
                        recyclerViewLayout.context,
                        memeFileName.toString(),
                        holder.binding.itemMemeImage.drawable,
                        currentFavourite.meme.url
                    )

                    Toast.makeText(
                        rootLayout.context,
                        "Meme downloaded successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    PermissionUtil.checkForStoragePermission(
                        recyclerViewLayout.context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )

                }
            } else {
                Toast.makeText(
                    it.context,
                    "No internet connection",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        holder.binding.itemShare.setOnClickListener {
            val shareText = it.context.getString(
                R.string.share_text,
                currentFavourite.meme.postLink,
                currentFavourite.meme.url
            )
            postActions.shareMeme(it.context, shareText)
        }

        holder.binding.itemLinkToPost.setOnClickListener {
            val postUrl = Uri.parse(currentFavourite.meme.postLink)
            val takeMeToOriginalPost = Intent(Intent.ACTION_VIEW, postUrl)
            try {
                startActivity(it.context, takeMeToOriginalPost, null)
            } catch (e: ActivityNotFoundException) {
                Log.e(
                    "FavouriteMemesAdapter",
                    "takeMeToOriginalPost: Error occurred - ${e.message}"
                )
                Toast.makeText(it.context, "${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        saveOnItemScroll(currentFavourite, holder)

        /** Single-click Listener **/
        holder.binding.favouriteMemeRowLayout.setOnClickListener {
            if (multiSelection) {
                applySelection(holder, currentFavourite)
            }
        }

        /** LONG-click Listener **/
        holder.binding.favouriteMemeRowLayout.setOnLongClickListener {
            if (!multiSelection) {
                multiSelection = true
                requireActivity.startActionMode(this)
                applySelection(holder, currentFavourite)
                true
            } else {

                applySelection(holder, currentFavourite)
                true
            }

        }
    }


    private fun saveOnItemScroll(
        currentFavourite: FavouritesEntity,
        holder: FavouritesViewHolder
    ) {
        if (selectedMemesList.contains(currentFavourite)) {
            changeMemeStyle(holder, R.color.contextual_stroke_color)
        } else {
            changeMemeStyle(holder, R.color.card_stroke_color)
        }
    }

    private fun applySelection(holder: FavouritesViewHolder, currentFavourite: FavouritesEntity) {
        if (selectedMemesList.contains(currentFavourite)) {
            selectedMemesList.remove(currentFavourite)
            changeMemeStyle(holder, R.color.card_stroke_color)
            applyActionModeTitle()
        } else {
            selectedMemesList.add(currentFavourite)
            changeMemeStyle(holder, R.color.contextual_stroke_color)
            applyActionModeTitle()
        }
    }

    private fun changeMemeStyle(holder: FavouritesViewHolder, cardStrokeColor: Int) {
        holder.binding.favouriteCardView.strokeColor =
            ContextCompat.getColor(requireActivity, cardStrokeColor)
    }

    override fun getItemCount(): Int {
        return favouriteMemesList.size
    }

    /** ACTION MODE **/

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode?.menuInflater?.inflate(R.menu.favourites_contextual_menu, menu)
        mActionMode = mode!!
        applyStatusBarColor(R.color.contextual_status_bar_color)
        return true
    }

    private fun applyStatusBarColor(contextualStatusBarColor: Int) {
        requireActivity.window.statusBarColor =
            ContextCompat.getColor(requireActivity, contextualStatusBarColor)
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return true
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        if (item?.itemId == R.id.delete) {
            selectedMemesList.forEach {
                mainViewModel.deleteMeme(it)
            }

            showSnackBar("${selectedMemesList.size} Item/s Deleted")
            multiSelection = false
            selectedMemesList.clear()
            mode?.finish()
        }

        return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        myViewHolders.forEach { holder ->
            changeMemeStyle(holder, R.color.card_stroke_color)
        }
        multiSelection = false
        selectedMemesList.clear()
        applyStatusBarColor(R.color.status_bar_color)
    }

    private fun applyActionModeTitle() {
        when (selectedMemesList.size) {
            0 -> {
                mActionMode.finish()
                multiSelection = false
            }

            1 -> {
                mActionMode.title = "${selectedMemesList.size} item selected"
            }

            else -> {
                mActionMode.title = "${selectedMemesList.size} items selected"
            }
        }
    }


    // Show snackbar message on successful deletion
    private fun showSnackBar(message: String) {
        Toast.makeText(
            requireActivity.applicationContext,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    // Set list of favourites to favouritesList livedata
    fun setData(newFavouritesList: List<FavouritesEntity>) {
        val memesDiffUtil = MemesDiffUtil(favouriteMemesList, newFavouritesList)
        val diffUtilResult = DiffUtil.calculateDiff(memesDiffUtil)
        favouriteMemesList = newFavouritesList
        Log.i("FavouritesAdapter", "setData: $newFavouritesList")
        diffUtilResult.dispatchUpdatesTo(this)
    }

    fun clearContextualActionMode() {
        if (this::mActionMode.isInitialized) {
            mActionMode.finish()
        }
    }

}