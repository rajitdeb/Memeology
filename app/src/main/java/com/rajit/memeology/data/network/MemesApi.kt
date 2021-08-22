package com.rajit.memeology.data.network

import com.rajit.memeology.models.Meme
import com.rajit.memeology.models.MultiMeme
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface MemesApi {

    @GET("/gimme")
    suspend fun getARandomMeme() : Response<Meme>

    @GET("/gimme/{subReddit}/25")
    suspend fun searchMemes(
        @Path("subReddit") subReddit: String,
    ): Response<MultiMeme>

}