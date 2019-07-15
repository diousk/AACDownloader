package com.example.downloader.database.daos

import androidx.room.Dao
import androidx.room.Query
import com.example.downloader.Resource
import com.example.downloader.database.entities.ResourceEntity
import io.reactivex.Completable
import io.reactivex.Single

@Dao
abstract class ResourceDao : EntityDao<ResourceEntity> {
    @Query("SELECT * FROM resources WHERE id = :resId")
    abstract fun getResourceById(resId: Int): Single<ResourceEntity>

    @Query("SELECT * FROM resources WHERE resource_url = :resourceUrl")
    abstract fun getResourceByUrl(resourceUrl: String): Single<ResourceEntity>

    @Query("SELECT * FROM resources")
    abstract fun getAllResources(): Single<List<ResourceEntity>>

    @Query("DELETE FROM resources")
    abstract fun deleteAll(): Completable
}