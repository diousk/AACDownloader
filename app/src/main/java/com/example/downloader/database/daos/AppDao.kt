package com.example.downloader.database.daos

import androidx.room.Dao
import androidx.room.Query
import com.example.downloader.database.entities.AppEntity
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
abstract class AppDao: EntityDao<AppEntity> {
    @Query("SELECT * FROM device_apps")
    abstract fun getLocalApps(): Single<List<AppEntity>>

    @Query("SELECT * FROM device_apps WHERE app_category LIKE 'GAME%'")
    abstract fun getLocalGameApps(): Single<List<AppEntity>>

    @Query("SELECT * FROM device_apps WHERE package_name IN (:pkgs)")
    abstract fun getLocalGameApps(pkgs: List<String>): Single<List<AppEntity>>

    @Query("SELECT package_name FROM device_apps")
    abstract fun getLocalAppsPkg(): Single<List<String>>

    @Query("SELECT * FROM device_apps")
    abstract fun observeLocalGameApps(): Flowable<List<AppEntity>>

    @Query("DELETE FROM device_apps")
    abstract fun deleteAll(): Completable
}