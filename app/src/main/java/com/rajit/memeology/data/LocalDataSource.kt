package com.rajit.memeology.data

import com.rajit.memeology.data.local.dao.MemesDao
import com.rajit.memeology.data.local.entities.FavouritesEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

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