package com.xluo.nops.bean

data class HistoryColor(
    var historyColor: Int,
    val createTime: Long = System.currentTimeMillis(),
    var modifyTime: Long = System.currentTimeMillis(),
)
