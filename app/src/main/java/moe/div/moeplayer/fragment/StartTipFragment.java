package moe.div.moeplayer.fragment;

import android.Manifest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;

import moe.div.moeplayer.App;
import moe.div.moeplayer.R;
import moe.div.moeplayer.event.ChangePageEvent;
import moe.hmo.mobase.fragment.BaseFragment;

/**
 * @author 林墨
 * @time 20/2/2  12:59
 * @desc 首次进入申请权限页面
 */
public class StartTipFragment extends BaseFragment {

    private RelativeLayout start_rl;
    private LinearLayout start_ll;

    private RxPermissions rxPermissions;

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start_tip, container, false);
        start_rl = view.findViewById(R.id.start_rl);
        start_ll = view.findViewById(R.id.start_ll);
        return view;
    }

    @Override
    protected void initData() {
        super.initData();
        rxPermissions = new RxPermissions(this);
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        // 加载数据点击事件
        View.OnClickListener loadDataEvent = v -> {
            // 请求权限
            rxPermissions
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        // 获得权限
                        // 通知切换成数据列表页面
                        EventBus.getDefault().post(new ChangePageEvent(ChangePageEvent.Page_Data_List));
                    } else {
                        // 没有权限
                        showErrorDialog("能赐予我权限吗？");
                    }
                });
        };
        start_ll.setOnClickListener(loadDataEvent);

    }
}
