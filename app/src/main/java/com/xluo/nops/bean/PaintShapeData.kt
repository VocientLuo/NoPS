package com.xluo.nops.bean

import android.graphics.Color
import android.widget.ImageView
import android.widget.TextView
import com.xluo.nops.R

data class IconStateList(val normal: Int, val selected: Int, var isSelect: Boolean)

data class MenuState(
    val normal: Int,
    val selected: Int,
    val color: Int,
    val selectColor: Int,
    var isSelect: Boolean
)

val menuPaintIcon = MenuState(
    R.mipmap.icon_paint_normal,
    R.mipmap.draw_aa_icon_huabi,
    Color.parseColor("#676767"),
    Color.parseColor("#ffffff"),
    false
)

val menuColorIcon = MenuState(
    R.mipmap.draw_aa_icon_secai_pre,
    R.mipmap.draw_aa_icon_secai,
    Color.parseColor("#676767"),
    Color.parseColor("#ffffff"),
    false
)

val menuEraseIcon = MenuState(
    R.mipmap.draw_aa_icon_xiangpi_pre,
    R.mipmap.draw_aa_icon_xiangpi,
    Color.parseColor("#676767"),
    Color.parseColor("#ffffff"),
    false
)

val menuDaubIcon = MenuState(
    R.drawable.aa_icon_tumo_pre,
    R.drawable.aa_icon_tumo,
    Color.parseColor("#676767"),
    Color.parseColor("#ffffff"),
    false
)

val bottomMenuList = listOf(
    menuPaintIcon, menuEraseIcon, menuColorIcon, menuDaubIcon
)

fun TextView.setMenuState(menuState: MenuState) {
    if (menuState.isSelect) {
        setTextColor(menuState.selectColor)
    } else {
        setTextColor(menuState.color)
    }
}

fun ImageView.setMenuState(menuState: MenuState) {
    if (menuState.isSelect) {
        setImageResource(menuState.selected)
    } else {
        setImageResource(menuState.normal)
    }
}

fun ImageView.setIconStateList(icon: IconStateList) {
    if (icon.isSelect) {
        setImageResource(icon.selected)
    } else {
        setImageResource(icon.normal)
    }
}
