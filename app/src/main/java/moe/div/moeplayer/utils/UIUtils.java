package moe.div.moeplayer.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Process;
import android.widget.Toast;

import moe.div.moeplayer.App;


/**
 * @author H-Mo
 * @desc UI相关的工具方法
 */
public class UIUtils {
    /**
     * 获取应用的上下文
     * @return
     */
    public static Context getContext(){
        return App.getContext();
    }

    /**
     * 获取应用的Resources对象
     * @return
     */
    public static Resources getResources(){
        return getContext().getResources();
    }

    /**
     * 获取String.xml的字符串
     * @param resId 资源ID
     * @return  字符串
     */
    public static String  getString(int resId){
        return getResources().getString(resId);
    }

    /**
     * 获取String.xml的字符串,带占位符
     * @param resId
     * @param formatArgs
     * @return
     */
    public static String  getString(int resId,Object... formatArgs){
        return getResources().getString(resId,formatArgs);
    }

    /**
     * 获取String.xml的字符串
     * @param resId 资源ID
     * @return  字符串数组
     */
    public static String[] getStringArr(int resId){
        return getResources().getStringArray(resId);
    }

    /**
     * 获取color.xml的颜色值
     * @param resId 资源ID
     * @return 颜色的值
     */
    public static int getColor(int resId){
        return getResources().getColor(resId);
    }

    /**
     * 获取应用的包名
     * @return 包名
     */
    public static String getPackageName() {
        return getContext().getPackageName();
    }

    /**
     * 获得主线程的Handler
     * @return
     */
    public static Handler getHandler(){
        return App.getHandler();
    }

    /**
     * 获得主线程的ID
     * @return
     */
    public static int getMainThreadID(){
        return App.getMainThreadId();
    }

    /**
     * 安全地执行一个方法，确保在主线程中执行
     * @param task 需要执行的方法
     */
    public static void postTaskSafely(Runnable task){
        // 得到当前线程
        int currentThreadID = Process.myTid();
        if(getMainThreadID() == currentThreadID){
            // 主线程中
            task.run();
        }else {
            // 子线程中
            getHandler().post(task);
        }
    }

    /**
     * dip 转换成 像素
     * @param dip   DIP
     * @return 像素
     */
    public static int dip2Px(int dip) {
        float density = getResources().getDisplayMetrics().density;
        int ppi = getResources().getDisplayMetrics().densityDpi;
        int px = (int) (dip * density + .5f);
        return px;
    }

    /**
     * 像素 转换成 DIP
     * @param px 像素
     * @return DIP
     */
    public static int px2Dip(int px){
        float density = getResources().getDisplayMetrics().density;
        int dp = (int) (px * density + .5f);
        return dp;
    }

    /**
     * 此方法在主线程或子线程都可以进行Toast显示
     * @param context
     * @param msg   显示内容
     */
    public static void showToast(final Activity context, final String msg){
        if("main".equals(Thread.currentThread().getName())){
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }else{
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
