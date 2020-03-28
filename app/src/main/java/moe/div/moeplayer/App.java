package moe.div.moeplayer;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Process;

/**
 * @author 林墨
 * @time 19/5/20  13:22
 * @desc 应用的入口
 */
public class App extends Application {

    private static Context mContext;    // 应用的上下文
    private static Handler mHandler;    // 主线程的Handler
    private static int mMainThreadId;   // 主线程的线程ID

    /**
     * 获取应用的上下文
     * @return
     */
    public static Context getContext() {
        return mContext;
    }

    /**
     * 获取主线程的Handler
     * @return
     */
    public static Handler getHandler() {
        return mHandler;
    }

    /**
     * 获取主线程的线程ID
     * @return
     */
    public static int getMainThreadId() {
        return mMainThreadId;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 保存一些需要的数据
        mContext = getApplicationContext();
        mHandler = new Handler();
        mMainThreadId = Process.myTid();
    }


}
