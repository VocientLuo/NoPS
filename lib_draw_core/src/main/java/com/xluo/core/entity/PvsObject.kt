package com.xluo.core.entity

/**
 * Create by xl
 * 图片处理相关实体的顶级父类
 */
open class PvsObject : Any() {

    /**
     * 对象的唯一id  时间戳标识
     */
    var objectId: Long = System.nanoTime()

}