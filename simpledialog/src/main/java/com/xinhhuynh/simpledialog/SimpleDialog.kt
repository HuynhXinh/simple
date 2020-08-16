package com.xinhhuynh.simpledialog

import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

class SimpleDialogStyles(
    var typeface: Typeface? = null,
    var title: Style = Style(size = 16.toSp(), color = Color.BLACK, bold = true, isAllCaps = true),
    var msg: Style = Style(size = 14.toSp(), color = "#6B6E79".toColor()),
    var separator: Style = Style(size = 1.toSp(), color = "#CDCED2".toColor()),
    var btnPositive: Style = Style(size = 16.toSp(), color = "#0A8020".toColor(), bold = true),
    var btnNegative: Style = Style(size = 16.toSp(), color = "#0A8020".toColor())
) {
    class Style(
        val size: Float,
        @ColorInt val color: Int,
        val bold: Boolean = false,
        val isAllCaps: Boolean = false
    )
}

class SimpleDialog(
    context: Context,
    private val title: (() -> String)? = null,
    private val msg: (() -> String)? = null,
    private val textPositive: (() -> CharSequence)? = null,
    private val onClickPositiveButton: (() -> Unit)? = null,
    private val textNegative: (() -> CharSequence)? = null,
    private val onClickNegativeButton: (() -> Unit)? = null,
    private val isCancelable: (() -> Boolean) = { true },
    private val styles: SimpleDialogStyles = SimpleDialogStyles()
) {

    val dialog = Dialog(context, getTheme(context))

    private fun getTheme(context: Context): Int {
        val outValue = TypedValue()
        context.theme.resolveAttribute(R.attr.alertDialogTheme, outValue, true)
        return outValue.resourceId
    }

    init {
        dialog.apply {

            setContentView(getLayout())

            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            setCancelable(isCancelable.invoke())

            findViewById<TextView>(R.id.tvTitle).apply {
                applyStyle(styles.title, styles.typeface)

                text = title?.invoke()
            }

            findViewById<TextView>(R.id.tvMsg).apply {
                applyStyle(styles.msg, styles.typeface)

                text = msg?.invoke()
            }

            textPositive?.let {
                findViewById<TextView>(R.id.tvPositive).apply {
                    applyStyle(styles.btnPositive, styles.typeface)

                    text = it.invoke()
                    show()
                    setOnClickListener {
                        dismiss()
                        onClickPositiveButton?.invoke()
                    }
                }
            }

            textNegative?.let {
                findViewById<TextView>(R.id.tvNegative).apply {
                    applyStyle(styles.btnNegative, styles.typeface)

                    text = it.invoke()
                    show()
                    setOnClickListener {
                        cancel()
                        onClickNegativeButton?.invoke()
                    }
                }
            }

            findViewById<View>(R.id.separator).apply {
                setBackgroundColor(styles.separator.color)
            }

            findViewById<View>(R.id.divider).apply {
                setBackgroundColor(styles.separator.color)

                showOrGone(isHaveTowButton())
            }
        }
    }

    private fun getLayout(): Int {
        return if (isHaveTowButton() && isTextButtonTooLong()) {
            R.layout.dialog_simple_vertical
        } else
            R.layout.dialog_simple
    }

    private fun isTextButtonTooLong(): Boolean {
        val buttonWidth = (Resources.getSystem().displayMetrics.widthPixels * 0.12).toInt()

        return getWidthOf(textPositive?.invoke(), styles.typeface, styles.btnPositive) >= buttonWidth ||
                getWidthOf(
                    textNegative?.invoke(),
                    styles.typeface,
                    styles.btnNegative
                ) >= buttonWidth
    }

    private fun getWidthOf(
        text: CharSequence?,
        _typeface: Typeface?,
        _style: SimpleDialogStyles.Style
    ): Int {
        text ?: return 0

        val result = Rect()

        Paint().apply {
            textSize = _style.size
            typeface = _typeface
            color = _style.color
            style = Paint.Style.FILL
        }.also {
            it.getTextBounds(text.toString(), 0, text.length, result)
        }

        return result.width()
    }

    private fun isHaveTowButton(): Boolean {
        return textPositive != null && textNegative != null
    }

    fun show() {
        dialog.show()
    }
}

fun String.toColor(): Int = Color.parseColor(this)

fun Context.toColor(@ColorRes id: Int): Int = ContextCompat.getColor(this, id)

fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Int.toSp(): Float = this.toPx() / Resources.getSystem().displayMetrics.scaledDensity

private fun TextView.applyStyle(style: SimpleDialogStyles.Style, typeface: Typeface? = null) {
    val textStyle = if (style.bold) Typeface.BOLD else Typeface.NORMAL
    setTypeface(typeface, textStyle)
    textSize = style.size
    setTextColor(style.color)
    isAllCaps = style.isAllCaps
}

private fun View.show() {
    this.visibility = View.VISIBLE
}

private fun View.showOrGone(show: Boolean) {
    this.visibility = if (show) View.VISIBLE else View.GONE
}