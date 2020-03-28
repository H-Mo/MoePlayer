package moe.div.moeplayer.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import moe.div.moeplayer.R;
import moe.div.moeplayer.bean.FileItem;
import moe.div.moeplayer.bean.FolderItem;
import moe.div.moeplayer.utils.StringUtil;
import moe.hmo.mobase.adapter.MoBaseRecyclerAdapter;

/**
 * @author 林墨
 * @time 20/2/2  16:12
 * @desc 文件列表适配器
 */
public class FolderListAdapter extends MoBaseRecyclerAdapter<FolderItem, FolderListAdapter.FolderHolder> {

    @NonNull
    @Override
    public FolderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_file, parent, false);
        return new FolderHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FolderHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        FolderItem item = getItem(position);
        // 根据类型设置不同的图标
        switch (item.getType()) {
            case  "Prev":
                holder.icon.setImageResource(R.mipmap.file_prev);
                break;
            case  "Path":
                holder.icon.setImageResource(R.mipmap.file_folder);
                break;
            case  "Video":
                holder.icon.setImageResource(R.mipmap.file_video);
                break;
            default:
                // 未知文件
                holder.icon.setImageResource(R.mipmap.file_unknown);
        }
        String fileName = item.getTitle();
        StringUtil.safeSetText(holder.name, fileName);
    }

    public static class FolderHolder extends RecyclerView.ViewHolder{

        public ImageView icon;
        public TextView name;

        public FolderHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            name = itemView.findViewById(R.id.name);
        }
    }
}
