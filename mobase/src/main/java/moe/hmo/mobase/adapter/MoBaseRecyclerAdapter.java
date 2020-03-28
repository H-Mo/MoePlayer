package moe.hmo.mobase.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 林墨
 * @time 17/2/21  9:32
 * @desc RecyclerView 用的适配器基类
 */
public abstract class MoBaseRecyclerAdapter<T,VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH>{

    public static final int TYPE_COMMON_VIEW = 0x2001;
    public static final int TYPE_FOOTER_VIEW = 0x2002;

    protected List<T> mList;
    protected AdapterView.OnItemClickListener mClickListener;
    protected AdapterView.OnItemLongClickListener mLongClickListener;

    protected boolean isNoMoreData = false;
    protected boolean mOpenLoadMore = true;
    protected boolean hasFooterView = false;         // 是否存在尾部布局

    public void setHasFooterView(boolean has){
        hasFooterView = has;
    }

    @Override
    public int getItemViewType(int position) {
        if (isFooterView(position)) {
            return TYPE_FOOTER_VIEW;
        }
        return TYPE_COMMON_VIEW;
    }

    // 判断是不是尾部
    public boolean isFooterView(int position) {
        return hasFooterView && mOpenLoadMore && position >= getItemCount() - 1;
    }

    // 设置没有更多了,改变尾部的布局
    public void setFooterNoMoreData(boolean isNoMore){
        isNoMoreData = isNoMore;
    }

    public boolean isFooterNoMoreData(){
        return isNoMoreData;
    }

    // 设置数据
    public void setData(List<T> data){
        if(mList == null){
            mList = data;
        }else {
            // 清除原来的再添加，避免闪屏
            mList.clear();
            mList.addAll(data);
        }
    }

    // 追加数据
    public void addData(List<T> data){
        if(mList == null){
            mList = new ArrayList<T>();
        }
        mList.addAll(data);
    }

    public void addData(T data){
        if(mList == null){
            mList = new ArrayList<T>();
        }
        mList.add(data);
    }


    // 获取数据
    public List<T> getData(){
        if(mList == null){
            mList = new ArrayList<T>();
        }
        return mList;
    }

    // 通过索引删除一个子项
    public void deleteItem(int position){
        mList.remove(position);
    }

    // 通过对象删除一个Item
    public void deleteItem(T t){
        mList.remove(t);
    }


    // 获取一个子项对象
    public T getItem(int position){
        return mList.get(position);
    }

    // 设置子项点击事件监听器
    public void setOnItemClickListener(AdapterView.OnItemClickListener clickListener){
        mClickListener = clickListener;
    }

    // 设置子项长按事件监听器
    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener longClickListener){
        mLongClickListener = longClickListener;
    }

    @Override
    public void onBindViewHolder(final VH holder, final int position) {
        // 点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener == null) {
                    return;
                }
                if(isFooterView(position)){
                    // 不处理尾部布局的点击事件
                    return;
                }
                mClickListener.onItemClick(null, holder.itemView, position, getItemId(position));
            }
        });
        // 长按事件
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(isFooterView(position)){
                    // 不处理尾部布局的点击事件
                    return false;
                }
                return mLongClickListener != null &&
                        mLongClickListener.onItemLongClick(null,
                                holder.itemView,
                                position,
                                getItemId(position));
            }
        });
    }


    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size() ;
    }

}
