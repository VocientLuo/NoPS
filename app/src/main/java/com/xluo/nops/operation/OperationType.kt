package com.xluo.nops.operation

/**
 * 操作相关的类型
 */
object OperationType {

    /**
     * 更新背景的操作
     */
    const val UPDATE_BACKGROUND_INFO: Int = 1

    const val ADD_IMAGE_LAYER: Int = 2

    const val DEL_IMAGE_LAYER: Int = 3

    const val UPDATE_IMAGE_LAYER: Int = 4

    const val ADD_TEXT_LAYER: Int = 5

    const val DEL_TEXT_LAYER: Int = 6

    const val UPDATE_TEXT_LAYER: Int = 7

    const val ADD_RICH_LAYER: Int = 8

    const val DEL_RICH_LAYER: Int = 9

    const val UPDATE_RICH_LAYER: Int = 10

    const val SORT_ALL_LAYER: Int = 11

    /**
     * 更新richLayer的操作类型
     * 普通类型，如图层的显示、隐藏，透明度修改
     */
    const val UPDATE_RICH_COMMON: Int = 101

    /**
     * 画笔操作
     */
    const val UPDATE_RICH_DRAW: Int = 102

    /**
     * 子图层的增删改操作
     */
    const val UPDATE_RICH_SUBLAYER: Int = 104

    const val UPDATE_LAYER_ATTR: Int = 105

    const val UPDATE_LAYER_VISIBLE: Int = 106

    const val UPDATE_LAYER_ALPHA: Int = 107
}