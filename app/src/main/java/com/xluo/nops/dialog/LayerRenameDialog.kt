package com.xluo.nops.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.xluo.nops.R
import com.xluo.nops.databinding.DialogLayerRenameBinding


class LayerRenameDialog : Dialog, View.OnClickListener {
    var mContext: Context? = null
    var binding: DialogLayerRenameBinding? = null
    private var configListener: ((editName: String) -> Unit)? = null
    private var cancelListener: (() -> Unit)? = null

    constructor(context: Context) : super(context, R.style.StyleBaseDialog) {
        mContext = context

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogLayerRenameBinding.inflate(LayoutInflater.from(mContext))
        setContentView(binding!!.root)
        initListener()
    }

    private fun initListener() {
        binding?.renameConfig?.setOnClickListener(this)
        binding?.renameCancel?.setOnClickListener(this)
    }

    fun setConfigListener(configListener: (editName: String) -> Unit) {
        this.configListener = configListener
    }

    fun setCancelListener(cancelListener: ()-> Unit){
        this.cancelListener = cancelListener
    }

    override fun onClick(v: View?) {
        when(v?.id){
            binding?.renameConfig?.id -> {
                configListener?.invoke(binding?.renameEdit?.text.toString())
            }

            binding?.renameCancel?.id -> {
                cancelListener?.invoke()
            }
        }
    }
}