package com.rajit.memeology.data

import com.rajit.memeology.data.network.MemesApi
import com.rajit.memeology.models.Meme
import com.rajit.memeology.models.MultiMeme
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val memesApi: MemesApi
) {

    suspend fun getARandomMeme(): Response<Meme>{
        return memesApi.getARandomMeme()
    }

    suspend fun searchMemes(subReddit: String): Response<MultiMeme> {
        return memesApi.searchMemes(subReddit)
    }

}