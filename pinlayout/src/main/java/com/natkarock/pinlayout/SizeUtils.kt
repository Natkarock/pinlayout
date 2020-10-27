package com.natkarock.pinlayout

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.TypedValue

object SizeUtils {
    fun dp2px(context: Context, dpValue: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dpValue,
            context.getResources().getDisplayMetrics()
        ).toInt()
    }

    fun sp2px(context: Context, spValue: Float): Float {
        return (spValue * context.resources.displayMetrics.scaledDensity + 0.5f)
    }

    fun px2sp(context: Context, pxValue: Float): Float {
        return (pxValue / context.resources.displayMetrics.scaledDensity + 0.5f)
    }

    fun pxToDp(context: Context?, px: Float): Float {
        return if (context != null) {
            val resources = context.resources
            val metrics = resources.displayMetrics
            px / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        } else {
            val metrics = Resources.getSystem().displayMetrics
            px / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        }
    }
}