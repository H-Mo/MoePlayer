package moe.div.moeplayer.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import moe.div.moeplayer.constant.UrlConstant;

/**
 * @author 林墨
 * @time 18/11/28  20:43
 * @desc 用于执行网络请求
 */
public class HttpHelper {

    /**
     * 检查版本更新
     * @param callback      回调
     */
    public static void checkVersion(HttpResponse callback){
        String url = UrlConstant.CHECK_VERSION;
        HttpUtils.getInstance().getData(url, callback);
    }
}
