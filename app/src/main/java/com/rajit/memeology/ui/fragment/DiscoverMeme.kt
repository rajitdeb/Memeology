package com.rajit.memeology.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.rajit.memeology.R
import com.rajit.memeology.adapters.DiscoverMemesAdapter
import com.rajit.memeology.databinding.FragmentDiscoverMemeBinding
import com.rajit.memeology.utils.NetworkResult
import com.rajit.memeology.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DiscoverMeme : Fragment(), SearchView.OnQueryTextListener {

    private var _binding: FragmentDiscoverMemeBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel by viewModels<MainViewModel>()

    private val mAdapter: DiscoverMemesAdapter by lazy {
        DiscoverMemesAdapter(mainViewModel)
    }

    private var searchView: SearchView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDiscoverMemeBinding.inflate(inflater, container, false)

        binding.discoverMemeRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
            setHasFixedSize(true)
        }

        mainViewModel.searchMemesResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Success -> {
                    mAdapter.setData(result.data?.memes!!)
                    hideLoadingView()
                }

                is NetworkResult.Error -> {
                    hideLoadingView()
                    if (!mainViewModel.hasInternetConnection()) {
                        showNoInternetViews()
                    }
                    if (!(result.message!!.contains("No internet"))) {
                        showCustomErrorMessage(result.message.toString())
                    }
                }

                is NetworkResult.Loading -> {
                    showLoadingView()
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.discover_menu, menu)

                val search = menu.findItem(R.id.searchByCategory)
                searchView = search.actionView as? SearchView
                searchView?.isSubmitButtonEnabled = true
                searchView?.setOnQueryTextListener(this@DiscoverMeme)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                TODO("Not yet implemented")
            }
        }, viewLifecycleOwner)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onQueryTextSubmit(searchQuery: String?): Boolean {
        if(searchQuery != null){
            searchApiData(searchQuery)
            searchView?.clearFocus()
        }
        return true
    }

    private fun searchApiData(searchQuery: String) {
        Toast.makeText(requireContext(), "Search Query: $searchQuery", Toast.LENGTH_SHORT).show()
        mainViewModel.searchMemes(searchQuery)
    }

    override fun onQueryTextChange(searchQuery: String?): Boolean {
        return true
    }

    private fun showLoadingView() {
        binding.progressBar.visibility = View.VISIBLE
        binding.discoverMemeRv.visibility = View.INVISIBLE

        binding.noInternetCardView.visibility = View.INVISIBLE
    }

    private fun hideLoadingView() {
        binding.progressBar.visibility = View.INVISIBLE
        binding.discoverMemeRv.visibility = View.VISIBLE

        binding.noInternetCardView.visibility = View.INVISIBLE
    }

    private fun showNoInternetViews() {
        binding.discoverMemeRv.visibility = View.INVISIBLE
        binding.noInternetCardView.visibility = View.VISIBLE
    }

    private fun showCustomErrorMessage(message: String) {
        binding.discoverMemeRv.visibility = View.INVISIBLE
        binding.noInternetMessageTv.text = message
        binding.noInternetCardView.visibility = View.VISIBLE
    }
}