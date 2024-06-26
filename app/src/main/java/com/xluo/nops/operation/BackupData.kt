package com.xluo.nops.operation

import android.graphics.RectF
import com.xluo.core.entity.PvsBackgroundLayer
import com.xluo.core.entity.PvsLayer

data class BackupData(
    val layerList: ArrayList<PvsLayer>,
    val bgLayer: PvsBackgroundLayer,
    val pvsLayer: PvsLayer,
    val oldPvsLayer: PvsLayer,
    val reviseRect: RectF,
    val type: Int,
    val stackPre: ArrayDeque<Operation>,
    val stackNext: ArrayDeque<Operation>,
    )
