package com.ljz.alarm.activity;

import com.ljz.alarm.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

/**
 * @author LJZ 欢迎界面
 * 
 */
public class WelcomeActivity extends Activity {

	private LinearLayout launch;
	private Animation fadeIn;
	private Animation fadeInScale;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置无title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 设置全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.welcome);
		init();
		setListener();
	}

	private void setListener() {
		fadeIn.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				launch.setAnimation(fadeInScale);//当渐变动画结束后执行比例动画
			}
		});
		fadeInScale.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				// 动画完成后跳转到主界面
				Intent intent = new Intent(WelcomeActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();
				overridePendingTransition(android.R.anim.fade_in,
						android.R.anim.fade_out);
			}
		});
	}

	private void init() {
		launch = (LinearLayout) findViewById(R.id.launch);
		// 另外的一种动画的写法
		// AnimationSet animationSet = new AnimationSet(false);
		// AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f,1.0f);
		// alphaAnimation.setDuration(3000);
		// alphaAnimation.setFillAfter(true);
		// animationSet.addAnimation(alphaAnimation);
		// launch.setAnimation(animationSet);
		// 设置渐变动画
		fadeIn = AnimationUtils.loadAnimation(this, R.anim.welcome_fade_in);
		fadeIn.setDuration(3);// 设置渐变时间
		fadeIn.setFillAfter(true);// 保持动画结束状态
		// 设置比例动画
		fadeInScale = AnimationUtils.loadAnimation(this,
				R.anim.welcome_fade_in_scale);
		fadeInScale.setDuration(3);
		fadeInScale.setFillAfter(true);
		launch.startAnimation(fadeIn);// 开始动画
	}

	
	 // 屏蔽返回键
	 @Override
	 public boolean onKeyDown(int keyCode, KeyEvent event) {
	 if (keyCode == KeyEvent.KEYCODE_BACK) {
	 return true;
	 }
	 return super.onKeyDown(keyCode, event);
	 }

}
