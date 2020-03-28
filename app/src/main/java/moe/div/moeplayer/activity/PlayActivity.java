package moe.div.moeplayer.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Message;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.utils.GSYVideoType;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import moe.div.moeplayer.R;
import moe.div.moeplayer.utils.AppUtils;
import moe.div.moeplayer.utils.LogUtil;
import moe.div.moeplayer.utils.SDCardFileUtil;
import moe.div.moeplayer.utils.UIUtils;
import moe.div.moeplayer.view.MoGSYVideoPlayer;
import moe.hmo.mobase.activity.BaseActivity;

/**
 * @author 林墨
 * @time 19/6/1  19:04
 * @desc 播放界面，两种模式，分别是本地文件模式和网络URL模式
 */
public class PlayActivity extends BaseActivity {

    public static final String Tag_Link = "Tag_Link";
    public static final String Tag_Title = "Tag_Title";

    private MoGSYVideoPlayer player;

    private OrientationUtils orientationUtils;

    private boolean isPlay;
    private boolean isPause;

    private String mLink;
    private String mTitle;

    @Override
    protected void initView() {
        // 设置布局
        setContentView(R.layout.activity_play);
        // 找到控件
        player = findViewById(R.id.player);
    }

    @Override
    protected void initData() {
        super.initData();
        // 初始化播放器
        initPlayer();
        // 取出传递过来的数据
        mLink = getIntent().getStringExtra(Tag_Link);
        mTitle = getIntent().getStringExtra(Tag_Title);
        // 如果是从外部进来的
        Uri data = getIntent().getData();
        if(data != null){
            Log.i("mo--", "收到：" + data.toString());
            mLink = SDCardFileUtil.getVideoPath(this, data);
        }
        Log.i("mo--", "mLink = " + mLink);
        // 本地视频模式
        if(mTitle == null || "".equals(mTitle)){
            mTitle = "次元播放器";
        }
        videoPlay(mLink);
    }

    @Override
    protected void initEvent() {
        super.initEvent();
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
        player.getBackButton().setOnClickListener(v -> onBackPressed());
        // 切换渲染
        GSYVideoType.setRenderType(GSYVideoType.GLSURFACE);

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

        // 全屏按钮
        player.getFullscreenButton().setOnClickListener(v -> {
            //直接横屏
            orientationUtils.resolveByClick();
            //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
            player.startWindowFullscreen(PlayActivity.this, true, true);
        });

    }

    // 视频播放
    private void videoPlay(String pathOrLink){
        player.setUp(pathOrLink, true, mTitle);
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
