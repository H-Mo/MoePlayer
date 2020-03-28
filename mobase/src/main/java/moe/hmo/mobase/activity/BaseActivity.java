package moe.hmo.mobase.activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import moe.hmo.mobase.R;
import moe.hmo.mobase.weiget.LoadingDialog;

/**
 * @author 林墨
 * Activity 基类，通用方法写在这里
 */
public abstract class BaseActivity extends AppCompatActivity {
	
	public static final int ERROR = 0xfff;
	
	protected Handler mHandler;

	protected LoadingDialog mProgressDialog;
	protected Toast mSucceedToast;
	protected TextView mSucceedToastHint;

	private int mShadowStartColor;
	private int mShadowEndColor;
	private int mInsetShadow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHandler = new MyHandler(this);
		initSaveData(savedInstanceState);
		initView();
		initController();
		initData();
		initEvent();
	}

	// 初始化保存的状态
	protected void initSaveData(Bundle savedInstanceState){

	}

    /**
     * 统一的成功吐司
     * @param text
     */
	public void showMoSucceedToast(String text){
        showMoToast(R.mipmap.toast_success, text);
	}

    /**
     * 统一的失败吐司
     * @param text
     */
    public void showMoErrorToast(String text){
        showMoToast(R.mipmap.toast_error, text);
    }

	/**
	 * 显示吐司
	 * @param text
	 */
	public void showToast(String text){
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 显示等待对话框
	 */
	public void showProgressDialog(){
		showProgressDialog(false);
	}

	public void showProgressDialog(boolean cancelable){
		showProgressDialog(false, null);
	}

	public void showProgressDialog(boolean cancelable, DialogInterface.OnCancelListener cancelListener){
		if(mProgressDialog != null){
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
		// 初始化等待对话框
		mProgressDialog = new LoadingDialog(this);
		mProgressDialog.setCancelable(cancelable);
		mProgressDialog.setOnCancelListener(cancelListener);
		mProgressDialog.show();
	}


	/**
	 * 隐藏等待对话框
	 */
	public void hideProgressDialog(){
		if(mProgressDialog != null && mProgressDialog.isShowing()){
			mProgressDialog.dismiss();
		}
	}

	// 显示成功 Toast
	public void showSucceedToast(){
		showSucceedToast(null);
	}

	// 显示成功 Toast
	public void showSucceedToast(String hint){
		mSucceedToast = Toast.makeText(getApplicationContext(), "Normarl toast", Toast.LENGTH_SHORT);
		mSucceedToast.setGravity(Gravity.CENTER, 0, 0);
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.toast_vote_succeed_layout, null);
		mSucceedToastHint = (TextView) view.findViewById(R.id.hint_text);
		mSucceedToast.setView(view);
		if(hint != null){
			mSucceedToastHint.setText(hint);
		}
		mSucceedToast.show();
	}

	// 显示提示吐司
	public void showMoToast(@DrawableRes int resId, String hint){
        mSucceedToast = Toast.makeText(getApplicationContext(), "Normarl toast", Toast.LENGTH_SHORT);
        mSucceedToast.setGravity(Gravity.CENTER, 0, 0);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.toast_info_layout, null);
        ImageView hintImage = (ImageView) view.findViewById(R.id.hint_image);
        TextView hintText = (TextView) view.findViewById(R.id.hint_text);
        mSucceedToast.setView(view);
        hintImage.setImageResource(resId);
        if(hint != null){
            hintText.setText(hint);
        }
        mSucceedToast.show();
    }

	/**
	 * 弹出二次确认对话框
	 * @param title         标题
	 * @param message       信息
	 * @param confirmCall   确认回调
	 * @param cancelCall    取消回调
	 */
	public void showTwiceConfirmDialog(String title, String message,
									   DialogInterface.OnClickListener confirmCall,
									   DialogInterface.OnClickListener cancelCall){
		AlertDialog alertDialog = new AlertDialog.Builder(this)
				.setTitle(title)
				.setMessage(message)
				.setPositiveButton("确定", confirmCall)
				.setNegativeButton("取消", cancelCall)
				.create();
		alertDialog.show();
	}

	public void showTwiceConfirmDialog(String title,
									   String message,
									   String confirmText,
									   String cancelText,
									   DialogInterface.OnClickListener confirmCall,
									   DialogInterface.OnClickListener cancelCall){

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		if(title != null && !"".equals(title)){
			builder.setTitle(title);
		}

		if(confirmText != null && !"".equals(confirmText)){
			builder.setPositiveButton(confirmText, confirmCall);
		}

		if(cancelText != null && !"".equals(cancelText)){
			builder.setNegativeButton(cancelText, cancelCall);
		}

		builder.setMessage(message);

        AlertDialog dialog = builder.create();

        dialog.show();

        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.rgb(51, 51, 51));
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.rgb(32, 146, 227));
	}


	/**
	 * 弹出错误提示对话框
	 * @param title			标题
	 * @param message		消息
	 * @param positive		确认按钮文本
	 * @param confirmCall	确认回调事件
     */
	public void showErrorDialog(String title, String message, String positive,
								DialogInterface.OnClickListener confirmCall){
		AlertDialog alertDialog = new AlertDialog.Builder(this)
				.setTitle(title)
				.setMessage(message)
				.setPositiveButton(positive, confirmCall)
                .setCancelable(false)
				.create();
		alertDialog.show();
	}

	public void showErrorDialog(String message){
		showErrorDialog("提示", message, "确定", null);
	}

	/**
	 * 该方法中必须setContentView
	 */
	protected abstract void initView();
	
	protected void initController() {
		// 空实现
	}
	
	protected void initData() {

	}
	
	protected void initEvent() {
		// 空实现
	}
	
	// 静态内部类的Handler
	private static class MyHandler extends Handler {
		
		WeakReference<BaseActivity> mWeakReference;
		
		public MyHandler(BaseActivity activity){
			mWeakReference = new WeakReference<BaseActivity>(activity);
		}
		
		@Override
		public void handleMessage(Message msg) {
			BaseActivity activity = mWeakReference.get();
			activity.handleMyMessage(msg);
		}
	}
	
	// 处理消息的方法
	protected abstract void handleMyMessage(Message msg);
}
