package com.xluo.nops.ui.home

import androidx.lifecycle.MutableLiveData
import com.xluo.lib_base.ui.BaseViewModel
import com.xluo.nops.db.DraftDB
import com.xluo.nops.db.entity.DraftEntity
import kotlinx.coroutines.launch

class HomeViewModel : BaseViewModel() {

    val draftListData = MutableLiveData<List<DraftEntity>>()

    fun loadDrafts() {
        launch {
            var draftList = DraftDB.instance.draftDao().queryAll()
            if (draftList == null) {
                draftList = arrayListOf()
            }
            draftListData.postValue(draftList)
        }
    }

    fun deleteItem(entity: DraftEntity) {
        launch {
            DraftDB.instance.draftDao().delete(entity)
            loadDrafts()
        }
    }
}