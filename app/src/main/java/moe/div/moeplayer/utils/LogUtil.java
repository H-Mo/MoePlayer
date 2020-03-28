package moe.div.moeplayer.utils;

import android.util.Log;

/**
 * @author 林墨
 * @time 19/1/4  22:53
 * @desc 打印日记工具,为了方便统一开启和关闭
 */
public class LogUtil {

    private final static boolean FLAG = true;

    public static void i(String msg){
        if(!FLAG){
            return;
        }
        Log.i("mo--", msg);
    }

}
