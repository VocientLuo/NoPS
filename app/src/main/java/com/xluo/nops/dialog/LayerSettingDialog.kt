package com.xluo.nops.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.xluo.nops.R
import com.xluo.nops.databinding.DialogLayerSettingBinding


class LayerSettingDialog : Dialog, View.OnClickListener {
    var mContext: Context? = null
    var binding: DialogLayerSettingBinding? = null
    private var layerSettingCopy: (() -> Unit)? = null
    private var layerRename: (() -> Unit)? = null

    constructor(context: Context) : super(context, R.style.StyleBaseDialog) {
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogLayerSettingBinding.inflate(LayoutInflater.from(mContext))
        setContentView(binding!!.root)
        initListener()
    }

    private fun initListener() {
        binding?.layerCopyBt?.setOnClickListener(this)
        binding?.renameBt?.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding?.layerCopyBt?.id -> {
                layerSettingCopy?.invoke()
            }

            binding?.renameBt?.id -> {
                layerRename?.invoke()
            }
        }
    }

    fun setCopyListener(layerSettingCopy: () -> Unit) {
        this.layerSettingCopy = layerSettingCopy
    }

    fun setRenameListener(layerRename: () -> Unit) {
        this.layerRename = layerRename
    }

}