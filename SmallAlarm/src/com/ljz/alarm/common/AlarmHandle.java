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

	// 构造器
	public AlarmHandle() {
	}

	// 增加一个闹钟
	public static void addAlarm(Context context, Alarm alarm) {

		ContentValues values = alarm2ContentValues(alarm);// 获取到了闹钟的ContentValues
		// 插入闹钟 数据提供给第三方应用程序使用时使用
		Uri uri = context.getContentResolver().insert(
				Alarm.Columns.CONTENT_URI, values);
		alarm.id = (int) ContentUris.parseId(uri);// 用于从路径中获取ID部分
		Log.w(TAG, "增加了一条闹钟");
		Log.w(TAG, alarm.id + "");
	}

	// 删除一个闹钟
	public static void deleteAlarm(Context context, int alarmId) {
		Uri uri = ContentUris
				.withAppendedId(Alarm.Columns.CONTENT_URI, alarmId);
		context.getContentResolver().delete(uri, null, null);
		Log.v(TAG, "删除了一条闹钟");
	}

	// 删除所有的闹钟 Alarm.Columns.CONTENT_URI代表所有的闹钟路径Alarm.Columns.CONTENT_URI/1
	// 第一个闹钟
	public static void deleteAllAlarm(Context context) {
		context.getContentResolver().delete(Alarm.Columns.CONTENT_URI, null,
				null);
		Log.v(TAG, "删除了所有闹钟");
	}

	// 修改闹钟后更新指定ID的闹钟的相关属性
	public static void updateAlarm(Context context, ContentValues values,
			int alarmId) {
		// withAppendedId(uri, id)用于为路径加上ID部分,获取到uri
		Uri uri = ContentUris
				.withAppendedId(Alarm.Columns.CONTENT_URI, alarmId);
		Log.w("uri", uri.toString());
		// content://com.honliv.su.common.AlarmProvider/alarms/1
		context.getContentResolver().update(uri, values, null, null);

	}

	// 根据ID号获得闹钟的信息
	public static Alarm getAlarm(Context context, int alarmId) {
		// 通过uri+alarmId来得到闹钟位置
		Uri uri = ContentUris
				.withAppendedId(Alarm.Columns.CONTENT_URI, alarmId);
		// 通过query查询得到cursor
		Cursor cursor = context.getContentResolver().query(uri,
				Alarm.Columns.ALARM_QUERY_COLUMNS, null, null, null);
		Alarm alarm = null;
		if (cursor != null) {// 1 判断是否为空 2关闭
			if (cursor.moveToFirst()) {
				alarm = new Alarm(cursor);// 传入alarm的构造函数中进行解析
			}
		}
		cursor.close();
		return alarm;
	}

	// 根据ID号获得全部闹钟的信息
	public static Alarm getNextAlarm(Context context) {
		// 查询uri中alarm表中所有的闹钟
		Cursor cursor = context.getContentResolver().query(
				Alarm.Columns.CONTENT_URI, Alarm.Columns.ALARM_QUERY_COLUMNS,
				Alarm.Columns.ENABLED_WHER, null,
				Alarm.Columns.ENABLED_SORT_ORDER);// 有效闹钟排序
		Alarm alarm = null;
		if (cursor != null) {
			// 查询出来的cursor的初始位置是指向第一条记录的前一个位置的
			// cursor.moveToFirst（）指向查询结果的第一个位置。
			if (cursor.moveToFirst()) {
				// 把cursor放置到Alarm中进行get到字段
				alarm = new Alarm(cursor);
			}
		}
		cursor.close();
		return alarm;
	}

	// 获得所有闹钟
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
		ContentValues values = new ContentValues();// 用于存放数据
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
