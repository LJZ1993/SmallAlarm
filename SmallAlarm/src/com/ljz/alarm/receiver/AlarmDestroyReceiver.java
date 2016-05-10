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
			Log.w("接收到了启动服务的广播", "接收到了启动服务的广播");
			// 在这里写重新启动service的相关操作
			AlarmClockManager.setNextAlarm(context);
		}

	}

}
