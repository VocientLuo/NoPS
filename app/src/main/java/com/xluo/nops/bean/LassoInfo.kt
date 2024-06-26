package com.xluo.nops.bean

import android.widget.ImageView
import android.widget.TextView
import com.xluo.core.utils.PvsDistortUtils
import com.xluo.core.widget.PvsCutView
import com.xluo.nops.R
import com.xluo.pen.PenLeaf

data class PopMenuBean(val name: String, val drawableBean: DrawableBean)

val fillInfo = mapOf(
    PvsCutView.FillType.ADD to PopMenuBean("增加", DrawableBean(R.mipmap.icon_xq_zj, R.mipmap.icon_xq_pre_zj, false)),
    PvsCutView.FillType.REDUCE to PopMenuBean("减去", DrawableBean(R.mipmap.icon_xq_jq, R.mipmap.icon_xq_pre_jq, false)),
)


val leafInfo = mapOf(
    PenLeaf.LeafType.FILL to DrawableBean(R.mipmap.icon_lyb_tc, R.mipmap.icon_lyb_pre_tc, false),
    PenLeaf.LeafType.CLEAN to DrawableBean(R.mipmap.icon_lyb_qc, R.mipmap.icon_lyb_pre_qc, false)
)

val lassoInfo = mapOf(
    PvsCutView.LassoType.LASSO to PopMenuBean("套索", DrawableBean(R.mipmap.icon_xq_ts,R.mipmap.icon_xq_pre_ts, false)),
    PvsCutView.LassoType.PAINT to PopMenuBean("画笔", DrawableBean(R.mipmap.icon_xq_hb,R.mipmap.icon_xq_pre_hb, false)),
    PvsCutView.LassoType.RECT to PopMenuBean("矩形", DrawableBean(R.mipmap.icon_xq_jx, R.mipmap.icon_xq_pre_jx, false)),
    PvsCutView.LassoType.OVAL to PopMenuBean("椭圆", DrawableBean(R.mipmap.icon_xq_ty, R.mipmap.icon_xq_pre_ty, false)),
    PvsCutView.LassoType.MAGIC to PopMenuBean("魔棒", DrawableBean(R.mipmap.icon_xq_mb, R.mipmap.icon_xq_pre_mb, false)),
)

val distortInfo = mapOf(
    PvsDistortUtils.DistortType.Push to PopMenuBean("推", DrawableBean(R.drawable.icon_t_yh, R.drawable.icon_t_yh_pre, false)),
    PvsDistortUtils.DistortType.Reset to PopMenuBean("恢复", DrawableBean(R.drawable.icon_hf_yh, R.drawable.icon_hf_yh_pre, false)),
    PvsDistortUtils.DistortType.LtoR to PopMenuBean("右旋转", DrawableBean(R.drawable.icon_yxz_yh, R.drawable.icon_yxz_yh_pre, false)),
    PvsDistortUtils.DistortType.RtoL to PopMenuBean("左旋转", DrawableBean(R.drawable.icon_zxz_yh, R.drawable.icon_zxz_yh_pre, false)),
    PvsDistortUtils.DistortType.Shrink to PopMenuBean("捏合", DrawableBean(R.drawable.icon_nh_yh, R.drawable.icon_nh_yh_pre, false)),
    PvsDistortUtils.DistortType.Extend to PopMenuBean("扩展", DrawableBean(R.drawable.icon_kz_yh, R.drawable.icon_kz_yh_pre, false)),
)


data class DrawableBean(val norRes: Int, val selRes: Int, var selected: Boolean)

fun TextView.updateDrawableTop(bean: DrawableBean) {
    val resId = if (bean.selected) {
        bean.selRes
    } else {
        bean.norRes
    }
    val drawable = resources.getDrawable(resId)
    drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
    setCompoundDrawables(null, drawable, null, null)
}

fun TextView.updateDrawableLeft(bean: DrawableBean) {
    val resId = if (bean.selected) {
        bean.selRes
    } else {
        bean.norRes
    }
    val drawable = resources.getDrawable(resId)
    drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
    setCompoundDrawables(drawable, null, null, null)
}

fun ImageView.updateImageDrawable(bean: DrawableBean) {
    val resId = if (bean.selected) {
        bean.selRes
    } else {
        bean.norRes
    }
    setImageResource(resId)
}
