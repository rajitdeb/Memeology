package com.rajit.memeology.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rajit.memeology.models.Meme
import com.rajit.memeology.utils.Constants

@Entity(tableName = Constants.FAVOURITES_TABLE_NAME)
class FavouritesEntity(
    var meme: Meme
){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}