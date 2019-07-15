package com.example.downloader.di

import android.content.Context
import androidx.room.Room
import com.example.downloader.database.KingKongDatabase
import com.example.downloader.database.daos.ResourceDao
import com.example.downloader.database.daos.AppDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(context: Context): KingKongDatabase {
        val builder = Room.databaseBuilder(context, KingKongDatabase::class.java, "kingkong.db")
            .fallbackToDestructiveMigration()
        return builder.build()
    }

    @Provides
    fun provideAppDao(db: KingKongDatabase): AppDao = db.appDao()

    @Provides
    fun provideResourceDao(db: KingKongDatabase): ResourceDao = db.resourceDao()
}