package com.xluo.pen.core

open class PenObject: Any() {

    /**
     * 对象的唯一id  时间戳标识
     */
    val objectId: Long = System.nanoTime()
}