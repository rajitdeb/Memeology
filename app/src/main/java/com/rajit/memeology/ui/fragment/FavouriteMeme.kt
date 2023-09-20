package com.rajit.memeology.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.rajit.memeology.R
import com.rajit.memeology.adapters.FavouriteMemesAdapter
import com.rajit.memeology.databinding.FragmentFavouriteMemeBinding
import com.rajit.memeology.utils.PostActions
import com.rajit.memeology.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "FavouriteMeme"

@AndroidEntryPoint
class FavouriteMeme : Fragment() {

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

        binding.favouritesRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
            setHasFixedSize(true)
        }

        lifecycleScope.launch {
            // Read Favourites
            mainViewModel.getFavorites().observe(viewLifecycleOwner) { result ->
                mAdapter.setData(result)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.favourite_meme_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.deleteAll) {

                    lifecycleScope.launch(Dispatchers.IO) {
                        if (mainViewModel.getFavorites().value!!.isNotEmpty()) {
                            mainViewModel.deleteAllFavourites()

                            postActions.showSnackBarMessage(
                                binding.coordinatorLayout,
                                "Deleted all items from favourites"
                            )
                        } else {
                            postActions.showSnackBarMessage(
                                binding.coordinatorLayout,
                                "Nothing to delete"
                            )
                        }
                    }
                }

                return false
            }
        }, viewLifecycleOwner)
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