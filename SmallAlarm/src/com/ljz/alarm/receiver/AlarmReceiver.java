package com.ljz.alarm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ljz.alarm.activity.AlarmDealActivity;
import com.ljz.alarm.common.Alarm;
import com.ljz.alarm.common.AlarmClockManager;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
			//�ǿ����㲥
//			Intent service = new Intent(context, AlarmService.class);
//			context.startService(service);
			//��������
			AlarmClockManager.setNextAlarm(context);
		}else{
			//ת���������
			Intent deal = new Intent(context, AlarmDealActivity.class);
			deal.putExtra(Alarm.Columns._ID, intent.getIntExtra(Alarm.Columns._ID, 0));
			deal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(deal);
		}
	}
}
