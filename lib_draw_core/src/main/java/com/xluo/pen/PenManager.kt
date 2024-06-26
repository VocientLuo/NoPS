package com.xluo.pen

import android.annotation.SuppressLint
import android.content.Context
import com.xluo.pen.high.HiPenCarbon
import com.xluo.pen.high.HiPenCarbonPencile
import com.xluo.pen.high.HiPenCircle
import com.xluo.pen.high.HiPenCircleSoft
import com.xluo.pen.high.HiPenCloth
import com.xluo.pen.high.HiPenCrayon
import com.xluo.pen.high.HiPenCrayonDark
import com.xluo.pen.high.HiPenHand
import com.xluo.pen.high.HiPenMark
import com.xluo.pen.high.HiPenPencil
import com.xluo.pen.high.HiPenPixel
import com.xluo.pen.high.HiPenSoft
import com.xluo.pen.core.BasePen
import com.xluo.core.constants.Constants
import com.xluo.draw_core.R
import com.xluo.pen.bean.PaintBean
import com.xluo.pen.high.HiPenBrush
import com.xluo.pen.high.HiPenOil
import com.xluo.pen.pic.PenMultiPic
import com.xluo.pen.pic.PicPen
import com.xluo.pen.shape.PenOval
import com.xluo.pen.shape.PenRect
import com.xluo.pen.shape.PenTriangle

@SuppressLint("StaticFieldLeak")
object PenManager {
    val paintPicList = arrayListOf(
        R.drawable.small_circle
    )

    /**
     * 只需要通过id获取笔
     */
    private val ID_PEN_MAP = mapOf<Int, Class<*>>(
        Constants.PEN_ID_CRAYON to PenCrayon::class.java,
        Constants.PEN_ID_MAOBI to PenMaobi::class.java,
        Constants.PEN_ID_STEEL to PenSteel::class.java,
        Constants.PEN_ID_ERASE to PenErase::class.java,
        Constants.PEN_ID_MARK to PenMark::class.java,
        Constants.PEN_ID_MULTI_PIC_16 to PenBorder::class.java,

        Constants.ELLIPSE to PenOval::class.java,
        Constants.RECT to PenRect::class.java,
        Constants.TRIANGLE to PenTriangle::class.java,
        Constants.CUT to PenCut::class.java,
        Constants.LEAF to PenLeaf::class.java,
        Constants.PEN_ID_HI_PENCIL to HiPenPencil::class.java,
        Constants.PEN_ID_HI_SOFT to HiPenSoft::class.java,
        Constants.PEN_ID_HI_CARBON to HiPenCarbon::class.java,
        Constants.PEN_ID_HI_CRAYON to HiPenCrayon::class.java,
        Constants.PEN_ID_HI_CRAYON_DARK to HiPenCrayonDark::class.java,
        Constants.PEN_ID_HI_CLOTH to HiPenCloth::class.java,
        Constants.PEN_ID_HI_OIL to HiPenOil::class.java,

        Constants.PEN_ID_BRUSH to HiPenBrush::class.java,
        Constants.PEN_ID_CHALK to PenChalk::class.java,
        Constants.PEN_ID_HI_PIXEL to HiPenPixel::class.java,
        Constants.PEN_ID_HI_HAND to HiPenHand::class.java,
        Constants.PEN_ID_HI_MARK to HiPenMark::class.java,
        Constants.PEN_ID_HI_CIRCLE to HiPenCircle::class.java,
        Constants.PEN_ID_HI_CIRCLE_SOFT to HiPenCircleSoft::class.java,
        Constants.PEN_ID_HI_CARBON_PENCILE to HiPenCarbonPencile::class.java,
    )

    val penCache = HashMap<Int, BasePen>()

    lateinit var context: Context

    fun init(c: Context) {
        context = c
    }

    fun getPen(paintBean: PaintBean, useCache: Boolean = true): BasePen {

        val pen = if (useCache) {
            getPenById(paintBean.id)
        } else {
            getPen(paintBean.id)
        }
        if (pen is PicPen) {
            // pen.setResIds(getPicResListByPenId(paintBean.id))
        }
        return pen
    }

    private fun getPenById(id: Int): BasePen {
        // 缓存笔
        var pen = penCache[id]
        if (pen == null) {
            pen = getPen(id)
            penCache[id] = pen
        }
        return pen
    }

    private fun getPen(id: Int): BasePen {
        var pen: BasePen? = null
        val target = ID_PEN_MAP[id]
        if (target != null) {
            return getPen(id, target)
        }
        // 1-15：多图片画笔， 17-25单图片，不可调色
        if (id in Constants.PEN_ID_MULTI_PIC_1..Constants.PEN_ID_MULTI_PIC_15 ||
            id in Constants.PEN_ID_MULTI_PIC_17..Constants.PEN_ID_MULTI_PIC_25) {
            return getPen(id, PenMultiPic::class.java)
        }
        // 单独处理部分画笔类
        when (id) {
            // 图案画笔，可调色
            Constants.PEN_ID_SEA_STAR, Constants.PEN_ID_HEART,
            Constants.PEN_ID_STAR, Constants.PEN_ID_SNOW, Constants.PEN_ID_LIANOU,
            Constants.PEN_ID_POINT, Constants.PEN_ID_STRAWBERRY, Constants.PEN_ID_GRASS -> {
                pen = PicPen(id)
            }
        }
        if (pen != null) {
            return pen
        }
        throw RuntimeException("pen id not exists,id:$id")
    }

    private fun getPen(id: Int, cls: Class<*>): BasePen {
        val constructor = cls.constructors.first()
        return constructor.newInstance(id) as BasePen
    }
}