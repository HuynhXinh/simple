package com.xinhhuynh.sample

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.xinhhuynh.simpledialog.SimpleDialog
import com.xinhhuynh.simpledialog.SimpleDialogStyles
import com.xinhhuynh.simpledialog.toColor
import com.xinhhuynh.simpledialog.toSp
import kotlinx.android.synthetic.main.activity_sample_dialog.*

class SampleDialogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample_dialog)

        btnShowOne.setOnClickListener {
            showSimpleDialog(
                title = { "Hi One !" },
                msg = { "Thanks for your visit to simple dialog. This is simple with positive button" },
                textPositive = { "Ok" }
            )
        }

        btnShowTwo.setOnClickListener {
            showSimpleDialog(
                title = { "Hi Two!" },
                msg = { "Thanks for your visit to simple dialog. This is simple with two positive and negative buttons" },
                textPositive = { "Ok" },
                textNegative = { "Cancel" }
            )
        }

        btnShowCustom.setOnClickListener {
            showCustomSimpleDialog(
                title = { "Hi Custom!" },
                msg = { "Thanks for your visit to simple dialog. This is custom simple dialog" },
                textPositive = { "Ok" },
                textNegative = { "Cancel" },
                isCancelable = { false }
            )
        }

        btnShowTextButtonTooLong.setOnClickListener {
            showSimpleDialog(
                title = { "Hi Long Text Button!" },
                msg = { "Thanks for your visit to simple dialog. This is very long text button" },
                textPositive = { "Very long text button OK" },
                textNegative = { "Cancel" }
            )
        }
    }
}

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
            typeface = ResourcesCompat.getFont(
                this,
                R.font.sharp_sans_disp_no1_semi_bold
            ),
            title = SimpleDialogStyles.Style(
                size = 18.toSp(),
                color = Color.BLACK,
                bold = true,
                isAllCaps = true
            ),
            msg = SimpleDialogStyles.Style(
                size = 16.toSp(),
                color = "#6B6E79".toColor()
            ),
            separator = SimpleDialogStyles.Style(
                size = 1.toSp(),
                color = "#CDCED2".toColor()
            ),
            btnPositive = SimpleDialogStyles.Style(
                size = 16.toSp(),
                color = toColor(R.color.colorPrimaryDark),
                bold = true,
                isAllCaps = true
            ),
            btnNegative = SimpleDialogStyles.Style(
                size = 16.toSp(),
                color = toColor(R.color.colorPrimaryDark),
                isAllCaps = true
            )
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