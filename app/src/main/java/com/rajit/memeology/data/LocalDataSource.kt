package com.rajit.memeology.data

import android.util.Log
import com.rajit.memeology.data.local.dao.MemesDao
import com.rajit.memeology.data.local.entities.FavouritesEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "LocalDataSource"
class LocalDataSource @Inject constructor(
    private val memesDao: MemesDao
) {

    suspend fun saveMemes(favouritesEntity: FavouritesEntity) {
        memesDao.saveMeme(favouritesEntity)
    }

    suspend fun deleteMeme(favouritesEntity: FavouritesEntity) {
        memesDao.deleteMeme(favouritesEntity)
    }

    suspend fun deleteAllFavourites() {
        memesDao.deleteAllFavourites()
    }

    fun getAllFavourites(): Flow<List<FavouritesEntity>> {
        return memesDao.getAllFavourites()
    }

}