package com.xluo.nops.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.xluo.nops.db.dao.DraftDao
import com.xluo.nops.db.entity.DraftEntity
import com.xluo.lib_base.BaseApplication

@Database(entities = [DraftEntity::class], version = 1, exportSchema = false)
abstract class DraftDB: RoomDatabase() {

    abstract fun draftDao(): DraftDao

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            Room.databaseBuilder(
                BaseApplication.instance.applicationContext,
                DraftDB::class.java,
                "draft.db"
            ).allowMainThreadQueries().build()
        }
    }
}