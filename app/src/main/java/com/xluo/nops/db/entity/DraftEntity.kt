package com.xluo.nops.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class DraftEntity {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L

    @ColumnInfo
    var name: String = ""

    @ColumnInfo
    var coverPath: String = ""

    @ColumnInfo
    var jsonData: String = ""

    @ColumnInfo
    var createTime: String = ""

    @ColumnInfo
    var modifyTime: Long = 0

    @ColumnInfo
    var draftType: String = ""

    var isSelected = false
}