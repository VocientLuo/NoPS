package com.xluo.pen.core

/**
 * 传统笔无法改变透明度
 */
abstract class TraditionPen(id: Int): BasePen(id) {
    init {
        alphaEnabled = false
    }
}