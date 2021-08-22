package com.rajit.memeology.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.*
import com.rajit.memeology.data.Repository
import com.rajit.memeology.data.local.entities.FavouritesEntity
import com.rajit.memeology.models.Meme
import com.rajit.memeology.models.MultiMeme
import com.rajit.memeology.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    /** ROOM **/

    var favouritesResponse: LiveData<List<FavouritesEntity>> =
        repository.local.getAllFavourites().asLiveData()

    fun saveMemes(favouritesEntity: FavouritesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.saveMemes(favouritesEntity)
        }

    fun deleteMeme(favouritesEntity: FavouritesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteMeme(favouritesEntity)
        }

    fun deleteAllFavourites() = viewModelScope.launch(Dispatchers.IO) {
        repository.local.deleteAllFavourites()
    }

    /** RETROFIT */
    var randomMemeResponse: MutableLiveData<NetworkResult<Meme>> = MutableLiveData()

    var searchMemesResponse: MutableLiveData<NetworkResult<MultiMeme>> = MutableLiveData()

    fun getARandomMeme() = viewModelScope.launch {
        getARandomMemeSafeCall()
    }

    fun searchMemes(subReddit: String) = viewModelScope.launch {
        searchMemesSafeCall(subReddit)
    }

    private suspend fun getARandomMemeSafeCall() {
        randomMemeResponse.value = NetworkResult.Loading()

        if(hasInternetConnection()){
            try {

                val response = repository.remote.getARandomMeme()
                randomMemeResponse.value = handleMemesResponse(response)

            } catch (e: Exception) {
                randomMemeResponse.value = NetworkResult.Error(e.message)
            }
        }else {
            randomMemeResponse.value = NetworkResult.Error("No internet connection")
        }

    }

    private suspend fun searchMemesSafeCall(subReddit: String) {
        Log.i("MainViewModel", "MainViewModel: searchMemesSafeCall: $subReddit")
        searchMemesResponse.value = NetworkResult.Loading()

        if(hasInternetConnection()){
            try {

                val response = repository.remote.searchMemes(subReddit)
                searchMemesResponse.value = handleMultiMemesResponse(response)

            } catch (e: Exception) {
                searchMemesResponse.value = NetworkResult.Error(e.message)
            }
        }else {
            searchMemesResponse.value = NetworkResult.Error("No internet connection")
        }
    }

    private fun handleMemesResponse(response: Response<Meme>): NetworkResult<Meme> {
        return when {
            response.message().contains("timeout") -> {
                NetworkResult.Error("Network Timeout")
            }

            response.isSuccessful -> {
                val randomMeme = response.body()
                NetworkResult.Success(randomMeme!!)
            }

            else -> {
                NetworkResult.Error(response.message())
            }
        }

    }

    private fun handleMultiMemesResponse(response: Response<MultiMeme>): NetworkResult<MultiMeme> {
        return when {
            response.message().contains("timeout") -> {
                NetworkResult.Error("Network Timeout")
            }

            response.code() == 400 -> {
                NetworkResult.Error("No memes found with the search query")
            }

            response.isSuccessful -> {
                val randomMeme = response.body()
                NetworkResult.Success(randomMeme!!)
            }

            else -> {
                NetworkResult.Error(response.message())
            }
        }

    }

    // code for checking internet connectivity
    fun hasInternetConnection(): Boolean {
        val connectivityManager =
            getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE)
                    as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

}