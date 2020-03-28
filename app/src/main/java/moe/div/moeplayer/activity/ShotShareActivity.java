package moe.div.moeplayer.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.File;

import moe.div.moeplayer.R;
import moe.div.moeplayer.utils.ImageUtils;
import moe.hmo.mobase.activity.BaseActivity;

/**
 * @author 林墨
 * @time 19/10/16  22:27
 * @desc 截图分享界面
 */
public class ShotShareActivity extends BaseActivity {

    public static final String TAG_ImagePath = "TAG_ImagePath";

    private ImageView image;
    private ImageView back;
    private ImageView share;
    private String mImagePath;

    @Override
    protected void initView() {
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_shot_share);

        image = findViewById(R.id.image);
        back = findViewById(R.id.back);
        share = findViewById(R.id.share);
    }

    @Override
    protected void initData() {
        super.initData();
        // 取出传递过来的数据
        mImagePath = getIntent().getStringExtra(TAG_ImagePath);
        // 加载图片
        ImageUtils.load(this, new File(mImagePath), image);
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        // 回退
        back.setOnClickListener(v -> onBackPressed());
        // 分享
        share.setOnClickListener(v -> {
            Intent imageIntent = new Intent(Intent.ACTION_SEND);
            imageIntent.setType("image/jpeg");
            imageIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(mImagePath));
            startActivity(Intent.createChooser(imageIntent, "分享"));
        });
    }

    @Override
    protected void handleMyMessage(Message msg) {

    }

}
