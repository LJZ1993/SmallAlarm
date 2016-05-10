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
 * @author LJZ ���ӹ�����
 * 
 */
public class AlarmClockManager {

	private final static String TAG = "AlarmClockManager";
	// ����
	private static Calendar calendar = Calendar.getInstance();
	// �������
	private static AlarmManager am;

	/**
	 * ������ʾ��Ϣ
	 * 
	 * @param context
	 *            ������
	 * @param hour
	 *            Сʱ
	 * @param minute
	 *            ����
	 */
	public static void setAlarm(Context context, Alarm alarm) {
		// ��ȡ�ظ����� ����
		String[] repeats = context.getResources().getStringArray(
				R.array.repeat_item);
		//��һ������ʱ��
		long timeMillis = time2Millis(alarm.hour, alarm.minutes, alarm.repeat,
				repeats);
		// ���´�����ʱ��浽���ݿ�
		ContentValues values = new ContentValues();
		values.put(Alarm.Columns.NEXTMILLIS, timeMillis);
		AlarmHandle.updateAlarm(context, values, alarm.id);
		Toast.makeText(context, fomatTip(timeMillis), Toast.LENGTH_SHORT)
				.show();// Toast����ʣ����ʱ������
		// ������һ��������
		setNextAlarm(context);
	}

	/**
	 * ����������һ������ʱ��
	 * 
	 * @param context
	 *            ������
	 */
	public static void setNextAlarm(Context context) {
		// ��ȡ��һ�����������
		Alarm alarm = AlarmHandle.getNextAlarm(context);
		// Log.w("alarm-----------", alarm.toString());
		if (alarm != null) {// ��������ʱ
			// �������ӹ㲥
			// ���������ӣ���ʱ�����壨�������������壩���������㲥�¼��ķ���
			Intent intent = new Intent("android.intent.action.ALARM_RECEIVER");
			intent.putExtra(Alarm.Columns._ID, alarm.id);// �������ӵ�id
			PendingIntent pi = PendingIntent.getBroadcast(context, alarm.id,
					intent, PendingIntent.FLAG_UPDATE_CURRENT);// ?
			// ͨ��system��ȡ���ӷ���
			am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			// ��type����ֵ��ΪAlarmManager.RTC_WAKEUP�������ж�ʱ���Ĺ����⣬���ᷢ�������������磬���塢�𶯣�
			// 1.���ӵ����ͣ����ֻ�˯��ʱ��ỽ���ֻ����� 2.��������� ���ӵ�ִ�ж���
			am.set(AlarmManager.RTC_WAKEUP, alarm.nextMillis, pi);
			// ��ʾ֪ͨ
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

	// ��ʣ����ʱ������
	private static String fomatTip(long timeMillis) {
		//ʱ�����ľ���ʱ�䣺��250011����
		long delta = timeMillis - System.currentTimeMillis();
		long hours = delta / (1000 * 60 * 60);// deltaΪ����
		long minutes = delta / (1000 * 60) % 60;
		long days = hours / 24;// ��һ���������������
		hours = hours % 24;// ��hours����24ʱ��%Ϊʣ�µ� ���С��Ϊ0
		//Log.w("hours", hours + "");
		//Log.w("delta", delta + "");

		String daySeq = (days == 0) ? "" : days + "��";

		String hourSeq = (hours == 0) ? "" : hours + "Сʱ";

		String minSeq = (minutes == 0) ? "1����" : minutes + "����";

		return "�ѽ���������Ϊ��������" + daySeq + hourSeq + minSeq + "������";
	}

	public static Long time2Millis(int hour, int minute, String repeat,
			String[] repeats) {
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, hour);// Сʱ
		calendar.set(Calendar.MINUTE, minute);// ����
		calendar.set(Calendar.SECOND, 0);// ��
		// �������ƣ�Calendar.MILLISECOND
		// �����壺������
		calendar.set(Calendar.MILLISECOND, 0);
		// �����ظ�ģʽΪ ֻ��һ�λ�ÿ��
		if (repeat.equals(repeats[Alarm.ALARM_ONCE])
				|| repeat.equals(repeats[Alarm.ALARM_EVERYDAY])) {
			// ��ʱ���Ѿ���ȥ�����Ƴ�һ��
			if (calendar.getTimeInMillis() - System.currentTimeMillis() < 0) {
				calendar.roll(Calendar.DATE, 1);
			}
		} else if (repeat.equals(repeats[Alarm.ALARM_MON_FRI])) {
			// �����ظ�ģʽΪ ��һ������
			if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
				// ������ʱ���Ѿ���ȥ�����Ƴ�3��
				if (calendar.getTimeInMillis() - System.currentTimeMillis() < 0) {
					calendar.roll(Calendar.DATE, 3);
				}
			} else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
				// ����
				calendar.roll(Calendar.DATE, 2);
			} else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				// ����
				calendar.roll(Calendar.DATE, 1);
			} else {
				// ��ʱ���Ѿ���ȥ�����Ƴ�һ��
				if (calendar.getTimeInMillis() - System.currentTimeMillis() < 0) {
					calendar.roll(Calendar.DATE, 1);
				}
			}
		}
		return calendar.getTimeInMillis();
	}
}
