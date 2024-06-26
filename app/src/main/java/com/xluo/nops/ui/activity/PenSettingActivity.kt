package com.xluo.nops.ui.activity

import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.xluo.nops.ui.activity.fragment.PenLineFragment
import com.xluo.nops.ui.activity.fragment.PenPressFragment
import com.xluo.nops.ui.activity.fragment.PenRenderFragment
import com.xluo.nops.ui.activity.fragment.PenTopFragment
import com.xluo.lib_base.ui.BaseViewModelActivity
import com.xluo.nops.R
import com.xluo.nops.databinding.ActivityPenSettingBinding
import com.xluo.nops.model.PenSettingModel
import com.xluo.nops.pop.PopPenSetMenu
import com.xluo.pen.core.BasePen
import org.greenrobot.eventbus.EventBus

class PenSettingActivity : BaseViewModelActivity<PenSettingModel, ActivityPenSettingBinding>() {
    companion object {
        var pen: BasePen? = null
    }

    val list = arrayListOf(
        PenTopFragment(),
        PenLineFragment(),
        PenRenderFragment(),
        PenPressFragment(),
//        PenAboutFragment()
    )

    private val tvList = arrayListOf<TextView>()
    val adapter = object: FragmentStateAdapter(this) {
        override fun getItemCount(): Int {
            return list.size
        }

        override fun createFragment(position: Int): Fragment {
            return list[position] as Fragment
        }
    }

    override fun createViewModel(): PenSettingModel {
        return PenSettingModel()
    }

    override fun createViewBinding(): ActivityPenSettingBinding {
        return ActivityPenSettingBinding.inflate(layoutInflater)
    }

    override fun initView() {
        pen?.let {
            binding.penMonitorView.postDelayed({
                binding.penMonitorView.updatePen(it)
            }, 100)
        }
        tvList.clear()
        tvList.add(binding.tvPenTop)
        tvList.add(binding.tvLine)
        tvList.add(binding.tvRender)
        tvList.add(binding.tvPressure)
        //tvList.add(binding.tvAbout)
        initFragments()
        binding.back.setOnClickListener {
            finish()
        }
        val popResetMenu = PopPenSetMenu(this)
        popResetMenu.menuClick = {
            pen?.let {
                it.reset()
                updatePen()
                EventBus.getDefault().post("reset")
            }
        }
        binding.ivMenu.setOnClickListener {
            popResetMenu.showAsDropDown(it)
        }
    }

    override fun onResume() {
        super.onResume()
        updatePen()
    }

    fun updatePen() {
        pen?.let {
            binding.penMonitorView.updatePen(it)
        }
    }

    private fun initFragments() {
        binding.vpContainer.adapter = adapter
        binding.vpContainer.setUserInputEnabled(false)
        tvList.forEachIndexed {index, tv ->
            tv.setOnClickListener {
                showFragment(index)
            }
        }
        showFragment(0)
    }

    private fun showFragment(index: Int) {
        tvList.forEach {
            it.isSelected = false
            it.setTextColor(getColor(R.color.black_90))
        }
        tvList[index].isSelected = true
        tvList[index].setTextColor(getColor(R.color.white))
        binding.vpContainer.currentItem = index
    }
}