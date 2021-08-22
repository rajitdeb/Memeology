package com.rajit.memeology.data.local.dao

import androidx.room.*
import com.rajit.memeology.data.local.entities.FavouritesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MemesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMeme(favouriteMeme: FavouritesEntity)

    @Delete
    suspend fun deleteMeme(favouriteMeme: FavouritesEntity)

    @Query("DELETE FROM favourite_memes")
    suspend fun deleteAllFavourites()

    @Query("SELECT * FROM favourite_memes")
    fun getAllFavourites(): Flow<List<FavouritesEntity>>

}