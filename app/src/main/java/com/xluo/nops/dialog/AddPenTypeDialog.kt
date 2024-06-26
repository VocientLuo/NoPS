package com.xluo.nops.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.xluo.nops.R
import com.xluo.nops.databinding.DialogAddPenTypeBinding

/**
 * 添加画笔分组
 */
class AddPenTypeDialog : Dialog {
    var mContext: Context? = null
    lateinit var binding: DialogAddPenTypeBinding
    var configListener: ((editName: String) -> Unit)? = null

    constructor(context: Context) : super(context, R.style.StyleBaseDialog) {
        mContext = context

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogAddPenTypeBinding.inflate(LayoutInflater.from(mContext))
        setContentView(binding.root)
        binding.btnOk.setOnClickListener {
            configListener?.invoke(binding.etName.text.toString())
            dismiss()
        }
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }
}