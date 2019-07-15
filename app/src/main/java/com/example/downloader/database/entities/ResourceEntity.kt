package com.example.downloader.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "resources",
    indices = [Index(value = ["resource_url"], unique = true)]
)
data class ResourceEntity(
    @PrimaryKey @ColumnInfo(name = "id") override val id: Int,
    @ColumnInfo(name = "resource_url") val resourceUrl: String,
    @ColumnInfo(name = "resource_file") val resourceFilePath: String,
    @ColumnInfo(name = "resource_md5") val resourceMd5: String
): KKEntity<Int>