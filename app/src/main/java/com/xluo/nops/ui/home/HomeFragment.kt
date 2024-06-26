package com.xluo.nops.ui.home

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.xluo.core.utils.dp
import com.xluo.lib_base.ui.BaseViewModelFragment
import com.xluo.lib_base.view.GridSpacingItemDecoration
import com.xluo.nops.adapter.DraftListAdapter
import com.xluo.nops.databinding.FragmentHomeBinding
import com.xluo.nops.db.entity.DraftEntity
import com.xluo.nops.ui.activity.NewCanvasActivity
import com.xluo.lib_base.listener.OnListClickListener
import com.xluo.nops.hide
import com.xluo.nops.show
import com.xluo.nops.ui.activity.DrawBoardActivity

class HomeFragment : BaseViewModelFragment<HomeViewModel, FragmentHomeBinding>() {

    var draftListAdapter: DraftListAdapter? = null

    override fun createViewModel(): HomeViewModel {
        return HomeViewModel()
    }

    override fun createViewBinding(): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(layoutInflater)
    }

    override fun initView(containerView: View) {
        binding.tvStartDraw.setOnClickListener {
            startActivity(Intent(requireContext(), NewCanvasActivity::class.java))
        }
        binding.rvDraftView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvDraftView.addItemDecoration(GridSpacingItemDecoration(2, 16.dp.toInt(), true))
        draftListAdapter = DraftListAdapter(requireContext(), arrayListOf())
        binding.rvDraftView.adapter = draftListAdapter
        draftListAdapter?.setOnListClickListener { position, data ->
            val intent = Intent(requireContext(), DrawBoardActivity::class.java)
            intent.putExtra(DrawBoardActivity.EXTRA_IS_FROM_DRAFT, true)
            intent.putExtra(DrawBoardActivity.EXTRA_DRAFT_ID, data.id)
            startActivity(intent)
        }
        draftListAdapter?.onDraftDeleteListener = OnListClickListener<DraftEntity> { position, data ->
            model.deleteItem(data)
        }
        model.draftListData.observe(this) {
            draftListAdapter?.longClickPosition = -1
            draftListAdapter?.refreshData(it)
            if (it.isEmpty()) {
                binding.tvStartDraw.show()
            } else {
                binding.tvStartDraw.hide()
            }
        }

    }

    override fun initData() {
        model.loadDrafts()
    }

    override fun onResume() {
        super.onResume()
        initData()
    }
}