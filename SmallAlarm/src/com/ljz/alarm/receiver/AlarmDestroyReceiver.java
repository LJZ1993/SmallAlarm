package com.ljz.alarm.receiver;

import com.ljz.alarm.common.AlarmClockManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AlarmDestroyReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("com.ljz.alarm.service.destroy")) {
			// TODO
			Log.w("���յ�����������Ĺ㲥", "���յ�����������Ĺ㲥");
			// ������д��������service����ز���
			AlarmClockManager.setNextAlarm(context);
		}

	}

}
