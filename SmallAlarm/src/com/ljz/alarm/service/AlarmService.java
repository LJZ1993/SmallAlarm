package com.ljz.alarm.service;

import java.io.IOException;

import com.ljz.alarm.common.Alarm;
import com.ljz.alarm.domain.AlarmPreference;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Binder;
import android.os.IBinder;
import android.os.Vibrator;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class AlarmService extends Service {

	public Vibrator mVibrator;
	private MediaPlayer mMediaPlayer;
	private Alarm mCurrentAlarm;
	private TelephonyManager mTelephonyManager;

	private IBinder binder = new MyBinder();

	/**
	 * 处理Activity绑定服务的逻辑 service里面唯一的抽象方法
	 */
	@Override
	public IBinder onBind(Intent intent) {
		if (intent != null) {
			mCurrentAlarm = (Alarm) intent.getSerializableExtra("alarm");
			if (mCurrentAlarm != null) {
				AudioManager volMgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
				// 获取系统设置的铃声模式
				switch (volMgr.getRingerMode()) {
				// 静音模式，值为0，这时候根据闹铃设置 ，开启震动、响铃
				case AudioManager.RINGER_MODE_SILENT:
					boolean isBell = (boolean) AlarmPreference.getSettingValue(
							AlarmService.this, AlarmPreference.BELL_MODE_KEY);
					// 若设置了静音时响铃则播放铃声和震动，否则只震动
					if (isBell) {
						play(mCurrentAlarm);
					} else {
						openVibrator();
					}
					break;
				case AudioManager.RINGER_MODE_VIBRATE:
					// 系统是震动模式 ，只震动 不响铃
					openVibrator();
					break;
				case AudioManager.RINGER_MODE_NORMAL:
					// 系统是正常模式
					play(mCurrentAlarm);
					break;
				}
			} else {
				stopSelf();// 停止服务
			}
		}

		return binder;

	}

	// 开启震动
	private void openVibrator() {
		if (mCurrentAlarm != null && mVibrator != null
				&& mCurrentAlarm.vibrate == 1) {
			mVibrator.vibrate(new long[] { 500, 500 }, 0);
		}
	}

	// 开始播放
	private void play(Alarm alarm) {
		// 播放前先停止
		stop();
		mMediaPlayer = new MediaPlayer();
		try {
			mMediaPlayer.setDataSource(alarm.bell);
			mMediaPlayer.prepare();
			mMediaPlayer.setLooping(true);
			mMediaPlayer.start();
			// 震动
			openVibrator();
		} catch (IllegalArgumentException | SecurityException
				| IllegalStateException | IOException e) {
			e.printStackTrace();
		}
		mMediaPlayer.setOnErrorListener(new OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				mp.stop();
				mp.reset();
				mp.release();
				mMediaPlayer = null;
				return true;
			}
		});
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// 初始化震动器
		mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		// 监听来电
		mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		mTelephonyManager.listen(mPhoneStateListener,
				PhoneStateListener.LISTEN_CALL_STATE);
	}

	// 停止播放
	public void stop() {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.reset();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
		// 停止震动
		mVibrator.cancel();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		stop();// 服务销毁时停止播放
		mTelephonyManager.listen(mPhoneStateListener, 0);
		stopForeground(true);
		Intent intent = new Intent("com.ljz.alarm.service.destroy");
		sendBroadcast(intent);
		Log.w("是否发送了启动服务的广播", "是否发送了启动服务的广播");
	}

	private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
		@Override
		public void onCallStateChanged(int state, String ignored) {
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:// 无任何状态
				if (mCurrentAlarm != null && mMediaPlayer != null
						&& !mMediaPlayer.isPlaying()) {
					play(mCurrentAlarm);
				}
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:// 接起电话时
				stop();
				break;
			case TelephonyManager.CALL_STATE_RINGING:// 电话进来时
				stop();
				break;
			}
		}
	};

	public class MyBinder extends Binder {
		public AlarmService getService() {
			return AlarmService.this;
		}
	}

}
