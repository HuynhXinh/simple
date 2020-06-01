package com.xinhhuynh.simpledialog

import android.app.Activity
import android.graphics.Color
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment

fun Activity.showSimpleDialog(
        title: (() -> String)? = null,
        msg: (() -> String)? = null,
        textPositive: (() -> CharSequence)? = null,
        onClickPositiveButton: (() -> Unit)? = null,
        textNegative: (() -> CharSequence)? = null,
        onClickNegativeButton: (() -> Unit)? = null,
        isCancelable: (() -> Boolean) = { true },
        styles: SimpleDialogStyles = SimpleDialogStyles()
) {
    SimpleDialog(
            context = this,
            title = title,
            msg = msg,
            textPositive = textPositive,
            onClickPositiveButton = onClickPositiveButton,
            textNegative = textNegative,
            onClickNegativeButton = onClickNegativeButton,
            isCancelable = isCancelable,
            styles = styles
    ).show()
}

fun Activity.showCustomSimpleDialog(
        title: (() -> String)? = null,
        msg: (() -> String)? = null,
        textPositive: (() -> CharSequence)? = null,
        onClickPositiveButton: (() -> Unit)? = null,
        textNegative: (() -> CharSequence)? = null,
        onClickNegativeButton: (() -> Unit)? = null,
        isCancelable: (() -> Boolean) = { true }
) {
    SimpleDialog(
            context = this,
            title = title,
            msg = msg,
            textPositive = textPositive,
            onClickPositiveButton = onClickPositiveButton,
            textNegative = textNegative,
            onClickNegativeButton = onClickNegativeButton,
            isCancelable = isCancelable,
            styles = SimpleDialogStyles(
                    typeface = ResourcesCompat.getFont(this, R.font.sharp_sans_disp_no1_semi_bold),
                    title = SimpleDialogStyles.Style(size = 18.toSp(), color = Color.BLACK, bold = true, isAllCaps = true),
                    msg = SimpleDialogStyles.Style(size = 16.toSp(), color = "#6B6E79".toColor()),
                    separator = SimpleDialogStyles.Style(size = 1.toSp(), color = "#CDCED2".toColor()),
                    btnPositive = SimpleDialogStyles.Style(size = 16.toSp(), color = toColor(R.color.colorPrimaryDark), bold = true, isAllCaps = true),
                    btnNegative = SimpleDialogStyles.Style(size = 16.toSp(), color = toColor(R.color.colorPrimaryDark), isAllCaps = true)
            )
    ).show()
}

fun Fragment.showSimpleDialog(
        title: (() -> String)? = null,
        msg: (() -> String)? = null,
        textPositive: (() -> CharSequence)? = null,
        onClickPositiveButton: (() -> Unit)? = null,
        textNegative: (() -> CharSequence)? = null,
        onClickNegativeButton: (() -> Unit)? = null,
        isCancelable: (() -> Boolean) = { true },
        styles: SimpleDialogStyles = SimpleDialogStyles()
) {
    SimpleDialog(
            context = requireContext(),
            title = title,
            msg = msg,
            textPositive = textPositive,
            onClickPositiveButton = onClickPositiveButton,
            textNegative = textNegative,
            onClickNegativeButton = onClickNegativeButton,
            isCancelable = isCancelable,
            styles = styles
    ).show()
}