package moe.div.moeplayer.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import moe.div.moeplayer.R;
import moe.hmo.mobase.activity.BaseActivity;

/**
 * @author 林墨
 * @time 19/5/20  13:29
 * @desc 启动时的闪屏页
 */
public class SplashActivity extends BaseActivity {

    private static final long ANIMATION_TIME = 2000;    // 动画时间

    private ConstraintLayout root_cl;

    private LinearLayout logo_ll;
    private ImageView logo_iv;
    private TextView logo_tv;

    @Override
    protected void initView() {
        // APP在后台,用户又点击图标启动APP时
        if (!isTaskRoot()) {
            finish();
            return;
        }
        // 设置布局
        setContentView(R.layout.splash_activity);
        // 找到控件
        root_cl = findViewById(R.id.root_cl);

        logo_ll = findViewById(R.id.logo_ll);
        logo_iv = findViewById(R.id.logo_iv);
        logo_tv = findViewById(R.id.logo_tv);
        // 开始动画
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(ANIMATION_TIME);
        logo_ll.startAnimation(animation);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        // 倒计时：2秒
        Observable<String> timeObs =  Observable.timer(2, TimeUnit.SECONDS).map(aLong -> "time_out");
        // 监听事件
        timeObs.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<String>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(String s) {
                    switch (s) {
                        case "time_out":    // 动画时间已经结束
                            intentMain();
                            break;
                    }
                }

                @Override
                public void onError(Throwable e) {
                    // 出错也跳转主界面
                    intentMain();
                }

                @Override
                public void onComplete() {
                    
                }
            });

    }

    @Override
    protected void handleMyMessage(Message msg) {

    }


    // 跳转主界面
    private void intentMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
