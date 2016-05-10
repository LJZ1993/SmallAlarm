package com.ljz.alarm.common;

import com.ljz.alarm.R;
import com.ljz.alarm.activity.MainActivity;
import com.ljz.alarm.activity.WelcomeActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class AlarmNotificationManager {

	private static NotificationManager notificationManager;
	
	/*
	 * 显示状态栏通知图标
	 */
	public static void showNotification(Context context, Alarm alarm){
		//获取系统级别的通知服务
		notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
		//创建notification实例
		Notification notification = new Notification();
		//设置图标
		notification.icon = R.drawable.alarm_label;
		// 表明在点击了通知栏中的"清除通知"后，此通知自动消失， 经常与FLAG_ONGOING_EVENT一起使用 
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		// 将此通知放到通知栏的"Ongoing"即"正在运行"组中  
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		//???进入Main做什么？？
		Intent intent = new Intent(context,MainActivity.class);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);//?
//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
		String title = context.getResources().getString(R.string.app_name);
		//hour为int类型，转化为String  三目运算符
		String hourStr = (alarm.hour+"").length() == 1 ? "0" + alarm.hour : alarm.hour + "";
		String minutesStr = (alarm.minutes+"").length() == 1 ? "0" + alarm.minutes : alarm.minutes + "";
		String str = hourStr + ":" + minutesStr + "\t" + alarm.label + "\t" + alarm.repeat;
		notification.setLatestEventInfo(context, title, str, pi);
		notificationManager.notify(0, notification);
	}
	
	
	/*
	 * 取消状态栏通知图标
	 */
	public static void cancelNotification(Context context){
//		notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
		if(notificationManager != null){
			notificationManager.cancelAll();
		}
	}
	
}
