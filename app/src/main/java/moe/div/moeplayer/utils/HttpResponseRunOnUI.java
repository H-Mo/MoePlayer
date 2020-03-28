package moe.div.moeplayer.utils;


import android.util.Log;

/**
 * @author 林墨
 * @Time 17/3/10  18:34
 * @Desc 网络请求回调类，在UI线程中进行回调,并直接返回字符串
 */
public abstract class HttpResponseRunOnUI extends HttpResponse<String> {

    public HttpResponseRunOnUI() {
        super(String.class);
    }

    @Override
    public void onError(final String msg) {
        UIUtils.postTaskSafely(new Runnable() {
            @Override
            public void run() {
                onErrorRunOnUI(msg);
            }
        });
    }

    @Override
    public void onSuccess(final String s) {
        UIUtils.postTaskSafely(new Runnable() {
            @Override
            public void run() {
                if(s.contains("系统繁忙")) {
                    onErrorRunOnUI("系统繁忙");
                    return;
                }
                Log.i("mo--", "json=" + s);
                onSuccessRunOnUI(s);
            }
        });
    }

    @Override
    public void parse(final String json) {
        UIUtils.postTaskSafely(new Runnable() {
            @Override
            public void run() {
                if(json.contains("系统繁忙")) {
                    onErrorRunOnUI("系统繁忙");
                    return;
                }
                Log.i("mo--", "json=" + json);
                onSuccessRunOnUI(json);
            }
        });
    }

    public abstract void onErrorRunOnUI(String msg);
    public abstract void onSuccessRunOnUI(String json);
}
