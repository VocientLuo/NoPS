package com.xluo.nops.ui.activity.fragment

import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.xluo.lib_base.ui.BaseViewModelFragment
import com.xluo.nops.ui.activity.PenSettingActivity
import com.xluo.nops.databinding.FragmentPenRenderBinding
import com.xluo.nops.model.PenSettingModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class PenRenderFragment : BaseViewModelFragment<PenSettingModel, FragmentPenRenderBinding>() {

    override fun createViewModel(): PenSettingModel {
        return PenSettingModel()
    }

    override fun createViewBinding(): FragmentPenRenderBinding {
        return FragmentPenRenderBinding.inflate(layoutInflater)
    }

    override fun initView(containerView: View) {
        EventBus.getDefault().register(this)
        with(binding) {
            sbFilterColor.setOnSeekBarChangeListener(object: OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    tvPenAppearText.text = "${p1}%"
                    PenSettingActivity.pen?.hueValue = pToHue(p1)
                    updatePen()
                }
                override fun onStartTrackingTouch(p0: SeekBar?) {
                }
                override fun onStopTrackingTouch(p0: SeekBar?) {
                }
            })
            sbPenFull.setOnSeekBarChangeListener(object: OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    tvPenFullText.text = "${p1}%"
                    PenSettingActivity.pen?.saturationValue = pToSaturation(p1)
                    updatePen()
                }
                override fun onStartTrackingTouch(p0: SeekBar?) {
                }
                override fun onStopTrackingTouch(p0: SeekBar?) {
                }
            })
            sbPenLight.setOnSeekBarChangeListener(object: OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    tvPenLightText.text = "${p1}%"
                    PenSettingActivity.pen?.brightnessValue = pToBright(p1)
                    updatePen()
                }
                override fun onStartTrackingTouch(p0: SeekBar?) {
                }
                override fun onStopTrackingTouch(p0: SeekBar?) {
                }
            })
            scPenColorActive.setOnCheckedChangeListener { _, b ->
                PenSettingActivity.pen?.colorMatrixEnabled = b
                updateRenderStatus(b)
                updatePen()
            }
        }
    }

    private fun updateRenderStatus(b: Boolean) {
        with(binding) {
            if (!b) {
                sbPenFull.isEnabled = false
                sbPenFull.alpha = 0.8f
                sbPenLight.isEnabled = false
                sbPenLight.alpha = 0.8f
                sbFilterColor.isEnabled = false
                sbFilterColor.alpha = 0.8f
            } else {
                sbPenFull.isEnabled = true
                sbPenFull.alpha = 1f
                sbPenLight.isEnabled = true
                sbPenLight.alpha = 1f
                sbFilterColor.isEnabled = true
                sbFilterColor.alpha = 1f
            }
        }
    }

    override fun initData() {
        PenSettingActivity.pen?.let {
            with(binding) {
                scPenColorActive.isChecked = it.colorMatrixEnabled
                updateRenderStatus(it.colorMatrixEnabled)
                val brightness = brightToProgress(it.brightnessValue)
                sbPenLight.progress = brightness
                tvPenLightText.text = "${brightness}%"
                val saturation = saturationToProgress(it.saturationValue)
                sbPenFull.progress = saturation
                tvPenFullText.text = "${saturation}%"
                val hueValue = hueToProgress(it.hueValue)
                sbFilterColor.progress = hueValue
                tvPenAppearText.text = "${hueValue}%"

            }
        }
    }

    private fun updatePen() {
        (activity as PenSettingActivity).updatePen()
    }

    // brightness [-1,1]
    private fun brightToProgress(value: Float): Int {
        return ((value + 1)/2*100).toInt()
    }

    private fun pToBright(p: Int): Float {
        return (p/100f*2-1)
    }

    // saturation [0,1]
    private fun saturationToProgress(value: Float): Int {
        return (value*100).toInt()
    }

    private fun pToSaturation(p: Int): Float {
        return p/100f
    }

    // hue [0,360]
    private fun hueToProgress(value: Float): Int {
        return (value/360*100).toInt()
    }

    private fun pToHue(p: Int): Float {
        return p/100f*360
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