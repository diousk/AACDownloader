package com.example.downloader.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.downloader.database.entities.KKEntity

@Entity(
    tableName = "device_apps",
    indices = [Index(value = ["package_name"], unique = true)]
)
data class AppEntity(
    @PrimaryKey @ColumnInfo(name = "id") override val id: String,
    @ColumnInfo(name = "package_name") val pkgName: String,
    @ColumnInfo(name = "app_category") val category: String,
    @ColumnInfo(name = "app_name") val name: String,
    @ColumnInfo(name = "icon_path") val iconFilePath: String,
    @ColumnInfo(name = "update_timestamp") val timestamp: Long? = null
) : KKEntity<String>