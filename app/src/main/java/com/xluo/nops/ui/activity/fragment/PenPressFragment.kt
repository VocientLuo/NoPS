package com.xluo.nops.ui.activity.fragment

import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.xluo.lib_base.ui.BaseViewModelFragment
import com.xluo.nops.databinding.FragmentPenPressureBinding
import com.xluo.nops.ui.activity.PenSettingActivity
import com.xluo.nops.model.PenSettingModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class PenPressFragment : BaseViewModelFragment<PenSettingModel, FragmentPenPressureBinding>() {
    override fun createViewModel(): PenSettingModel {
        return PenSettingModel()
    }

    override fun createViewBinding(): FragmentPenPressureBinding {
        return FragmentPenPressureBinding.inflate(layoutInflater)
    }

    override fun initView(containerView: View) {
        with(binding) {
            scPenPressActive.setOnCheckedChangeListener { _, b ->
                PenSettingActivity.pen?.penPressEnabled = b
                ViewUtils.viewEnabled(sbPenSize, b)
            }
            sbPenSize.setOnSeekBarChangeListener(object: OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    PenSettingActivity.pen?.minDiam = p1.toFloat()
                    tvPenAppearText.text = "${p1}%"
                    updatePen()
                }
                override fun onStartTrackingTouch(p0: SeekBar?) {
                }
                override fun onStopTrackingTouch(p0: SeekBar?) {
                }
            })
            scPenSpeedMoni.setOnCheckedChangeListener { _, b ->
                PenSettingActivity.pen?.speedMoniPress = b

            }
        }
        EventBus.getDefault().register(this)
    }

    override fun initData() {
        PenSettingActivity.pen?.let {
            with(binding) {
                scPenSpeedMoni.isChecked = it.speedMoniPress
                sbPenSize.progress = it.minDiam.toInt()
                ViewUtils.viewEnabled(sbPenSize, it.penPressEnabled)
                scPenPressActive.isChecked = it.penPressEnabled
            }
        }
    }

    private fun updatePen() {
        (activity as PenSettingActivity).updatePen()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReset(msg: String) {
        if (msg == "reset") {
            initData()
        }
    }
}