package com.xluo.nops.ui.activity.fragment

import android.view.Gravity
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import com.xluo.core.constants.Constants
import com.xluo.lib_base.ui.BaseViewModelFragment
import com.xluo.nops.databinding.FragmentPenTopBinding
import com.xluo.nops.ui.activity.PenSettingActivity
import com.xluo.nops.model.PenSettingModel
import com.xluo.nops.pop.PopPenModeSelector
import com.xluo.pen.high.HiPenBase
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 笔尖配置
 */
class PenTopFragment : BaseViewModelFragment<PenSettingModel, FragmentPenTopBinding>() {
    override fun createViewModel(): PenSettingModel {
        return PenSettingModel()
    }

    override fun createViewBinding(): FragmentPenTopBinding {
        return FragmentPenTopBinding.inflate(layoutInflater)
    }

    private lateinit var penModeSelector: PopPenModeSelector

    private var penPicSelectFragment: PenPicSelectFragment? = null

    override fun initView(containerView: View) {
        EventBus.getDefault().register(this)
        penModeSelector = PopPenModeSelector(requireContext(), PenSettingActivity.pen?.penMixMode)
        penModeSelector.onPenModeChanged = {
            PenSettingActivity.pen?.penMixMode = it
            binding.tvPenXModeSelect.text = "${it.name}"
            updatePen()
        }
        binding.sbPenSize.max = Constants.MAX_PEN_SIZE
        binding.sbPenSize.setOnSeekBarChangeListener(object: OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                binding.tvPenSizeText.text = "$p1"
                PenSettingActivity.pen?.size = p1
                updatePen()
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {
            }
            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })
        binding.sbPenAlpha.setOnSeekBarChangeListener(object: OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                val textAlpha = ((p1/255f)*100).toInt()
                binding.tvPenAlphaText.text = "${textAlpha}%"
                PenSettingActivity.pen?.alpha = 255 - p1
                updatePen()
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {
            }
            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })
        binding.sbPenFlow.setOnSeekBarChangeListener(object: OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                val textAlpha = ((p1/255f)*100).toInt()
                binding.tvPenFlowText.text = "${textAlpha}%"
                PenSettingActivity.pen?.flowValue = p1
                updatePen()
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {
            }
            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })


        binding.scPenShape.setOnCheckedChangeListener { _, b ->
            PenSettingActivity.pen?.penShapeEnabled = b
            updatePenShapeEnabled(b)
            updatePen()
        }
        binding.scPenShapeHorizontal.setOnCheckedChangeListener { _, b ->
            PenSettingActivity.pen?.penVertical = !b
            updatePen()
        }
        binding.scPenShapeSquare.setOnCheckedChangeListener { _, b ->
            PenSettingActivity.pen?.penSquare = b
            updatePen()
        }
        binding.scPenShapeColor.setOnCheckedChangeListener { _, b ->
            PenSettingActivity.pen?.penShapeColorEnabled = b
            penShapeColorEnabled(b)
            updatePen()
        }
        binding.scPenShapeReverse.setOnCheckedChangeListener { _, b ->
            PenSettingActivity.pen?.penShapeReverse = b
            updatePen()
        }
        binding.scPenShapeRGB.setOnCheckedChangeListener { _, b ->
            PenSettingActivity.pen?.penShapeRGBEffectAlpha = b
            updatePen()
        }
        binding.scPenShapeGap.setOnCheckedChangeListener { _, b ->
            PenSettingActivity.pen?.penShapeGapEnabled = b
            updatePen()
        }

        binding.sbPenRotate.setOnSeekBarChangeListener(object: OnSeekBarChangeListener {
             override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                 binding.tvPenRotateText.text = "$p1"
                 PenSettingActivity.pen?.rotation = p1.toFloat()
                 updatePen()
             }

             override fun onStartTrackingTouch(p0: SeekBar?) {
             }

             override fun onStopTrackingTouch(p0: SeekBar?) {
             }
        })
        binding.sbPenInSpace.setOnSeekBarChangeListener(object: OnSeekBarChangeListener {
             override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                 binding.tvPenInSpaceText.text = "$p1%"
                 PenSettingActivity.pen?.midPadding = p1
                 updatePen()
             }

             override fun onStartTrackingTouch(p0: SeekBar?) {
             }

             override fun onStopTrackingTouch(p0: SeekBar?) {
             }
        })
        binding.sbPenInSpaceAlpha.setOnSeekBarChangeListener(object: OnSeekBarChangeListener {
             override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                 binding.tvPenInSpaceAlphaText.text = "${p1}%"
                 PenSettingActivity.pen?.midPaddingAlpha = p1*2.55f
                 updatePen()
             }

             override fun onStartTrackingTouch(p0: SeekBar?) {
             }

             override fun onStopTrackingTouch(p0: SeekBar?) {
             }
        })

        binding.scPenRotateByPen.setOnCheckedChangeListener { _, b ->
            PenSettingActivity.pen?.isRotateByPenOriental = b
            updatePen()
        }
        binding.tvPenXModeSelect.setOnClickListener {
            penModeSelector.showAtLocation(it, Gravity.BOTTOM, 0, 0)
        }
        binding.scPenShapeHorizontal.setOnCheckedChangeListener { _, b ->
            PenSettingActivity.pen?.penVertical = b
            binding.ivPenCircle.updateOriental(b)
            updatePen()
        }
        binding.scPenShapeSquare.setOnCheckedChangeListener { _, b ->
            PenSettingActivity.pen?.penSquare = b
            updatePen()
        }
        binding.ivPenPic.setOnClickListener {
            PenSettingActivity.pen?.let {
                if (it is HiPenBase) {
                    showPenShapeSelect()
                }
            }
        }
    }

    private fun showPenShapeSelect() {
        if (penPicSelectFragment == null) {
            penPicSelectFragment = PenPicSelectFragment()
            penPicSelectFragment?.onSelect = {resId ->
                PenSettingActivity.pen?.let {
                    if (it is HiPenBase) {
                        it.updateBitmap(resId)
                        binding.ivPenPic.setImageResource(resId)
                        updatePen()
                    }
                }
            }
        }
        penPicSelectFragment?.let {
            if (it.isAdded) {
                return
            }
            it.show(requireActivity().supportFragmentManager, it.tag)
        }
    }

    private fun updatePen() {
        (activity as PenSettingActivity).updatePen()
    }

    private fun updatePenShapeEnabled(enabled: Boolean) {
        with(binding) {
            ivPenPic.isEnabled = enabled
            scPenShapeColor.isEnabled = enabled
            scPenShapeReverse.isEnabled = enabled
            scPenShapeRGB.isEnabled = enabled
            scPenShapeGap.isEnabled = enabled
            if (enabled) {
                scPenShapeColor.alpha = 1.0f
                scPenShapeReverse.alpha = 1.0f
                scPenShapeRGB.alpha = 1.0f
                scPenShapeGap.alpha = 1.0f
            } else {
                scPenShapeColor.alpha = 0.5f
                scPenShapeReverse.alpha = 0.5f
                scPenShapeRGB.alpha = 0.5f
                scPenShapeGap.alpha = 0.5f
                PenSettingActivity.pen?.let {
                    it.penShapeColorEnabled = false
                    it.penShapeReverse = false
                    it.penShapeRGBEffectAlpha  = false
                    it.penShapeGapEnabled = false
                    if (it is HiPenBase) {
                        it.resetBitmap()
                    }
                }
                binding.ivPenPic.setImageBitmap(null)
            }
        }
    }

    private fun penShapeColorEnabled(checked: Boolean) {
        with(binding) {
            ViewUtils.viewEnabled(scPenShapeReverse, !checked)
            ViewUtils.viewEnabled(scPenShapeRGB, !checked)
            scPenShapeReverse.isEnabled = !checked
            scPenShapeRGB.isEnabled = !checked
        }
    }

    override fun initData() {
        PenSettingActivity.pen?.let {
            with(binding) {
                sbPenSize.progress = it.size
                tvPenSizeText.text = "${it.size}"
                sbPenAlpha.progress = 255-it.alpha
                sbPenFlow.progress = it.flowValue
                tvPenAlphaText.text = "${(255-it.alpha)/255*100}%"
                tvPenFlowText.text = "${(it.alpha)/255*100}%"
                ivPenPic.setImageBitmap(null)
                ivPenCircle.updateOriental(it.penVertical)
                scPenShape.isChecked = it.penShapeEnabled
                scPenShapeReverse.isChecked = it.penShapeReverse
                scPenShapeGap.isChecked = it.penShapeGapEnabled
                scPenShapeHorizontal.isChecked = it.penVertical
                scPenShapeSquare.isChecked = it.penSquare
                sbPenRotate.progress = it.rotation.toInt()
                tvPenRotateText.text = "${it.rotation}°"
                scPenRotateByPen.isChecked = it.isRotateByPenOriental
                sbPenInSpace.progress = it.midPadding
                tvPenInSpaceText.text = "${it.midPadding}"
                sbPenInSpaceAlpha.progress = (it.midPaddingAlpha/2.55).toInt()
                tvPenInSpaceAlphaText.text = "${it.midPaddingAlpha}%"
                it.penMixMode?.let {mode ->
                    tvPenXModeSelect.text = "${mode.name}"
                }
                ViewUtils.viewEnabled(sbPenAlpha, it.alphaEnabled)
                ViewUtils.viewEnabled(ivPenPic, it.penShapePicEnabled)
                ViewUtils.viewEnabled(ivPenPicIcon, it.penShapePicEnabled)
                ViewUtils.viewEnabled(sbPenFlow, it.flowValueEnabled)
                ViewUtils.viewEnabled(sbPenInSpace, it.midPaddingEnabled)
                ViewUtils.viewEnabled(sbPenRotate, it.rotationEnabled)
                ViewUtils.viewEnabled(scPenRotateByPen, it.rotationEnabled)
                penShapeColorEnabled(it.penShapeColorEnabled)
                updatePenShapeEnabled(it.penShapeEnabled)
            }
        }
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