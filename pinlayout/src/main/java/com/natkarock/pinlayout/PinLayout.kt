package com.mobile.finiza.app.ui.view.pinView

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import com.natkarock.pinlayout.R
import com.natkarock.pinlayout.SizeUtils
import com.natkarock.pinlayout.dismissKeyboard
import java.io.FileNotFoundException
import java.lang.Exception
import java.lang.reflect.InvocationTargetException

class PinLayout : LinearLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        getAttributes(attributeSet)
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) : super(
        context,
        attributeSet,
        defStyle
    ) {
        getAttributes(attributeSet)

    }

    private var selectBackgroundAtr: Drawable? = null
        set(value) {
            field = value
            addViews()
            invalidate()
        }

    private var unselectBackgroundAtr: Drawable? = null
        set(value) {
            field = value
            addViews()
            invalidate()

        }

    private var unselectEmptyBackgroundAtr: Drawable? = null
        set(value) {
            field = value
            addViews()
            invalidate()

        }

    private var pinCount = PIN_COUNT_DEFAULT
        set(value) {
            field = value
            invalidate()
            requestLayout()
        }

    private var pinItems = mutableListOf<PinItem>()

    private val isFull
        get() = pinItems.filter { it.isFull() }.size == pinItems.size

    private var pinMargin = resources.getDimension(R.dimen.basic_pin_margin)
        set(value) {
            field = value
            addViews()
            invalidate()
            requestLayout()
        }

    private var pinFont: Typeface? = NO_FONT
        set(value) {
            field = value
            invalidate()
            requestLayout()
        }

    val pin get() = pinItems.fold("") { acc, pinItem -> acc + pinItem.text.toString() }

    var onFullListener: ((isFull: Boolean) -> Unit)? = null


    private fun getAttributes(attrs: AttributeSet) {
        context.theme.obtainStyledAttributes(attrs, R.styleable.PinLayout, 0, 0).apply {
            try {
                selectBackgroundAtr =
                    getDrawable(R.styleable.PinLayout_pin_select_background)
                unselectBackgroundAtr = getDrawable(
                    R.styleable.PinLayout_pin_unselect_background
                )
                unselectEmptyBackgroundAtr = getDrawable(
                    R.styleable.PinLayout_pin_unselect_empty_background
                )
                pinCount = getInteger(R.styleable.PinLayout_pin_count, PIN_COUNT_DEFAULT)
                if (hasValue(R.styleable.PinLayout_pin_margin)) {
                    pinMargin =
                        SizeUtils.pxToDp(
                            context,
                            getDimension(R.styleable.PinLayout_pin_margin, 0f)
                        )
                }
                try {
                    val fontDesc = getString(R.styleable.PinLayout_pin_font)
                    fontDesc?.let {
                        pinFont = Typeface.createFromAsset(context.assets, "fonts/${it}")
                    }
                } catch (e: Exception) {
                    Log.e(LOG_NAME, e.message.toString())
                }
                addViews()
            } finally {
                recycle()
            }
        }
    }

    init {
        orientation = HORIZONTAL
    }

    private fun addViews() {
        removeAllViews()
        pinItems = mutableListOf()
        for (item in 1..pinCount) {
            val pinItem = PinItem(context)
            addPin(pinItem)
            pinItems.add(pinItem)
        }
        for (index in 0..pinItems.lastIndex) {
            pinItems[index].nextFocusCallback = {
                if (index < pinItems.lastIndex) {
                    pinItems[index + 1].requestFocus()
                } else {

                    context.dismissKeyboard()
                    if (unselectBackgroundAtr != null) {
                        pinItems[index].setBackgroundDrawable(unselectBackgroundAtr)
                    }
                }
                onFullListener?.invoke(isFull)
            }
            pinItems[index].prevFocusCallback = {
                if (index > 0) {
                    pinItems[index - 1].requestFocus()
                }
                onFullListener?.invoke(isFull)
            }
            pinItems[index].onFocusCallback = {
                onFullListener?.invoke(false)
            }
        }
    }

    private fun addPin(pinItem: PinItem) {
        val pinParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1f)
        pinParams.setMargins(pinMargin.toInt(), 0, pinMargin.toInt(), 0)
        pinItem.apply {
            layoutParams = pinParams
            unselectBackground = unselectBackgroundAtr
            selectBackground = selectBackgroundAtr
            unselectEmptyBackground = unselectEmptyBackgroundAtr
            pinFont?.let { typeface = it }
        }
        addView(pinItem)
    }


    fun clear() {
        for (item in pinItems) {
            item.setText("")
        }
    }

    companion object {
        private const val PIN_COUNT_DEFAULT = 6
        private val NO_FONT = null
        private val LOG_NAME = "PinLayout"
    }
}