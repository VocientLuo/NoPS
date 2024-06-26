package com.xluo.nops.ui.activity.fragment

import android.view.View
import com.xluo.nops.ui.activity.PenSettingActivity
import com.xluo.lib_base.ui.BaseViewModelFragment
import com.xluo.nops.databinding.FragmentPenAboutBinding
import com.xluo.nops.model.PenSettingModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class PenAboutFragment : BaseViewModelFragment<PenSettingModel, FragmentPenAboutBinding>() {
    override fun createViewModel(): PenSettingModel {
        return PenSettingModel()
    }

    override fun createViewBinding(): FragmentPenAboutBinding {
        return FragmentPenAboutBinding.inflate(layoutInflater)
    }

    override fun initView(containerView: View) {
        PenSettingActivity.pen?.let {
            binding.tvPenName.text = it.name
        }
        binding.tvPenReset.setOnClickListener {

        }
        EventBus.getDefault().register(this)
    }

    override fun initData() {

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