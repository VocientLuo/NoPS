package com.xluo.lib_base.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import androidx.viewpager2.widget.ViewPager2
import java.lang.reflect.InvocationTargetException

///////////////////////////////配合viewBinding使用 start/////////////////////////////////////////////////////////////////////////////
/**
 * ViewBindViewHolder
 */
class ViewBindViewHolder<B : ViewBinding>(val bindView: B) : RecyclerView.ViewHolder(bindView.root)

class HeaderViewHolder(headerView: ViewBinding) : RecyclerView.ViewHolder(headerView.root)

class FooterViewHolder(footerView: ViewBinding) : RecyclerView.ViewHolder(footerView.root)


/**
 * Adapter适配器
 * 多item类型待实现
 */
abstract class ViewBindAdapter<T, B : ViewBinding>(diffCallback: DiffUtil.ItemCallback<T>) : ListAdapter<T, RecyclerView.ViewHolder>(diffCallback) {

    val typeHeader = 0
    val typeNormal = 1
    val typeFooter = 2

    var headerViewBinding: ViewBinding? = null

    var footerViewBinding: ViewBinding? = null

    override fun getItemCount(): Int {
        return when {
            headerViewBinding != null && footerViewBinding != null -> currentList.size + 2
            headerViewBinding != null && footerViewBinding == null -> currentList.size + 1
            headerViewBinding == null && footerViewBinding != null -> currentList.size + 1
            else -> currentList.size
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            headerViewBinding != null && position == 0 -> typeHeader
            footerViewBinding != null && position == itemCount - 1 -> typeFooter
            else -> typeNormal
        }
    }

    fun addHeader(headerViewBinding: ViewBinding) {
        this.headerViewBinding = headerViewBinding
        notifyItemInserted(0)
    }

    fun addFooter(footerViewBinding: ViewBinding) {
        this.footerViewBinding = footerViewBinding
        notifyItemInserted(itemCount - 1)
    }
}

/**
 * Adapter 参数
 */
class BindViewAdapterConfig<T, B : ViewBinding> {

    //layoutManger,默认
    var layoutManger: RecyclerView.LayoutManager? = null

    //一个item点击事件
    var mOnItemClick: ((itemViewHolder: B, itemData: T, position: Int) -> Unit)? = null

    //一个item长按事件
    var mOnItemLongClick: ((itemViewHolder: B, itemData: T, position: Int) -> Unit)? = null

    //itemDecoration
    var itemDecoration: ((outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) -> Unit)? = null

    //item布局数据绑定
    @Deprecated("bug:当使用remove删除item时，position不会更新，会造成位置错位，必须使用 adapter.notifyDataSetChanged()", replaceWith = ReplaceWith("bindView2"))
    var bindView: ((itemViewHolder: B, itemData: T, position: Int) -> Unit)? = null
    var bindView2: ((holder: RecyclerView.ViewHolder, itemView: B, itemData: T) -> Unit)? = null

    var headerViewBinding: ViewBinding? = null

    var footerViewBinding: ViewBinding? = null


    /**
     * 必须实现,item布局数据绑定
     */
    @Deprecated("bug:当使用remove删除item时，position不会更新，会造成位置错位，必须使用 adapter.notifyDataSetChanged()", ReplaceWith("onBindView2 { holder,itemView, itemData -> }"))
    fun onBindView(bindView: (itemViewHolder: B, itemData: T, position: Int) -> Unit) {
        this.bindView = bindView
    }

    fun onBindView2(bindView: (holder: RecyclerView.ViewHolder, itemView: B, itemData: T) -> Unit) {
        this.bindView2 = bindView
    }


    /**
     * itemDecoration
     */
    fun addItemDecoration(decoration: (outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) -> Unit) {
        itemDecoration = decoration
    }

    /**
     *一个item点击事件
     */
    fun onItemClick(onItemClick: (itemViewHolder: B, itemData: T, position: Int) -> Unit) {
        mOnItemClick = onItemClick
    }

    /**
     * 一个item长按事件
     */
    fun onItemLongClick(onItemLongClick: (itemViewHolder: B, itemData: T, position: Int) -> Unit) {
        mOnItemLongClick = onItemLongClick
    }

}

/**
 * 反射获取 ViewBinding 实例
 */
@Throws(NoSuchMethodException::class, InvocationTargetException::class, IllegalAccessException::class)
inline fun <reified B : ViewBinding> Class<B>.inflateViewBind(context: Context, parent: ViewGroup): B {
    val method = getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.javaPrimitiveType)
    return method.invoke(null, LayoutInflater.from(context), parent, false) as B
}

