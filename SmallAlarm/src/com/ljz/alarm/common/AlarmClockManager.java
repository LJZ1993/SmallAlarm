package com.ljz.alarm.common;

import java.util.Calendar;

import com.ljz.alarm.R;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * @author LJZ 闹钟管理器
 * 
 */
public class AlarmClockManager {

	private final static String TAG = "AlarmClockManager";
	// 日历
	private static Calendar calendar = Calendar.getInstance();
	// 闹铃管理
	private static AlarmManager am;

	/**
	 * 设置提示信息
	 * 
	 * @param context
	 *            上下文
	 * @param hour
	 *            小时
	 * @param minute
	 *            分钟
	 */
	public static void setAlarm(Context context, Alarm alarm) {
		// 获取重复次数 数组
		String[] repeats = context.getResources().getStringArray(
				R.array.repeat_item);
		//下一次响铃时间
		long timeMillis = time2Millis(alarm.hour, alarm.minutes, alarm.repeat,
				repeats);
		// 将下次响铃时间存到数据库
		ContentValues values = new ContentValues();
		values.put(Alarm.Columns.NEXTMILLIS, timeMillis);
		AlarmHandle.updateAlarm(context, values, alarm.id);
		Toast.makeText(context, fomatTip(timeMillis), Toast.LENGTH_SHORT)
				.show();// Toast出还剩多少时间响铃
		// 设置下一个次闹钟
		setNextAlarm(context);
	}

	/**
	 * 设置闹钟下一次响铃时间
	 * 
	 * @param context
	 *            上下文
	 */
	public static void setNextAlarm(Context context) {
		// 获取下一个响铃的闹钟
		Alarm alarm = AlarmHandle.getNextAlarm(context);
		// Log.w("alarm-----------", alarm.toString());
		if (alarm != null) {// 存在闹钟时
			// 发送闹钟广播
			// 伴随着闹钟（到时）响铃（包括周期性闹铃），将触发广播事件的发送
			Intent intent = new Intent("android.intent.action.ALARM_RECEIVER");
			intent.putExtra(Alarm.Columns._ID, alarm.id);// 传递闹钟的id
			PendingIntent pi = PendingIntent.getBroadcast(context, alarm.id,
					intent, PendingIntent.FLAG_UPDATE_CURRENT);// ?
			// 通过system获取闹钟服务
			am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			// 将type参数值设为AlarmManager.RTC_WAKEUP，除了有定时器的功能外，还会发出警报声（例如，响铃、震动）
			// 1.闹钟的类型：在手机睡眠时间会唤醒手机闹铃 2.隔多久响铃 闹钟的执行动作
			am.set(AlarmManager.RTC_WAKEUP, alarm.nextMillis, pi);
			// 显示通知
			//AlarmNotificationManager.showNotification(context, alarm);
		} else {
			AlarmNotificationManager.cancelNotification(context);
		}
	}

	public static void cancelAlarm(Context context, int id) {
		Log.v(TAG, "cancelAlarm");
		Intent intent = new Intent("android.intent.action.ALARM_RECEIVER");
		PendingIntent pi = PendingIntent.getBroadcast(context, id, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.cancel(pi);
		setNextAlarm(context);
	}

	// 还剩多少时间响铃
	private static String fomatTip(long timeMillis) {
		//时间间隔的绝对时间：如250011毫秒
		long delta = timeMillis - System.currentTimeMillis();
		long hours = delta / (1000 * 60 * 60);// delta为毫秒
		long minutes = delta / (1000 * 60) % 60;
		long days = hours / 24;// 周一到周五有天数间隔
		hours = hours % 24;// 当hours大于24时，%为剩下的 如果小于为0
		//Log.w("hours", hours + "");
		//Log.w("delta", delta + "");

		String daySeq = (days == 0) ? "" : days + "天";

		String hourSeq = (hours == 0) ? "" : hours + "小时";

		String minSeq = (minutes == 0) ? "1分钟" : minutes + "分钟";

		return "已将闹钟设置为从现在起" + daySeq + hourSeq + minSeq + "后提醒";
	}

	public static Long time2Millis(int hour, int minute, String repeat,
			String[] repeats) {
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, hour);// 小时
		calendar.set(Calendar.MINUTE, minute);// 分钟
		calendar.set(Calendar.SECOND, 0);// 秒
		// 属性名称：Calendar.MILLISECOND
		// 代表含义：毫秒数
		calendar.set(Calendar.MILLISECOND, 0);
		// 闹钟重复模式为 只响一次或每天
		if (repeat.equals(repeats[Alarm.ALARM_ONCE])
				|| repeat.equals(repeats[Alarm.ALARM_EVERYDAY])) {
			// 若时间已经过去，则推迟一天
			if (calendar.getTimeInMillis() - System.currentTimeMillis() < 0) {
				calendar.roll(Calendar.DATE, 1);
			}
		} else if (repeat.equals(repeats[Alarm.ALARM_MON_FRI])) {
			// 闹钟重复模式为 周一到周五
			if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
				// 周五若时间已经过去，则推迟3天
				if (calendar.getTimeInMillis() - System.currentTimeMillis() < 0) {
					calendar.roll(Calendar.DATE, 3);
				}
			} else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
				// 周六
				calendar.roll(Calendar.DATE, 2);
			} else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				// 周日
				calendar.roll(Calendar.DATE, 1);
			} else {
				// 若时间已经过去，则推迟一天
				if (calendar.getTimeInMillis() - System.currentTimeMillis() < 0) {
					calendar.roll(Calendar.DATE, 1);
				}
			}
		}
		return calendar.getTimeInMillis();
	}
}
