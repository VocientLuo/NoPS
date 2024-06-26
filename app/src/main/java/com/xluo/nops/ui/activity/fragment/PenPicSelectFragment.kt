package com.xluo.nops.ui.activity.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import androidx.recyclerview.widget.GridLayoutManager
import com.xluo.nops.adapter.PenShapeSelectAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.xluo.nops.databinding.FragmentPenPicSelectBinding

class PenPicSelectFragment: BottomSheetDialogFragment() {
    private lateinit var binding: FragmentPenPicSelectBinding

    private lateinit var adapter: PenShapeSelectAdapter

    var onSelect: ((Int) -> Unit) ? = null

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        binding = FragmentPenPicSelectBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        val manager = GridLayoutManager(requireContext(), 4)
        adapter = PenShapeSelectAdapter(requireContext(), getResList())
        binding.rvShapeList.layoutManager = manager
        binding.rvShapeList.adapter = adapter
        adapter.onClick = {
            onSelect?.invoke(it)
            dismiss()
        }
    }

    private fun getResList(): List<Int> {
        val list = arrayListOf<Int>()
        for (i in 1 .. 50) {
            val fix = if ( i < 10) {
                "0$i"
            } else {
                "$i"
            }
            val name = "pic_tippen${fix}"
            list.add(getResId(name))
        }
        return list
    }

    private fun getResId(name: String): Int {
        val resId = resources.getIdentifier(name, "drawable", requireContext().packageName);
        return resId
    }
}