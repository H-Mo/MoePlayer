package moe.div.moeplayer.utils;

/**
 * @author H-Mo
 * @desc 相应网络请求的回调
 */
public abstract class HttpResponse<T> {
    Class<T> mClz;

    public HttpResponse(Class<T> clz){
        this.mClz = clz;
    }

    /**
     * 解析json
     * @param json
     */
    public void  parse(String json){
        // 如果不需要解析，直接返回 json 字符串
        if(mClz == String.class){
            onSuccess((T) json);
            return;
        }
        // 解析成对象
        T t = JsonUtil.parseObject(json, mClz);
        // 判断是否为空
        if (t == null) {
            onError("解析失败");
        }else {
            onSuccess(t);
        }
    }

    /**
     * 发生错误
     * @param msg 错误消息
     */
    public abstract void  onError(String msg);

    /**
     * 请求得到响应，并解析成功
     * @param t 解析后得到的对象
     */
    public abstract void  onSuccess(T t);
}
