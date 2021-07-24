package com.singularitycoder.bottomsheet

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

fun TextView.showHideIcon(
    context: Context,
    showTick: Boolean,
    @DrawableRes icon1: Int = android.R.drawable.ic_delete,
    @DrawableRes icon2: Int = android.R.drawable.ic_delete,
    @DrawableRes icon3: Int = android.R.drawable.ic_delete,
    @DrawableRes icon4: Int = android.R.drawable.ic_delete,
    @ColorRes iconColor1: Int = android.R.color.white,
    @ColorRes iconColor2: Int = android.R.color.white,
    @ColorRes iconColor3: Int = android.R.color.white,
    @ColorRes iconColor4: Int = android.R.color.white,
    direction: Int
) {
    val left = 1
    val top = 2
    val right = 3
    val bottom = 4
    val leftRight = 5
    val topBottom = 6

    val drawable1 = ContextCompat.getDrawable(context, icon1)?.changeColor(context = context, color = iconColor1)
    val drawable2 = ContextCompat.getDrawable(context, icon2)?.changeColor(context = context, color = iconColor2)
    val drawable3 = ContextCompat.getDrawable(context, icon3)?.changeColor(context = context, color = iconColor3)
    val drawable4 = ContextCompat.getDrawable(context, icon4)?.changeColor(context = context, color = iconColor4)

    if (showTick) {
        when (direction) {
            left -> this.setCompoundDrawablesWithIntrinsicBounds(drawable1, null, null, null)
            top -> this.setCompoundDrawablesWithIntrinsicBounds(null, drawable2, null, null)
            right -> this.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable3, null)
            bottom -> this.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable4)
            leftRight -> this.setCompoundDrawablesWithIntrinsicBounds(drawable1, null, drawable3, null)
            topBottom -> this.setCompoundDrawablesWithIntrinsicBounds(null, drawable2, null, drawable4)
        }
    } else this.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
}

private fun Drawable.changeColor(
    context: Context,
    @ColorRes color: Int
): Drawable {
    val unwrappedDrawable = this
    val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable)
    DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(context, color))
    return this
}

fun showSnackBar(
    view: View,
    message: String,
    anchorView: View? = null,
    duration: Int = Snackbar.LENGTH_SHORT
) {
    Snackbar.make(view, message, duration).apply {
        this.animationMode = BaseTransientBottomBar.ANIMATION_MODE_SLIDE
        if (null != anchorView) this.anchorView = anchorView
        this.show()
    }
}