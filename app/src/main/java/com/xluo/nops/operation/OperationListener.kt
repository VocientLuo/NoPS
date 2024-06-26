package com.xluo.nops.operation

import com.xluo.core.entity.PvsBackgroundLayer

interface OperationListener {

    fun onUpdateBackground(pvsBackgroundLayer: PvsBackgroundLayer)

    /**
     * 所有操作之后调用 用于判断撤销重做是否可用
     */
    fun onDataChange()

}