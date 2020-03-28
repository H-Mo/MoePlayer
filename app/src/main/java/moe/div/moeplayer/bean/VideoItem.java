package moe.div.moeplayer.bean;

import android.support.annotation.NonNull;

/**
 * @author 林墨
 * @time 19/5/26  21:04
 * @desc SD卡中的视频文件实体类
 */
public class VideoItem extends FileItem{

    private long mDuration;      // 视频时长
    private String mThumbPath;   // 视频缩略图

    public VideoItem(@NonNull String path, String name, String size, String date, long duration) {
        super(path, name, size, date);
        mDuration = duration;
    }

    public long getDuration() {
        return mDuration;
    }

    public void setDuration(long duration) {
        mDuration = duration;
    }

    public String getThumbPath() {
        return mThumbPath;
    }

    public void setThumbPath(String thumbPath) {
        mThumbPath = thumbPath;
    }
}
