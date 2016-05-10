package com.ljz.alarm.common;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class AlarmHandle {

	private final static String TAG = "AlarmHandle";

	// ������
	public AlarmHandle() {
	}

	// ����һ������
	public static void addAlarm(Context context, Alarm alarm) {

		ContentValues values = alarm2ContentValues(alarm);// ��ȡ�������ӵ�ContentValues
		// �������� �����ṩ��������Ӧ�ó���ʹ��ʱʹ��
		Uri uri = context.getContentResolver().insert(
				Alarm.Columns.CONTENT_URI, values);
		alarm.id = (int) ContentUris.parseId(uri);// ���ڴ�·���л�ȡID����
		Log.w(TAG, "������һ������");
		Log.w(TAG, alarm.id + "");
	}

	// ɾ��һ������
	public static void deleteAlarm(Context context, int alarmId) {
		Uri uri = ContentUris
				.withAppendedId(Alarm.Columns.CONTENT_URI, alarmId);
		context.getContentResolver().delete(uri, null, null);
		Log.v(TAG, "ɾ����һ������");
	}

	// ɾ�����е����� Alarm.Columns.CONTENT_URI�������е�����·��Alarm.Columns.CONTENT_URI/1
	// ��һ������
	public static void deleteAllAlarm(Context context) {
		context.getContentResolver().delete(Alarm.Columns.CONTENT_URI, null,
				null);
		Log.v(TAG, "ɾ������������");
	}

	// �޸����Ӻ����ָ��ID�����ӵ��������
	public static void updateAlarm(Context context, ContentValues values,
			int alarmId) {
		// withAppendedId(uri, id)����Ϊ·������ID����,��ȡ��uri
		Uri uri = ContentUris
				.withAppendedId(Alarm.Columns.CONTENT_URI, alarmId);
		Log.w("uri", uri.toString());
		// content://com.honliv.su.common.AlarmProvider/alarms/1
		context.getContentResolver().update(uri, values, null, null);

	}

	// ����ID�Ż�����ӵ���Ϣ
	public static Alarm getAlarm(Context context, int alarmId) {
		// ͨ��uri+alarmId���õ�����λ��
		Uri uri = ContentUris
				.withAppendedId(Alarm.Columns.CONTENT_URI, alarmId);
		// ͨ��query��ѯ�õ�cursor
		Cursor cursor = context.getContentResolver().query(uri,
				Alarm.Columns.ALARM_QUERY_COLUMNS, null, null, null);
		Alarm alarm = null;
		if (cursor != null) {// 1 �ж��Ƿ�Ϊ�� 2�ر�
			if (cursor.moveToFirst()) {
				alarm = new Alarm(cursor);// ����alarm�Ĺ��캯���н��н���
			}
		}
		cursor.close();
		return alarm;
	}

	// ����ID�Ż��ȫ�����ӵ���Ϣ
	public static Alarm getNextAlarm(Context context) {
		// ��ѯuri��alarm�������е�����
		Cursor cursor = context.getContentResolver().query(
				Alarm.Columns.CONTENT_URI, Alarm.Columns.ALARM_QUERY_COLUMNS,
				Alarm.Columns.ENABLED_WHER, null,
				Alarm.Columns.ENABLED_SORT_ORDER);// ��Ч��������
		Alarm alarm = null;
		if (cursor != null) {
			// ��ѯ������cursor�ĳ�ʼλ����ָ���һ����¼��ǰһ��λ�õ�
			// cursor.moveToFirst����ָ���ѯ����ĵ�һ��λ�á�
			if (cursor.moveToFirst()) {
				// ��cursor���õ�Alarm�н���get���ֶ�
				alarm = new Alarm(cursor);
			}
		}
		cursor.close();
		return alarm;
	}

	// �����������
	public static List<Alarm> getAlarms(Context context) {
		List<Alarm> alarms = new ArrayList<Alarm>();
		Cursor cursor = context.getContentResolver().query(
				Alarm.Columns.CONTENT_URI, Alarm.Columns.ALARM_QUERY_COLUMNS,
				null, null, Alarm.Columns.DEFAULT_SORT_ORDER);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				alarms.add(new Alarm(cursor));
			}
		}
		cursor.close();
		return alarms;
	}

	private static ContentValues alarm2ContentValues(Alarm alarm) {
		ContentValues values = new ContentValues();// ���ڴ������
		values.put(Alarm.Columns.HOUR, alarm.hour);
		values.put(Alarm.Columns.MINUTES, alarm.minutes);
		values.put(Alarm.Columns.REPEAT, alarm.repeat);
		values.put(Alarm.Columns.BELL, alarm.bell);
		values.put(Alarm.Columns.VIBRATE, alarm.vibrate);
		values.put(Alarm.Columns.LABEL, alarm.label);
		values.put(Alarm.Columns.ENABLED, alarm.enabled);
		return values;
	}
}
