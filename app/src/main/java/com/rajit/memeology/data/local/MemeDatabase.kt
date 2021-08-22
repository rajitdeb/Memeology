package com.rajit.memeology.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rajit.memeology.data.local.dao.MemesDao
import com.rajit.memeology.data.local.entities.FavouritesEntity

@Database(
    entities = [FavouritesEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(MemesTypeConverter::class)
abstract class MemeDatabase : RoomDatabase() {

    abstract fun memesDao(): MemesDao

}