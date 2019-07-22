package com.example.downloader.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.downloader.database.daos.ResourceDao
import com.example.downloader.database.entities.ResourceEntity
import com.example.downloader.database.daos.AppDao
import com.example.downloader.database.entities.AppEntity

@Database(entities = [AppEntity::class, ResourceEntity::class], version = 1)
abstract class ResDatabase: RoomDatabase() {
    abstract fun appDao(): AppDao
    abstract fun resourceDao(): ResourceDao
}