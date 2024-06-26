package com.xluo.nops.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.xluo.nops.db.entity.DraftEntity

@Dao
interface DraftDao {

    @Query("select * from DraftEntity")
    fun queryAll(): MutableList<DraftEntity>

    @Query("select * from DraftEntity where id = :id")
    fun query(id: Long): DraftEntity?

    @Insert
    fun insert(draftEntity: DraftEntity)

    @Update
    fun update(draftEntity: DraftEntity)

    @Delete
    fun delete(draftEntity: DraftEntity)


}