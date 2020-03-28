package moe.div.moeplayer.utils;

import android.content.Context;
import android.support.annotation.IdRes;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;

import moe.div.moeplayer.R;


/**
 * 类描述：加载图片工具类(仅仅包装一下，方便以后需要更换处理库时统一更换)
 * 创建人：林墨
 * 创建时间：2018/4/20.
 * 修改人：
 * 修改时间：
 */
public class ImageUtils {

    /**
     * 加载图片
     * @param context           上下文
     * @param url               图片地址
     * @param imageView         图片控件
     * @param def               默认显示的图片（失败时）
     */
    public static void load(Context context, String url, ImageView imageView, int def){
        if(context == null){
            return;
        }
        Glide.with(context)
                .load(url)
                .error(def)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    public static void load(Context context, String url, ImageView imageView){
        load(context, url, imageView, R.mipmap.toast_error);
    }



    /**
     * 加载一个本地图片
     * @param context
     * @param file
     * @param imageView
     */
    public static void load(Context context, File file, ImageView imageView){
        if(context == null){
            return;
        }
        Glide.with(context)
            .load(file)
            .into(imageView);
    }

}
