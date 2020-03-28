package moe.div.moeplayer.activity;

import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import moe.div.moeplayer.R;
import moe.hmo.mobase.activity.BaseActivity;

/**
 * @author 林墨
 * @time 19/6/7  16:18
 * @desc 礼物界面-请我喝饮料
 */
public class GiftActivity extends BaseActivity {

    private Toolbar toolbar;
    private ImageView toolbar_back;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_gift);

        toolbar = findViewById(R.id.toolbar);
        toolbar_back = findViewById(R.id.toolbar_back);
    }

    @Override
    protected void initData() {
        super.initData();
        setSupportActionBar(toolbar);
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        toolbar_back.setOnClickListener(v -> onBackPressed());
    }

    @Override
    protected void handleMyMessage(Message msg) {

    }
}
