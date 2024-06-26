package com.xluo.core.entity

import android.graphics.*
import com.xluo.pen.core.BasePen

/**
 * Create by xl
 * Time:2022/2/16
 * 图层基类，定义所有类型图层通用的属性 方法等
 */
abstract class PvsLayer : PvsObject() {

    var pos = RectF()


    //图层操作的matrix
    var matrix = Matrix()

    /**
     * 图层镜像值
     */
    var mirrorPointF = PointF(1f, 1f)


    abstract fun type(): Int

    abstract fun copy(): PvsLayer?

    var paintPathList: ArrayList<BasePen> = arrayListOf()

    /**
     * 图层透明度
     */
    var alpha: Int = 255

    /**
     * 图层名称
     */
    var name: String = ""

    /**
     * 图层当前的层级
     */
    var layerLevel: Int = -1

    /**
     * 当前图层是否展示出来
     */
    var isShow = true

    /**
     * 是否为选中图层
     */
    var isSelected = false

    /**
     * 旋转角度
     */
    var rotation = 0f

    /**
     * 宽度缩放倍数
     */
    var scaleX = 1f

    /**
     * 高度缩放倍数
     */
    var scaleY = 1f

    /**
     * 水平移动距离 px
     */
    var transX = 0f

    /**
     * 垂直移动距离 px
     */
    var transY = 0f


    var bitmap: Bitmap? = null

    /**
     * 是否锁定画布 不让编辑
     */
    var isLocked = false

    /**
     * 默认方式添加到页面时的宽度
     */
    var originWidth = 1

    /**
     * 默认方式添加到页面时的高度
     */
    var originHeight = 1

    /**
     * 替换类型图片的uri
     */
    var uri: String? = null

}