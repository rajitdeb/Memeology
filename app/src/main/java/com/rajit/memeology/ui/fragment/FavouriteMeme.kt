package com.rajit.memeology.ui.fragment

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.rajit.memeology.R
import com.rajit.memeology.adapters.FavouriteMemesAdapter
import com.rajit.memeology.databinding.FragmentFavouriteMemeBinding
import com.rajit.memeology.utils.PostActions
import com.rajit.memeology.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

const val TAG = "FavouriteMemeFragment"

@AndroidEntryPoint
class FavouriteMeme : Fragment(){

    private var _binding: FragmentFavouriteMemeBinding? = null
    private val binding get() = _binding!!

    private val postActions = PostActions()

    private val mainViewModel by viewModels<MainViewModel>()
    private val mAdapter: FavouriteMemesAdapter by lazy {
        FavouriteMemesAdapter(requireActivity(), binding.coordinatorLayout, mainViewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentFavouriteMemeBinding.inflate(inflater, container, false)

        setHasOptionsMenu(true)

        binding.favouritesRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
            setHasFixedSize(true)
        }

        // Read Favourites
        mainViewModel.favouritesResponse.observe(viewLifecycleOwner, { result ->
            mAdapter.setData(result)
        })

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.favourite_meme_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.deleteAll){
            if (mainViewModel.favouritesResponse.value!!.isNotEmpty()) {
                mainViewModel.deleteAllFavourites()
                postActions.showSnackBarMessage(binding.coordinatorLayout, "Deleted all items from favourites")
            } else {
                postActions.showSnackBarMessage(binding.coordinatorLayout, "Nothing to delete")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        super.onStop()
        mAdapter.clearContextualActionMode()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}