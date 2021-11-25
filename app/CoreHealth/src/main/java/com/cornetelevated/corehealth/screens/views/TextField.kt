package com.cornetelevated.corehealth.screens.views

import android.annotation.SuppressLint
import android.content.Context
import android.text.InputType
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.cornetelevated.corehealth.R

class TextField(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private var placeholderTextView: TextView? = null
    private var errorTextView: TextView? = null
    private var editText: EditText? = null
    private var isPasswordShown = false

    var placeholderText: String
        get() = placeholderTextView?.text.toString()
        set(value) {
            placeholderTextView?.text = value
            editText?.hint = value
        }

    var errorText: String
        get() = errorTextView?.text.toString()
        set(value) {
            errorTextView?.text = value
            if (value.isNotEmpty()) {
                errorTextView?.visibility = View.VISIBLE
            } else {
                errorTextView?.visibility = View.GONE
            }
        }

    var inputType: Int
        get() = editText?.inputType ?: InputType.TYPE_CLASS_TEXT
        set(value) {
            editText?.inputType = value
        }

    var text: String
        get() = editText?.text.toString()
        set(value) {
            editText?.setText(value)
        }

    init {
        inflate(context, R.layout.view_textfield, this)
        placeholderTextView = findViewById(R.id.placeholderLabel)
        errorTextView = findViewById(R.id.errorLabel)
        editText = findViewById(R.id.editText)
    }

    fun setClickListener(listener: OnClickListener) {
        editText?.isFocusable = false
        editText?.isClickable = true
        editText?.setOnClickListener(listener)
    }

    fun addTextWatcher(watcher: TextWatcher) {
        editText?.addTextChangedListener(watcher)
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setPassword() {
        editText?.transformationMethod = PasswordTransformationMethod()
        val icon = ContextCompat.getDrawable(context, R.drawable.ic_show)
        icon?.setBounds(0,0,60,40)
        editText?.setCompoundDrawables(null,null, icon, null)
        editText?.setOnTouchListener(OnTouchListener { v, event ->
            editText?.let {
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= (it.right - it.compoundDrawables[2].bounds.width())
                    ) {
                        if (isPasswordShown) {
                            // hide password
                            isPasswordShown = false
                            it.transformationMethod = PasswordTransformationMethod()
                            it.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_show, 0)
                        } else {
                            // show password
                            isPasswordShown = true
                            editText?.transformationMethod = HideReturnsTransformationMethod()
                            editText?.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_hide, 0)
                        }
                        return@OnTouchListener true
                    }
                }
            }
            false
        })
    }

}