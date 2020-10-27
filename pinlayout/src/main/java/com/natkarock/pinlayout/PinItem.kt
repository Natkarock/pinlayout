package com.mobile.finiza.app.ui.view.pinView

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.InputFilter
import android.text.InputType
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import com.google.android.material.textfield.TextInputEditText



class PinItem : TextInputEditText {

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) : super(
        context,
        attributeSet,
        defStyle
    )

    var selectBackground: Drawable? = null
        set(value) {
            field = value
            invalidateBackground()
            invalidate()
            requestLayout()
        }

    var unselectBackground: Drawable? = null
        set(value) {
            field = value
            invalidateBackground()
            invalidate()
            requestLayout()
        }

    var unselectEmptyBackground: Drawable? = null
        set(value) {
            field = value
            invalidateBackground()
            invalidate()
            requestLayout()
        }


    var nextFocusCallback: (() -> Unit)? = null
    var prevFocusCallback: (() -> Unit)? = null
    var onFocusCallback: (() -> Unit)? = null


    init {

         //set initial parameters
        if (unselectEmptyBackground != null) {
            setBackgroundDrawable(unselectEmptyBackground)
        }
        textAlignment = TEXT_ALIGNMENT_CENTER
        inputType = InputType.TYPE_CLASS_NUMBER
        filters = arrayOf(InputFilter.LengthFilter(MAX_NUM))
        isCursorVisible = false

        //set listeners
        setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                setText("")
                selectBackground?.let { setBackgroundDrawable(it) }
                onFocusCallback?.invoke()
            } else {
                if ( text == null || text.toString().isEmpty()) {
                    unselectEmptyBackground?.let { setBackgroundDrawable(it) }
                } else {
                    unselectBackground?.let {  setBackgroundDrawable(it) }
                }
            }
        }

        setOnKeyListener { _, keyCode, event ->
            if ((event.action == KeyEvent.ACTION_DOWN) &&
                (keyCode == KeyEvent.KEYCODE_DEL)
            ) {
                prevFocusCallback?.invoke()
            }
            false

        }

        setOnClickListener {
            if(isFocused){
                setText("")
                selectBackground?.let { setBackgroundDrawable(it) }
                onFocusCallback?.invoke()
            }
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }


    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        count: Int
    ) {
        Log.i(PIN_ITEM_LOG, text?.length.toString())
        val lengthAfter = text?.length ?: 0
        if (isFocused) {
            if (lengthAfter == MAX_NUM) {
                nextFocusCallback?.invoke()
            }
        }
        super.onTextChanged(text, start, lengthBefore, count)
    }


    fun isFull() = text?.length == MAX_NUM

    private fun invalidateBackground() {
        if (isFocused) {
            selectBackground?.let { setBackgroundDrawable(it) }
        } else {
            if ( text == null || text.toString().isEmpty()) {
                unselectEmptyBackground?.let { setBackgroundDrawable(it) }
            } else {
                unselectBackground?.let {  setBackgroundDrawable(it) }
            }
        }
    }

    companion object {
        private const val MAX_NUM = 1
        private const val PIN_ITEM_LOG = "PIN_ITEM_LOG"
    }

}