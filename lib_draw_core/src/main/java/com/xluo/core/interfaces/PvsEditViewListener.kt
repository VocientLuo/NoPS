package com.xluo.core.interfaces

import com.xluo.core.entity.PvsLayer


interface PvsEditViewListener {


    /**
     * 点击了某个图层
     * @param pvsLayer PvsLayer
     */
    fun onClickLayer(pvsLayer: PvsLayer)

}