/**
 * RecyclerView Adapter 相关参数、功能部署
 */
inline fun <T, reified B : ViewBinding> RecyclerView.bindAdapter(
    dataList: MutableList<T>? = null,
    diffCallback: DiffUtil.ItemCallback<T>? = null,
    isRefresh: Boolean = false,
    config: BindViewAdapterConfig<T, B>.() -> Unit,
): ViewBindAdapter<T, B> {

    val adapterConfig = BindViewAdapterConfig<T, B>().apply(config)

    val bindAdapter = object : ViewBindAdapter<T, B>(diffCallback ?: DefaultDiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when (viewType) {
                typeHeader -> HeaderViewHolder(headerViewBinding!!)
                typeFooter -> FooterViewHolder(footerViewBinding!!)
                else -> ViewBindViewHolder(B::class.java.inflateViewBind(context, parent))
            }

        }

        @SuppressLint("NotifyDataSetChanged")
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            if (getItemViewType(holder.layoutPosition) == typeNormal) {
                val i = getBindPosition(holder)
                adapterConfig.bindView?.invoke((holder as ViewBindViewHolder<B>).bindView, getItem(i), i)
                adapterConfig.bindView2?.invoke(holder, (holder as ViewBindViewHolder<B>).bindView, getItem(i))

                holder.itemView.setOnClickListener {
                    val clickPosition = getBindPosition(holder)
                    adapterConfig.mOnItemClick?.invoke((holder as ViewBindViewHolder<B>).bindView, getItem(clickPosition), clickPosition)
                    if (isRefresh) {
                        notifyDataSetChanged()
                    }
                }

                holder.itemView.setOnLongClickListener {
                    val longClickPosition = getBindPosition(holder)
                    adapterConfig.mOnItemLongClick?.invoke((holder as ViewBindViewHolder<B>).bindView, getItem(longClickPosition), longClickPosition)
                    if (isRefresh) {
                        notifyDataSetChanged()
                    }
                    return@setOnLongClickListener true
                }
            }
        }

        fun getBindPosition(holder: RecyclerView.ViewHolder): Int {
            val currentPosition = holder.layoutPosition
            return if (null != headerViewBinding) currentPosition - 1 else currentPosition
        }

    }


    dataList?.let { bindAdapter.submitList(it) }

    adapterConfig.itemDecoration?.also {
        if (itemDecorationCount > 0) removeItemDecoration(getItemDecorationAt(0))
        addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                it.invoke(outRect, view, parent, state)
            }
        })
    }

    layoutManager = adapterConfig.layoutManger ?: LinearLayoutManager(context)

    adapter = bindAdapter

    adapterConfig.headerViewBinding?.let {
        bindAdapter.addHeader(it)
    }

    adapterConfig.footerViewBinding?.let {
        bindAdapter.addFooter(it)
    }


    return bindAdapter
}

/**
 * 不需要刷新数据时提供默认DiffUtil
 */
class DefaultDiffCallback<T> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T & Any, newItem: T & Any): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: T & Any, newItem: T & Any): Boolean {
        return oldItem.equals(newItem)
    }
}


inline fun <T, reified B : ViewBinding> ViewPager2.bindAdapter(diffCallback: DiffUtil.ItemCallback<T>, config: BindViewAdapterConfig<T, B>.() -> Unit): ViewBindAdapter<T, B> {

    val adapterConfig = BindViewAdapterConfig<T, B>().apply(config)

    val bindAdapter = object : ViewBindAdapter<T, B>(diffCallback) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewBindViewHolder<B> {
            return ViewBindViewHolder(B::class.java.inflateViewBind(context, parent))
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val currentPosition = holder.layoutPosition
            adapterConfig.bindView?.invoke((holder as ViewBindViewHolder<B>).bindView, getItem(currentPosition), currentPosition)
            adapterConfig.bindView2?.invoke(holder, (holder as ViewBindViewHolder<B>).bindView, getItem(currentPosition))

            holder.itemView.setOnClickListener {
                val clickPosition = holder.layoutPosition
                adapterConfig.mOnItemClick?.invoke((holder as ViewBindViewHolder<B>).bindView, getItem(clickPosition), clickPosition)
            }

            holder.itemView.setOnLongClickListener {
                val longClickPosition = holder.layoutPosition
                adapterConfig.mOnItemLongClick?.invoke((holder as ViewBindViewHolder<B>).bindView, getItem(longClickPosition), longClickPosition)
                return@setOnLongClickListener true
            }
        }

    }
    adapter = bindAdapter
    return bindAdapter
}

///////////////////////////////配合viewBinding使用 end/////////////////////////////////////////////////////////////////////////////
