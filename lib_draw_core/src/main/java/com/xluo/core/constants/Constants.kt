package com.xluo.core.constants

import com.xluo.core.utils.dp

/**
 * Create by xl
 */
object Constants {

    /**
     * 图片类型图层
     */
    const val LAYER_TYPE_IMAGE = 1

    /**
     * 文字图层
     */
    const val LAYER_TYPE_TEXT = 2

    /**
     * 形状图层
     */
    const val LAYER_TYPE_SHAPE = 4

    /**
     * 背景图层
     */
    const val LAYER_TYPE_BG = 3

    /**
     * 图片、文本混合图层
     */
    const val LAYER_TYPE_RICH = 5

    /**
     * 绘制图层
     */
    const val LAYER_TYPE_PATH = 6

    /**
     * 裁减图层
     */
    const val LAYER_TYPE_CUT = 7

    /**
     * 画布背景类型  表示纯色背景
     */
    const val BG_TYPE_COLOR = 1

    /**
     * 画布背景类型  表示图片背景
     */
    const val BG_TYPE_IMAGE = 2


    /**
     * 画布背景类型  表示无背景 透明背景颜色
     */
    const val BG_TYPE_NONE = 3

    /**
     * 画布背景类型  表示渐变纯色背景
     */
    const val BG_TYPE_JIANBIAN_COLOR = 4


    //居左对齐
    const val TEXT_ALIGN_LEFT = 0

    const val TEXT_ALIGN_CENTER = 1

    const val TEXT_ALIGN_RIGHT = 2

    //默认文字大小为14sp
    const val TEXT_DEFAULT_SIZE = 16f

    //文字上下左右边距都加入14个dp值
    val TEXT_DEFAULT_MARGIN = 10.dp

    //文字距离view的边距都加入20个dp值
    val TEXT_DEFAULT_BOLDER_MARGIN = 20.dp

    /**
     * 文字字符的间距 默认为0f  范围值0 -5f
     */
    const val TEXT_LETTER_SPACE = 0f

    /**
     * 文字字符的行距 默认为0f  范围值0 -5f
     */
    const val TEXT_LINE_SPACE = 1.2f

    /**
     * 文字字符的间距 默认为0f  范围值0 -3f
     */
    const val TEXT_LETTER_SPACE_MAX = 3f

    /**
     * 文字字符的间距 默认为0f  范围值0 -3f
     */
    const val TEXT_LINE_SPACE_MAX = 3f


    /**
     * 圆环边框的宽度
     */
    const val RING_WIDTH = 50f



    const val DEFAULT_ERASE_SIZE = 80
    const val DEFAULT_PEN_SIZE = 20
    const val MAX_PEN_SIZE = 200

    /**-----------------------------------
     * 画笔
     * 线性笔
     *
     */
    const val PEN_ID_BOARD = 0

    /**
     * 蜡笔
     */
    const val PEN_ID_CRAYON = 1
    // 毛笔
    const val PEN_ID_MAOBI = 2
    const val PEN_ID_STEEL = 3
    // 水彩笔
    const val PEN_ID_BRUSH = 4

    // 图案画笔
    const val PEN_ID_SEA_STAR = 5
    const val PEN_ID_HEART = 6
    const val PEN_ID_STAR = 7
    const val PEN_ID_SNOW = 8
    // 莲藕
    const val PEN_ID_LIANOU = 9
    // 点点
    const val PEN_ID_POINT = 10
    // 草莓
    const val PEN_ID_STRAWBERRY = 11
    // 小草
    const val PEN_ID_GRASS = 12


    // 马克笔
    const val PEN_ID_MARK = 13
    // 粉笔
    const val PEN_ID_CHALK = 14

    // 铅笔
    const val PENCIL = 15

    // 柔边笔
    const val SOFT_EDGE_PEN = 16

    // 圆珠笔
    const val BALL_POINT_PEN = 17

    // 传统铅笔
    const val TRADITIONAL_PENCIL = 18

    // 粗糙铅笔
    const val ROUGH_PENCIL = 19

    //碳铅笔
    const val CARBON_PENCIL = 20

    //丝纱
    const val GAUZE_PENCIL = 21

    //滚筒
    const val ROLLER_PENCIL = 22

    //中性笔
    const val NEUTRAL_PENCIL = 23

    // 多图片画笔
    const val PEN_ID_MULTI_PIC_1 = 101
    const val PEN_ID_MULTI_PIC_2 = 102
    const val PEN_ID_MULTI_PIC_3 = 103
    const val PEN_ID_MULTI_PIC_4 = 104
    const val PEN_ID_MULTI_PIC_5 = 105
    const val PEN_ID_MULTI_PIC_6 = 106
    const val PEN_ID_MULTI_PIC_7 = 107
    const val PEN_ID_MULTI_PIC_8 = 108
    const val PEN_ID_MULTI_PIC_9 = 109
    const val PEN_ID_MULTI_PIC_10 = 110
    const val PEN_ID_MULTI_PIC_11 = 111
    const val PEN_ID_MULTI_PIC_12 = 112
    const val PEN_ID_MULTI_PIC_13 = 113
    const val PEN_ID_MULTI_PIC_14 = 114
    const val PEN_ID_MULTI_PIC_15 = 115
    // 这三个不是图案画笔
    const val PEN_ID_MULTI_PIC_16 = 116
    const val PEN_ID_MULTI_PIC_17 = 117
    const val PEN_ID_MULTI_PIC_18 = 118

    const val PEN_ID_MULTI_PIC_19 = 119
    const val PEN_ID_MULTI_PIC_20 = 120
    const val PEN_ID_MULTI_PIC_21 = 121
    const val PEN_ID_MULTI_PIC_22 = 122
    const val PEN_ID_MULTI_PIC_23 = 123
    const val PEN_ID_MULTI_PIC_24 = 124
    const val PEN_ID_MULTI_PIC_25 = 125

    // 形状画笔
    /**
     * 曲线
     */
    const val CURVE = 200

    /**
     * 直线
     */
    const val LINE = 201

    /**
     * 椭圆
     */
    const val ELLIPSE = 202

    /**
     * 矩形
     */
    const val RECT = 203

    /**
     * 多边形
     */
    const val POLYGON = 204


    /**
     * 三角形
     */
    const val TRIANGLE = 205

    /**
     * 裁剪笔
     */
    const val CUT = 206

    /**
     * 柳叶笔
     */
    const val LEAF = 207
    // 橡皮擦
    const val PEN_ID_ERASE = 208
    // 涂抹笔
    const val  PEN_ID_DAUB = 209

    //自定义画笔
    const val PEN_ID_CUSTOM = 300

    const val PEN_ID_HI_PENCIL = 1001
    const val PEN_ID_HI_SOFT = 1002
    const val PEN_ID_HI_CARBON = 1003
    const val PEN_ID_HI_CLOTH = 1004
    const val PEN_ID_HI_CRAYON = 1005
    const val PEN_ID_HI_CRAYON_DARK = 1006
    const val PEN_ID_HI_OIL = 1007
    const val PEN_ID_HI_PIXEL = 1008
    const val PEN_ID_HI_HAND = 1009
    const val PEN_ID_HI_MARK = 1010
    const val PEN_ID_HI_CIRCLE = 1011
    const val PEN_ID_HI_CIRCLE_SOFT = 1012
    const val PEN_ID_HI_CARBON_PENCILE = 1013
}