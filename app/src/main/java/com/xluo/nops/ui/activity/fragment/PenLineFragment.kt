package com.xluo.nops.ui.activity.fragment

import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.xluo.lib_base.ui.BaseViewModelFragment
import com.xluo.nops.databinding.FragmentPenLineBinding
import com.xluo.nops.ui.activity.PenSettingActivity
import com.xluo.nops.model.PenSettingModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class PenLineFragment : BaseViewModelFragment<PenSettingModel, FragmentPenLineBinding>() {
    override fun createViewModel(): PenSettingModel {
        return PenSettingModel()
    }

    override fun createViewBinding(): FragmentPenLineBinding {
        return FragmentPenLineBinding.inflate(layoutInflater)
    }

    override fun initView(containerView: View) {
        binding.sbPenSpace.setOnSeekBarChangeListener(object: OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                binding.tvPenSpaceText.text = "${p1}%"
                PenSettingActivity.pen?.space = p1
                updatePen()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar) {
            }
        })
        binding.sbPenRandomSpace.setOnSeekBarChangeListener(object: OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                binding.tvPenRandomSpaceText.text = "${p1}%"
                PenSettingActivity.pen?.randomSpace = p1
                updatePen()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar) {
            }
        })
        EventBus.getDefault().register(this)
    }

    override fun initData() {
        PenSettingActivity.pen?.let {
            binding.sbPenSpace.progress = it.space
            binding.tvPenSpaceText.text = "${it.space}%"
            binding.sbPenRandomSpace.progress = it.randomSpace
            binding.tvPenRandomSpaceText.text = "${it.randomSpace}%"
            ViewUtils.viewEnabled(binding.sbPenSpace, it.spaceEnabled)
            ViewUtils.viewEnabled(binding.sbPenRandomSpace, it.randomSpaceEnabled)
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