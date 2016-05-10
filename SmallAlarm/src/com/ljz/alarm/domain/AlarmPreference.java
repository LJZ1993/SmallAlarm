package com.ljz.alarm.domain;

import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AlarmPreference {

	// 文件名
	private final static String ALARM_SETTING = "alarm_setting";
	// key
	public final static String BELL_MODE_KEY = "bell_mode";
	public final static String CANCEL_MODE_KEY = "cancel_mode";
	public final static String NUM_TIMES_KEY = "num_times";
	public final static String SHAKE_ITEM_KEY = "shake_item";
	public final static String COUNTRY_NAME_KEY = "	country_name";
	public final static String COUNTRY_CODE_KEY = "	country_code";
	public static final int SETTING_COUNTY = 520;
	/**
	 * 保存设置
	 * 
	 * @param context
	 *            Context
	 * @param settings
	 *            Map键值对
	 */
	public static void saveSetting(Context context, Map<String, Object> settings) {
		if (settings != null && settings.size() > 0) {

			SharedPreferences alarmSettings = context.getSharedPreferences(
					ALARM_SETTING, Activity.MODE_PRIVATE);
			Editor editor = alarmSettings.edit();
			for (String key : settings.keySet()) {
				switch (key) {
				case BELL_MODE_KEY:
					editor.putBoolean(BELL_MODE_KEY,
							(Boolean) settings.get(key));
					break;
				case CANCEL_MODE_KEY:
					editor.putInt(CANCEL_MODE_KEY, (int) settings.get(key));
					break;
				case NUM_TIMES_KEY:
					editor.putInt(NUM_TIMES_KEY, (int) settings.get(key));
					break;
				case SHAKE_ITEM_KEY:
					editor.putInt(SHAKE_ITEM_KEY, (int) settings.get(key));
					break;
				case COUNTRY_NAME_KEY:
					editor.putString(COUNTRY_NAME_KEY, (String) settings.get(key));
					break;
				case COUNTRY_CODE_KEY:
					editor.putString(COUNTRY_CODE_KEY, (String) settings.get(key));
					break;
				}
			}
			// 提交保存
			editor.commit();
		}
	}

	/**
	 * 获得设置的值
	 * 
	 * @param context
	 *            Context
	 * @param key
	 * @return
	 */
	public static Object getSettingValue(Context context, String key) {
		SharedPreferences alarmSettings = context.getSharedPreferences(
				ALARM_SETTING, Activity.MODE_PRIVATE);
		Object o = null;
		switch (key) {
		case BELL_MODE_KEY:
			o = alarmSettings.getBoolean(key, true);
			break;
		case CANCEL_MODE_KEY:
			o = alarmSettings.getInt(key, 0);
			break;
		case NUM_TIMES_KEY:
			o = alarmSettings.getInt(key, 3);
			break;
		case SHAKE_ITEM_KEY:
			o = alarmSettings.getInt(key, 5000);
			break;
		case COUNTRY_CODE_KEY:
			o = alarmSettings.getString(key, null);
			break;
		case COUNTRY_NAME_KEY:
			o = alarmSettings.getString(key, null);
			break;
		}
		return o;
	}
}
