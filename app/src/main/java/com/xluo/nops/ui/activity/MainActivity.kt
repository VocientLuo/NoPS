package com.xluo.nops.ui.activity

import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import com.xluo.core.utils.dp
import com.xluo.lib_base.listener.OnListClickListener
import com.xluo.lib_base.ui.BaseViewModelActivity
import com.xluo.lib_base.view.GridSpacingItemDecoration
import com.xluo.nops.adapter.DraftListAdapter
import com.xluo.nops.model.MainModel

import com.xluo.nops.databinding.ActivityDraftBinding
import com.xluo.nops.db.entity.DraftEntity

class MainActivity: BaseViewModelActivity<MainModel, ActivityDraftBinding>() {

    var draftListAdapter: DraftListAdapter? = null

    override fun createViewModel(): MainModel {
        return MainModel()
    }

    override fun createViewBinding(): ActivityDraftBinding {
        return ActivityDraftBinding.inflate(layoutInflater)
    }

    override fun initView() {
        binding.tvAdd.setOnClickListener {
            startActivity(Intent(this@MainActivity, NewCanvasActivity::class.java))
        }
        binding.rvDraftView.layoutManager = GridLayoutManager(this, 2)
        binding.rvDraftView.addItemDecoration(GridSpacingItemDecoration(2, 16.dp.toInt(), true))
        draftListAdapter = DraftListAdapter(this, arrayListOf())
        binding.rvDraftView.adapter = draftListAdapter
        draftListAdapter?.setOnListClickListener { position, data ->
            val intent = Intent(this@MainActivity, DrawBoardActivity::class.java)
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
        }
        model.loadDrafts()
    }

    override fun onResume() {
        super.onResume()
        model.loadDrafts()
    }
}