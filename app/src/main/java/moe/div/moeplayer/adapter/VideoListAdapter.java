package moe.div.moeplayer.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import moe.div.moeplayer.R;
import moe.div.moeplayer.bean.VideoItem;
import moe.div.moeplayer.utils.ImageUtils;
import moe.div.moeplayer.utils.StringUtil;
import moe.hmo.mobase.adapter.MoBaseRecyclerAdapter;

/**
 * @author 林墨
 * @time 19/5/29  12:55
 * @desc 视频列表适配器
 */
public class VideoListAdapter extends MoBaseRecyclerAdapter<VideoItem, VideoListAdapter.VideoHolder> {

    @NonNull
    @Override
    public VideoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_video, parent, false);
        return new VideoHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        VideoItem item = getItem(position);

        String thumbPath = item.getThumbPath();
        if(thumbPath != null && !"".equals(thumbPath.trim())){
            File thumbFile = new File(thumbPath);
            if(thumbFile != null && thumbFile.exists()){
                ImageUtils.load(holder.itemView.getContext(), thumbFile, holder.image);
            }
        }else {
            holder.image.setImageResource(R.mipmap.logo);
        }

        StringUtil.safeSetText(holder.name, item.getFileName());

        StringUtil.safeSetText(holder.size, item.getFileSize());

        String strTime = StringUtil.long2VideoStrTime(item.getDuration());
        StringUtil.safeSetText(holder.duration, strTime);
    }

    public static class VideoHolder extends RecyclerView.ViewHolder{

        public ImageView image;
        public TextView name;
        public TextView duration;
        public TextView size;

        public VideoHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            duration = itemView.findViewById(R.id.duration);
            size = itemView.findViewById(R.id.size);
        }
    }

}
