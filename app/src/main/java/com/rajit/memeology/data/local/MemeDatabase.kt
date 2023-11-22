package com.rajit.memeology.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rajit.memeology.data.local.dao.MemesDao
import com.rajit.memeology.data.local.entities.FavouritesEntity
import com.rajit.memeology.utils.Constants.Companion.DATABASE_NAME

@Database(
    entities = [FavouritesEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(MemesTypeConverter::class)
abstract class MemeDatabase : RoomDatabase() {

    abstract fun memesDao(): MemesDao

    /**
     * NOTE:
     * If DI wasn't used in this project, then the Thread-safety and Singleton Instance creation
     * would have been done in this way
     */
//    companion object {
//
//        /*
//         * Volatile annotation is used to let all the threads know immediately about this instance
//         * in a multi-threaded environment
//         */
//        @Volatile
//        private var instance: MemeDatabase? = null
//        private var LOCK = Any()
//
//        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
//            instance ?: createDatabase(context).also {
//                instance = it
//            }
//        }
//
//        private fun createDatabase(context: Context) =
//            Room.databaseBuilder(
//                context.applicationContext,
//                MemeDatabase::class.java,
//                DATABASE_NAME
//            ).build()
//
//    }

}