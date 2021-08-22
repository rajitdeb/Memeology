package com.rajit.memeology.models

data class Meme(
    val author: String,
    val postLink: String,
    val subreddit: String,
    val title: String,
    val ups: Int,
    val url: String,
    val preview: List<String>
)