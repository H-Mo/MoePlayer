package moe.div.moeplayer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * @author 林墨
 * @time 19/10/7  10:11
 * @desc 视频内容的广播接收者
 */
public class VideoBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.i("mo--", "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            sb.append("\nkey:").append(key).append(", value:").append(bundle.getString(key));
        }
        return sb.toString();
    }

}
