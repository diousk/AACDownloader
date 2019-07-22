package com.example.downloader.di

import android.content.Context
import androidx.room.Room
import com.example.downloader.database.ResDatabase
import com.example.downloader.database.daos.ResourceDao
import com.example.downloader.database.daos.AppDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(context: Context): ResDatabase {
        val builder = Room.databaseBuilder(context, ResDatabase::class.java, "res.db")
            .fallbackToDestructiveMigration()
        return builder.build()
    }

    @Provides
    fun provideAppDao(db: ResDatabase): AppDao = db.appDao()

    @Provides
    fun provideResourceDao(db: ResDatabase): ResourceDao = db.resourceDao()
}