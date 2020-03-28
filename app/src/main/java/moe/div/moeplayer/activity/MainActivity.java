package moe.div.moeplayer.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;

import io.reactivex.disposables.Disposable;
import moe.div.moeplayer.R;
import moe.div.moeplayer.constant.UrlConstant;
import moe.div.moeplayer.event.ChangePageEvent;
import moe.div.moeplayer.fragment.DataListFragment;
import moe.div.moeplayer.fragment.FolderListFragment;
import moe.div.moeplayer.fragment.StartTipFragment;
import moe.div.moeplayer.utils.AppUtils;
import moe.div.moeplayer.utils.FragmentUtils;
import moe.div.moeplayer.utils.HttpHelper;
import moe.div.moeplayer.utils.HttpResponseRunOnUI;
import moe.div.moeplayer.utils.JsonUtil;
import moe.div.moeplayer.utils.LogUtil;
import moe.hmo.mobase.activity.BaseActivity;

/**
 * @author 林墨
 * @time 19/5/20  13:22
 * @desc 主界面
 */
public class MainActivity extends BaseActivity{

    private DrawerLayout rootDrawer;

    private Toolbar toolbar;
    private ImageView mode_iv;
    private ScrollView drawer_sv;

    private LinearLayout net_video_ll;
    private LinearLayout tucao_ll;
    private LinearLayout github_ll;
    private LinearLayout blog_ll;
    private LinearLayout gift_me_ll;
    private LinearLayout group_add_ll;

    final RxPermissions rxPermissions = new RxPermissions(this);

    private StartTipFragment mStartTipFragment;
    private DataListFragment mDataListFragment;
    private FolderListFragment mFolderListFragment;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_main);
        // 上车
        EventBus.getDefault().register(this);

        rootDrawer = findViewById(R.id.rootDrawer);

        toolbar = findViewById(R.id.toolbar);
        mode_iv = findViewById(R.id.mode_iv);

        drawer_sv = findViewById(R.id.drawer_sv);

        net_video_ll = findViewById(R.id.net_video_ll);
        tucao_ll = findViewById(R.id.tucao_ll);
        github_ll = findViewById(R.id.github_ll);
        blog_ll = findViewById(R.id.blog_ll);
        gift_me_ll = findViewById(R.id.gift_me_ll);
        group_add_ll = findViewById(R.id.group_add_ll);

        // 抽屉布局与Toolbar绑定在一起
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, rootDrawer, toolbar, 0, 0);
        rootDrawer.addDrawerListener(toggle);
        toggle.syncState();
    }



    @Override
    protected void initData() {
        super.initData();
        // 初始化Fragment
        mStartTipFragment = new StartTipFragment();
        mDataListFragment = new DataListFragment();
        mFolderListFragment = new FolderListFragment();
        // 检查是否获得了权限
        if (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) {
            // 先展示首次打开视图
            FragmentUtils.replaceFragment(R.id.fl, mStartTipFragment, this);
        } else {
            // 暂时数据列表视图
            FragmentUtils.replaceFragment(R.id.fl, mDataListFragment, this);
        }
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        // 切换列表模式
        mode_iv.setOnClickListener(v -> {
            mode_iv.setSelected(!mode_iv.isSelected());
            if(mode_iv.isSelected()){
                // 切换成文件夹
                EventBus.getDefault().post(new ChangePageEvent(ChangePageEvent.Page_Explorer));
            }else {
                // 切换回列表
                EventBus.getDefault().post(new ChangePageEvent(ChangePageEvent.Page_Data_List));
            }
        });
        // 网络视频播放器
        net_video_ll.setOnClickListener(v -> {
            rootDrawer.closeDrawer(Gravity.START);
            Intent intent = new Intent(this, NetVideoPlayActivity.class);
            startActivity(intent);
        });
        // 吐槽
        tucao_ll.setOnClickListener(v -> {
            Intent intent = new Intent(this, WebActivity.class);
            intent.putExtra(WebActivity.TAG_Title, "吐槽与建议");
            String url = UrlConstant.Tucao_Web;
            intent.putExtra(WebActivity.TAG_Url, url);
            startActivity(intent);
        });
        // github
        github_ll.setOnClickListener(v -> AppUtils.openWebUrl(this, "http://div.moe"));
        // 博客
        blog_ll.setOnClickListener(v -> AppUtils.openWebUrl(this, "http://div.moe"));
        // 请喝饮料
        gift_me_ll.setOnClickListener(v -> {
            rootDrawer.closeDrawer(Gravity.START);
            Intent intent = new Intent(this, GiftActivity.class);
            startActivity(intent);
        });
        // 加入QQ群
        group_add_ll.setOnClickListener(v -> {
            rootDrawer.closeDrawer(Gravity.START);
            if(!AppUtils.joinQQGroup(this, "P1N32VTB_Z7OF8o7T7Q9H9obEUu7sLFh")){
                // 未检测到QQ
                showErrorDialog("未检测到QQ程序，请手动搜索QQ群号 721800134 加入聊天~");
            }
        });
    }

    @Override
    protected void handleMyMessage(Message msg) {

    }

    /**
     * 重写返回键方法
     * 若抽屉在打开状态，点返回键，只关抽屉，不退出程序。
     */
    @Override
    public void onBackPressed() {
        if (rootDrawer.isDrawerOpen(GravityCompat.START)) {
            rootDrawer.closeDrawer(GravityCompat.START);
        } else {
            showTwiceConfirmDialog("提示",
                "确定要退出吗?",
                (dialog, which) -> MainActivity.super.onBackPressed(),
                null
            );
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangePageEvent(ChangePageEvent event) {
        switch (event.getPage()) {
            case ChangePageEvent.Page_Start :
                FragmentUtils.replaceFragment(R.id.fl, mStartTipFragment, this);
                break;
            case ChangePageEvent.Page_Data_List :
                FragmentUtils.replaceFragment(R.id.fl, mDataListFragment, this);
                break;
            case ChangePageEvent.Page_Explorer :
                FragmentUtils.replaceFragment(R.id.fl, mFolderListFragment, this);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 下车
        EventBus.getDefault().unregister(this);
    }
}
