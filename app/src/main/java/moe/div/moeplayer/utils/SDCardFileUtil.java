package moe.div.moeplayer.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.shuyu.gsyvideoplayer.utils.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import moe.div.moeplayer.bean.FileItem;
import moe.div.moeplayer.bean.VideoItem;

/**
 * @author 林墨
 * @time 19/5/26  20:57
 * @desc 手机SD卡文件工具类
 */
public class SDCardFileUtil {

    /**
     * 获取SD卡中的视频文件数据，耗时操作注意不要在主线程中使用
     * @param context   上下文
     * @return          视频文件数据集合
     */
    public static List<VideoItem> getVideos(Context context) {
        // 准备容器
        List<VideoItem> data = new ArrayList<>();
        // 查询数据
        Uri videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        ContentResolver cr = context.getContentResolver();
        String[] thumbColumns = {
            MediaStore.Video.Thumbnails.DATA,
            MediaStore.Video.Thumbnails.VIDEO_ID
        };
        String[] projection = new String[]{
            MediaStore.Video.VideoColumns.DATA, MediaStore.Video.VideoColumns.DURATION,
            MediaStore.Video.VideoColumns.DISPLAY_NAME, MediaStore.Video.VideoColumns.DATE_ADDED,
            MediaStore.Video.Media._ID, MediaStore.Images.ImageColumns.SIZE
        };
        Cursor cursor = cr.query(videoUri, projection, null, null, null);
        if (cursor == null) {
            // 查询失败返回空集合
            return data;
        }
        // 创建实体对象存储数据并添加到集合中
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                String name = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                String date = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED));
                String size = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
                long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));

                VideoItem item = new VideoItem(path, name, size, date, duration);
                item.setType(FileItem.Type.Video);

                try {
                    int id = cursor.getInt(cursor
                        .getColumnIndex(MediaStore.Video.Media._ID));
                    Cursor thumbCursor = context.getContentResolver().query(
                        MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                        thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID
                            + "=" + id, null, null);
                    if (thumbCursor.moveToFirst()) {
                        item.setThumbPath(thumbCursor.getString(thumbCursor
                            .getColumnIndex(MediaStore.Video.Thumbnails.DATA)));
                    }
                    thumbCursor.close();
                }catch (Exception e){
                    e.printStackTrace();
                }

                data.add(item);
            }
        }
        // 关闭
        cursor.close();
        // 返回数据
        return data;
    }

    /**
     * 分页查询手机中的视频
     * @param context       上下文
     * @param page          页码
     * @param limit         每页条数
     * @return      视频文件数据集合
     */
    public static List<VideoItem> getVideos(Context context, int page, int limit) {
        String sortOrder = MediaStore.Video.VideoColumns.DATE_ADDED + " desc "
            + "limit " + limit + " offset " + (limit * (page - 1));
        // 准备容器
        List<VideoItem> data = new ArrayList<>();
        // 查询数据
        Uri videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        ContentResolver cr = context.getContentResolver();
        String[] thumbColumns = {
            MediaStore.Video.Thumbnails.DATA,
            MediaStore.Video.Thumbnails.VIDEO_ID
        };
        String[] projection = new String[]{
            MediaStore.Video.VideoColumns.DATA, MediaStore.Video.VideoColumns.DURATION,
            MediaStore.Video.VideoColumns.DISPLAY_NAME, MediaStore.Video.VideoColumns.DATE_ADDED,
            MediaStore.Video.Media._ID, MediaStore.Images.ImageColumns.SIZE
        };
        Cursor cursor = cr.query(videoUri, projection, null, null, sortOrder);
        if (cursor == null) {
            // 查询失败返回空集合
            return data;
        }
        // 创建实体对象存储数据并添加到集合中
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                String name = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                String date = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED));
                String size = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
                long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));

                VideoItem item = new VideoItem(path, name, size, date, duration);
                item.setType(FileItem.Type.Video);

                try {
                    int id = cursor.getInt(cursor
                        .getColumnIndex(MediaStore.Video.Media._ID));
                    Cursor thumbCursor = context.getContentResolver().query(
                        MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                        thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID
                            + "=" + id, null, null);
                    if (thumbCursor.moveToFirst()) {
                        item.setThumbPath(thumbCursor.getString(thumbCursor
                            .getColumnIndex(MediaStore.Video.Thumbnails.DATA)));
                    }
                    thumbCursor.close();
                }catch (Exception e){
                    e.printStackTrace();
                }

                data.add(item);
            }
        }
        // 关闭
        cursor.close();
        // 返回数据
        return data;
    }

    // 得到视频地址
    public static String getVideoPath(Context context, Uri uri) {
        String path = "";
        if(uri.getScheme().toString().compareTo("content") ==0) {
            String[] projection = new String[]{
                MediaStore.Video.VideoColumns.DATA
            };
            Cursor cursor = context.getContentResolver().query(uri,projection,null,null,null);
            if (cursor == null) {
                // 查询失败返回空
                return path;
            }
            if(cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
            }
            // 关闭
            cursor.close();
            // 返回路径
            return path;
        }else if(uri.getScheme().compareTo("file") ==0) {
            return uri.getPath();
        }
        return uri.toString();
    }

    // 保存图片为文件
    public static String saveBitmap(Bitmap bitmap) throws FileNotFoundException {
        if (bitmap != null) {
            File storageDirectory = Environment.getExternalStorageDirectory();
            File dir = new File(storageDirectory, "次元播放器");
            if(!dir.exists()){
                dir.mkdir();
            }
            File file = new File(dir, "次元播放器-" + System.currentTimeMillis() + ".jpg");
            OutputStream outputStream;
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            bitmap.recycle();
            return file.getAbsolutePath();
        }
        return null;
    }
}
