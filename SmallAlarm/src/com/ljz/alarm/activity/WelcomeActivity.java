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
 * @author LJZ ��ӭ����
 * 
 */
public class WelcomeActivity extends Activity {

	private LinearLayout launch;
	private Animation fadeIn;
	private Animation fadeInScale;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ������title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// ����ȫ��
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
				launch.setAnimation(fadeInScale);//�����䶯��������ִ�б�������
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
				// ������ɺ���ת��������
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
		// �����һ�ֶ�����д��
		// AnimationSet animationSet = new AnimationSet(false);
		// AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f,1.0f);
		// alphaAnimation.setDuration(3000);
		// alphaAnimation.setFillAfter(true);
		// animationSet.addAnimation(alphaAnimation);
		// launch.setAnimation(animationSet);
		// ���ý��䶯��
		fadeIn = AnimationUtils.loadAnimation(this, R.anim.welcome_fade_in);
		fadeIn.setDuration(3);// ���ý���ʱ��
		fadeIn.setFillAfter(true);// ���ֶ�������״̬
		// ���ñ�������
		fadeInScale = AnimationUtils.loadAnimation(this,
				R.anim.welcome_fade_in_scale);
		fadeInScale.setDuration(3);
		fadeInScale.setFillAfter(true);
		launch.startAnimation(fadeIn);// ��ʼ����
	}

	
	 // ���η��ؼ�
	 @Override
	 public boolean onKeyDown(int keyCode, KeyEvent event) {
	 if (keyCode == KeyEvent.KEYCODE_BACK) {
	 return true;
	 }
	 return super.onKeyDown(keyCode, event);
	 }

}
