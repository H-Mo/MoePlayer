package moe.div.moeplayer.activity;

import android.os.Build;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import moe.div.moeplayer.R;
import moe.div.moeplayer.utils.StringUtil;
import moe.hmo.mobase.activity.BaseActivity;

/**
 * @author 林墨
 * @time 19/4/20  16:25
 * @desc 加载网页使用
 */
public class WebActivity extends BaseActivity {

    public static final String TAG_Title = "TAG_Title";
    public static final String TAG_Url = "TAG_Url";

    private Toolbar toolbar;
    private ImageView toolbar_back;
    private TextView toolbar_title;

    private RelativeLayout progress_rl;
    private WebView webView;

    private String mTitle;
    private String mUrl;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_web);

        toolbar = findViewById(R.id.toolbar);
        toolbar_back = findViewById(R.id.toolbar_back);
        toolbar_title = findViewById(R.id.toolbar_title);

        progress_rl = findViewById(R.id.progress_rl);
        webView = findViewById(R.id.webView);
    }

    @Override
    protected void initData() {
        super.initData();
        // 取出传递过来的标题和网址
        mTitle = getIntent().getStringExtra(TAG_Title);
        mUrl = getIntent().getStringExtra(TAG_Url);
        // 初始化
        initWevView();
        // 设置标题
        StringUtil.safeSetText(toolbar_title, mTitle);
        // 加载页面
        webView.loadUrl(mUrl);
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        // 回退
        toolbar_back.setOnClickListener(v -> onBackPressed());
        // 监听 WebView 处理重定向
        webView.setWebViewClient(new WebViewClient() {

            // 设置重定向监听
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // 显示页面
                progress_rl.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void handleMyMessage(Message msg) {

    }

    private void initWevView() {
        // WEBView相关
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        } else {
            webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        }
        // 允许执行js
        webView.getSettings().setJavaScriptEnabled(true);
        // 设置可以支持缩放
        webView.getSettings().setSupportZoom(true);
        // 设置出现缩放工具
        webView.getSettings().setBuiltInZoomControls(true);
        // 隐藏Zoom缩放按钮
        webView.getSettings().setDisplayZoomControls(false);
        // 允许本地缓存
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        // 自适应屏幕
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack();
            return;
        }
        super.onBackPressed();
    }

}
