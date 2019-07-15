package com.example.downloader.database.daos

import androidx.room.*
import com.example.downloader.database.entities.KKEntity
import io.reactivex.Completable
import io.reactivex.Single

interface EntityDao<in E : KKEntity<*>> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: E): Single<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(entities: List<E>): Single<List<Long>>

    @Update
    fun update(entity: E): Completable

    @Delete
    fun delete(entity: E): Completable
}