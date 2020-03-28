package moe.div.moeplayer.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author H-Mo
 * @desc 网络请求工具类
 */
public class HttpUtils {

    private static HttpUtils mInstance;
    private static OkHttpClient mClient;

    private HttpUtils(){}

    public static HttpUtils getInstance(){
        if(mInstance == null){
            synchronized (HttpUtils.class){
                if(mInstance == null){
                    mInstance = new HttpUtils();
                    // 缓存目录
                    File file = new File(Environment.getExternalStorageDirectory(), "a_cache");
                    // 缓存大小
                    int cacheSize = 10 * 1024 * 1024;
                    // 初始化Cookie拦截器
                    Interceptor addCookiesInterceptor = new Interceptor(){

                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request.Builder builder = chain.request().newBuilder();
                            // 取出保存的Cookie
                            HashSet<String> preferences = (HashSet<String>)
                                UIUtils.getContext()
                                    .getSharedPreferences("Cookie.sp", 0)
                                    .getStringSet("Cookie",new HashSet<String>());
                            // 添加到请求头中
                            for (String cookie : preferences) {
                                builder.addHeader("Cookie", cookie);
                                Log.v("OkHttp", "Adding Header: " + cookie);
                            }
                            // 放行
                            return chain.proceed(builder.build());
                        }
                    };
                    Interceptor receivedCookiesInterceptor = new Interceptor(){

                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Response originalResponse = chain.proceed(chain.request());
                            // 如果存在Set-Cookie响应头
                            if (!originalResponse.headers("Set-Cookie").isEmpty()) {
                                // 将Cookie保存进集合
                                HashSet<String> cookies = new HashSet<>();
                                for (String header : originalResponse.headers("Set-Cookie")) {
                                    cookies.add(header);
                                }
                                // 将集合保存到SP中
                                UIUtils.getContext()
                                    .getSharedPreferences("Cookie.sp", 0)
                                    .edit()
                                    .putStringSet("Cookie",cookies)
                                    .apply();
                            }
                            // 放行
                            return originalResponse;
                        }
                    };
                    Interceptor netCacheInterceptor = new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request request = chain.request();
                            Response originResponse = chain.proceed(request);

                            if (!TextUtils.isEmpty(request.header("Cache-Control"))){
                                String cacheHeader = request.header("Cache-Control");
                                if(cacheHeader != null){
                                    LogUtil.i("执行缓存");
                                    originResponse = originResponse.newBuilder()
                                        .removeHeader("pragma")
                                        .header("Cache-Control", cacheHeader)
                                        .build();
                                }
                            }
                            return originResponse;
                        }
                    };
                    mClient = new OkHttpClient.Builder()
                        .addInterceptor(addCookiesInterceptor)
                        .addInterceptor(receivedCookiesInterceptor)
                        .addNetworkInterceptor(netCacheInterceptor)
                        .cache(new Cache(file, cacheSize)) // 配置缓存
                        .build();
                }
            }
        }
        return mInstance;
    }


    /**
     * 发起一个 Get 请求，异步执行
     * @param url   链接
     * @param httpResponse 响应回调
     */
    public void getData(String url, final HttpResponse httpResponse){
        getData(url, null, httpResponse);
    }

    /**
     * 可指定接收数据的字符编码,常用与请求网页HTML
     * @param url
     * @param encode        GB2312 | UTF-8
     * @param httpResponse
     */
    public void getData(String url, String encode, final HttpResponse httpResponse){
        getData(url, 0, encode, httpResponse);
    }

    public void getData(String url, int cache, String encode, final HttpResponse httpResponse){
        Request request;
        if(cache == 0){
            // 创建请求对象
            request = new Request.Builder()
                .url(url)
                .build();
        }else {
            // 缓存控制类
            CacheControl cacheControl = new CacheControl.Builder()
                .maxStale(cache, TimeUnit.SECONDS)
                .maxAge(cache, TimeUnit.SECONDS)
                .build();
            // 创建请求对象
            request = new Request.Builder()
                .url(url)
                .cacheControl(cacheControl)
                .build();
        }

        // 异步请求数据
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                httpResponse.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    String data = "";
                    if(encode == null || "".equals(encode)){
                        data = response.body().string();
                    }else {
                        byte[] b = response.body().bytes();  // 获取数据的bytes
                        data = new String(b, encode); // 转换编码
                    }
                    httpResponse.parse(data);
                }else {
                    httpResponse.onError("请求失败");
                }
            }
        });
    }


    /**
     * 发起一个 Get 请求，同步执行
     * @param url       链接
     * @return
     * @throws IOException
     */
    public String getDataSync(String url) throws IOException {
        // 创建请求对象
        Request request = new Request.Builder().url(url).build();
        // 同步请求数据
        Response response = mClient.newCall(request).execute();
        if(response.isSuccessful()){
            return response.body().string();
        }
        return null;
    }

    /**
     * 请求网络数据，POST请求
     * @param url
     * @param map
     * @param httpResponse
     */
    public void postData(String url,Map<String,String> map, final HttpResponse httpResponse){
        postData(url, map, null, httpResponse);
    }

    /**
     * 请求网络数据，POST请求,可指定接收数据的字符编码,常用与请求网页HTML
     * @param url
     * @param map
     * @param encode    GB2312 | UTF-8
     * @param httpResponse
     */
    public void postData(String url,Map<String,String> map, String encode, final HttpResponse httpResponse){
        // 创建请求的参数body
        FormBody.Builder builder = new FormBody.Builder();
        // 遍历key
        if (null != map) {
            for (Map.Entry<String, String> entry : map.entrySet()) {

                System.out.println("Key = " + entry.getKey() + ", Value = "
                    + entry.getValue());
                builder.add(entry.getKey(), entry.getValue().toString());

            }
        }
        RequestBody body = builder.build();

        Request request = new Request.Builder().url(url).post(body).build();

        mClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                httpResponse.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String data = "";
                    if(encode == null || "".equals(encode)){
                        data = response.body().string();
                    }else {
                        byte[] b = response.body().bytes();  // 获取数据的bytes
                        data = new String(b, encode); // 转换编码
                    }
                    httpResponse.parse(data);
                } else {
                    httpResponse.onError(response.message());
                }
            }

        });
    }

    /**
     * 上传文件，异步执行
     * @param url           后台接口
     * @param file          文件对象
     * @param httpResponse  响应回调
     */
    public void updateFile(String url, File file, final HttpResponse httpResponse){
        // 修改MINI-TYPE
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        // 创建请求主体
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addPart(Headers.of(
                        "Content-Disposition",
                        "form-data; name=\"file\"; filename=\"" + file.getName() + "\""), fileBody)
                .build();
        // 将请求主体添加请求中
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Call call = mClient.newCall(request);
        // 异步执行
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                httpResponse.onError(e.getMessage());
                Log.e("mo--", "failure upload!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                httpResponse.parse(response.body().string());
            }
        });
    }


    /**
     * 清除Cookies
     */
    public void clearCookies(){
        UIUtils.getContext()
            .getSharedPreferences("Cookie.sp", 0)
            .edit()
            .putStringSet("Cookie",new HashSet<>())
            .apply();
    }

    /**
     * 将cookie同步到WebView
     * @param url WebView要加载的url
     * @param context
     * @return true 同步cookie成功，false同步cookie失败
     * @Author JPH
     */
    public boolean syncCookie(Context context, String url) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(context);
        }
        CookieManager cookieManager = CookieManager.getInstance();
        //如果没有特殊需求，这里只需要将session id以"key=value"形式作为cookie即可
        // 取出保存的Cookie
        HashSet<String> preferences = (HashSet<String>)
            UIUtils.getContext()
                .getSharedPreferences("Cookie.sp", 0)
                .getStringSet("Cookie",new HashSet<String>());
        // 添加到请求头中
        for (String cookie : preferences) {
            cookieManager.setCookie(url, cookie);
            Log.v("OkHttp", "Adding Header: " + cookie);
        }

        String newCookie = cookieManager.getCookie(url);
        return !TextUtils.isEmpty(newCookie);
    }
}

