package com.rajit.memeology.ui.fragment

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.rajit.memeology.data.local.entities.FavouritesEntity
import com.rajit.memeology.databinding.FragmentRandomMemeBinding
import com.rajit.memeology.models.Meme
import com.rajit.memeology.utils.CustomTab
import com.rajit.memeology.utils.DownloadUtil
import com.rajit.memeology.utils.NetworkResult
import com.rajit.memeology.utils.PermissionUtil
import com.rajit.memeology.utils.PostActions
import com.rajit.memeology.utils.sdk29AndUp
import com.rajit.memeology.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RandomMeme : Fragment(), SensorEventListener {

    private var _binding: FragmentRandomMemeBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel by viewModels<MainViewModel>()

    private var postActions = PostActions()

    private lateinit var mSensorManager: SensorManager
    private var mSensor: Sensor? = null

    private val TAG = "RandomMemeActivity"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRandomMemeBinding.inflate(layoutInflater, container, false)

        if (Build.VERSION.SDK_INT < 29) {
            PermissionUtil.checkForStoragePermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }

        mSensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        mainViewModel.getARandomMeme()

        mainViewModel.getRandomMemeResponse().observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Success -> {
                    Log.i(TAG, "Response: ${result.data}")
                    result.data?.let {
                        binding.memeData = it
                        val memeData = it
                        hideLoadingView()

                        // setup listeners
                        binding.itemDownload
                            .setOnClickListener {

                                val memeFileName = memeData.url.subSequence(
                                    memeData.url.lastIndexOf("/") + 1, memeData.url.lastIndex + 1
                                )

                                sdk29AndUp {
                                    downloadMeme(memeFileName.toString(), memeData.url)
                                } ?: if (ContextCompat.checkSelfPermission(
                                        requireContext(),
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                    ) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    downloadMeme(memeFileName.toString(), memeData.url)
                                } else {
                                    PermissionUtil.checkForStoragePermission(
                                        requireContext(),
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                    )
                                }
                            }

                        binding.itemShare
                            .setOnClickListener {
                                val shareText =
                                    getString(
                                        com.rajit.memeology.R.string.share_text,
                                        memeData.postLink,
                                        memeData.url
                                    )
                                postActions.shareMeme(requireContext(), shareText)
                            }


                        lifecycleScope.launch {

                            mainViewModel.getFavorites().observe(viewLifecycleOwner) { favouritesList ->
                                for (meme in favouritesList) {
                                    if (memeData.url == meme.meme.url) {
                                        binding.itemFavourites.setImageResource(com.rajit.memeology.R.drawable.ic_bookmark)
                                    } else {
                                        binding.itemFavourites.setImageResource(com.rajit.memeology.R.drawable.ic_bookmark_temp)
                                    }
                                }
                            }
                        }

                        binding.itemFavourites.setOnClickListener {
                            saveToFavourites(memeData)
                        }

                        binding.itemLinkToPost.setOnClickListener {
                            val postUrl = memeData.postLink

                            // Load URL on Custom Tab
                            CustomTab.loadURL(requireContext(), postUrl)

                        }

                        binding.nextBtn.setOnClickListener {
                            mainViewModel.getARandomMeme()
                        }
                    }
                }

                is NetworkResult.Error -> {
                    postActions.showSnackBarMessage(
                        binding.coordinatorLayout,
                        result.message.toString()
                    )

                    hideLoadingView()

                    if (!mainViewModel.hasInternetConnection()) {
                        showNoInternetViews()
                    } else {
                        binding.memeLayout.visibility = View.INVISIBLE
                        binding.shakeToNextTv.visibility = View.INVISIBLE
                        binding.nextBtn.visibility = View.INVISIBLE
                    }

                    Log.e(TAG, "Serious Error: ${result.message}")

                }

                is NetworkResult.Loading -> showLoadingView()
            }
        }

        return binding.root
    }

    private fun showLoadingView() {
        binding.progressBar.visibility = View.VISIBLE
        binding.memeLayout.visibility = View.INVISIBLE
        binding.nextBtn.visibility = View.INVISIBLE
        binding.shakeToNextTv.visibility = View.INVISIBLE
    }

    private fun hideLoadingView() {
        binding.progressBar.visibility = View.INVISIBLE
        binding.memeLayout.visibility = View.VISIBLE
        binding.nextBtn.visibility = View.VISIBLE
        binding.shakeToNextTv.visibility = View.VISIBLE
    }

    private fun saveToFavourites(memeObj: Meme) {

        val favouritesEntity = FavouritesEntity(memeObj)

        mainViewModel.saveMemes(favouritesEntity)
        binding.itemFavourites.setImageResource(com.rajit.memeology.R.drawable.ic_bookmark)
        postActions.showSnackBarMessage(binding.coordinatorLayout, "Saved to favourites")

    }

    private fun downloadMeme(memeFileName: String, memeUrl: String) {
        DownloadUtil.downloadMeme(
            requireContext(),
            memeFileName,
            binding.itemMemeImg.drawable,
            memeUrl
        )

        postActions.downloadMeme(binding.coordinatorLayout, mainViewModel.hasInternetConnection())
    }

    private fun showNoInternetViews() {
        binding.memeLayout.visibility = View.INVISIBLE
        binding.shakeToNextTv.visibility = View.INVISIBLE
        binding.nextBtn.visibility = View.INVISIBLE

        binding.noInternetCardView.visibility = View.VISIBLE
    }

    private fun detectShakeAndChangeToNextMeme(event: SensorEvent?) {
        if (event != null) {

            // provides a value for the acceleration / shake in x axis
            val x = event.values[0]
            
            if (x > 8f && x < 10f) {
                mainViewModel.getARandomMeme()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(mainViewModel.hasInternetConnection()) {
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        mSensorManager.unregisterListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(mainViewModel.hasInternetConnection()){
            detectShakeAndChangeToNextMeme(event)
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) = Unit
}