package moe.div.moeplayer.activity;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Message;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;

import moe.div.moeplayer.R;
import moe.div.moeplayer.utils.AppUtils;
import moe.div.moeplayer.utils.LogUtil;
import moe.div.moeplayer.utils.SDCardFileUtil;
import moe.div.moeplayer.view.MoGSYVideoPlayer;
import moe.hmo.mobase.activity.BaseActivity;

/**
 * @author 林墨
 * @time 19/6/23  16:53
 * @desc 网络视频播放界面
 */
public class NetVideoPlayActivity extends BaseActivity {

    private Toolbar toolbar;
    private ImageView toolbar_back;
    private TextView toolbar_title;

    private CardView play_cv;
    private MoGSYVideoPlayer player;

    private CardView input_cv;
    private EditText input_et;
    private TextView input_tv;

    private OrientationUtils orientationUtils;

    private boolean isPlay;
    private boolean isPause;

    private String mLink;

    @Override
    protected void initView() {
        // 设置布局
        setContentView(R.layout.activity_net_play);
        // 找到控件
        toolbar = findViewById(R.id.toolbar);
        toolbar_back = findViewById(R.id.toolbar_back);
        toolbar_title = findViewById(R.id.toolbar_title);

        play_cv = findViewById(R.id.play_cv);
        player = findViewById(R.id.player);

        input_cv = findViewById(R.id.input_cv);
        input_et = findViewById(R.id.input_et);
        input_tv = findViewById(R.id.input_tv);
    }

    @Override
    protected void initData() {
        super.initData();
        // 初始化播放器
        initPlayer();
        toolbar_title.setText("网络视频播放器");
        input_cv.setVisibility(View.VISIBLE);
        // 如果是从外部进来的
        Uri data = getIntent().getData();
        if(data != null){
            Log.i("mo--", "收到：" + data.toString());
            mLink = SDCardFileUtil.getVideoPath(this, data);
            Log.i("mo--", "mLink = " + mLink);
            // 显示链接到文本输入框中
            input_et.setText(mLink);
            // 播放
            videoPlay(mLink);
        }
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        // 回退事件
        toolbar_back.setOnClickListener(v -> onBackPressed());
        // 播放按钮
        input_tv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // 获取输入框文本
                String key = input_et.getText().toString();
                if("".equals(key.trim())){
                    showMoErrorToast("播放链接不能为空");
                    return;
                }
                // 关闭软键盘
                AppUtils.closeSoftInput(NetVideoPlayActivity.this);
                // 播放
                videoPlay(key);
            }

        });
    }

    @Override
    protected void handleMyMessage(Message msg) {

    }

    // 初始化播放器
    private void initPlayer() {
        //外部辅助的旋转，帮助全屏
        orientationUtils = new OrientationUtils(this, player);
        //初始化不打开外部的旋转
        orientationUtils.setEnable(false);
        //设置返回键
        player.getBackButton().setVisibility(View.GONE);

        GSYVideoOptionBuilder gsyVideoOption = new GSYVideoOptionBuilder();

        gsyVideoOption
            .setIsTouchWiget(true)
            .setRotateViewAuto(false)
            .setLockLand(false)
            .setAutoFullWithSize(true)
            .setShowFullAnimation(false)
            .setNeedLockFull(true)
            .setCacheWithPlay(false)
            .setNeedShowWifiTip(false)
            .setVideoAllCallBack(new GSYSampleCallBack() {
                @Override
                public void onPrepared(String url, Object... objects) {
                    super.onPrepared(url, objects);
                    // 开始播放了才能旋转和全屏
                    orientationUtils.setEnable(true);
                    isPlay = true;
                }

                @Override
                public void onQuitFullscreen(String url, Object... objects) {
                    super.onQuitFullscreen(url, objects);
                    // title
                    LogUtil.i("***** onQuitFullscreen **** " + objects[0]);
                    // 当前非全屏player
                    LogUtil.i("***** onQuitFullscreen **** " + objects[1]);
                    if (orientationUtils != null) {
                        orientationUtils.backToProtVideo();
                    }
                }
            }).setLockClickListener((view, lock) -> {
            if (orientationUtils != null) {
                //配合下方的onConfigurationChanged
                orientationUtils.setEnable(!lock);
            }
        }).build(player);

        player.getFullscreenButton().setOnClickListener(v -> {
            //直接横屏
            orientationUtils.resolveByClick();
            //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
            player.startWindowFullscreen(NetVideoPlayActivity.this, true, true);
        });
    }

    // 视频播放
    private void videoPlay(String pathOrLink){
        player.setUp(pathOrLink, true, "");
        player.startPlayLogic();
    }

    // 视频暂停
    private void videoPause(){
        player.getCurrentPlayer().onVideoPause();
        isPause = true;
    }

    // 视频终止
    private void videoStop(){
        GSYVideoManager.releaseAllVideos();
    }

    @Override
    public void onBackPressed() {
        if (orientationUtils != null) {
            orientationUtils.backToProtVideo();
        }
        if (GSYVideoManager.backFromWindowFull(this)) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        player.getCurrentPlayer().onVideoPause();
        super.onPause();
        isPause = true;
    }

    @Override
    protected void onResume() {
        player.getCurrentPlayer().onVideoResume(false);
        super.onResume();
        isPause = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isPlay) {
            player.getCurrentPlayer().release();
        }
        if (orientationUtils != null)
            orientationUtils.releaseListener();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //如果旋转了就全屏
        if (isPlay && !isPause) {
            player.onConfigurationChanged(this, newConfig, orientationUtils, true, true);
        }
    }

}
