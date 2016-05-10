package com.ljz.alarm.common;

import java.io.Serializable;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

@SuppressWarnings("serial")
public class Alarm implements Serializable {// http://xiebh.iteye.com/blog/121311
// ��һ������ʵ��Serializable�ӿں��������඼���Զ���ʵ�����л�
	// Ϊ�˱������ڴ��еĸ��ֶ����״̬��Ҳ����ʵ�����������Ƿ����������ҿ��԰ѱ���Ķ���״̬�ٶ�����
	// ȡ���ַ�ʽ
	// ����
	public final static int CANCEL_NUM_MODE = 0;
	// ҡ���ֻ�
	public final static int CANCEL_SHAKE_MODE = 1;
	//����ģʽ
	public final static int CANCEL_NORMAL_MODE = 2;
	// �����ظ���ʽ
	// ��һ��
	public final static int ALARM_ONCE = 0;
	// ��һ������
	public final static int ALARM_MON_FRI = 1;
	// ÿ��
	public final static int ALARM_EVERYDAY = 2;

	// ���ʵ����ݿ���
	public static final String DATABASE_NAME = "clock.db";
	public static final String AUTHORITIES = "com.ljz.alarm.common.AlarmProvider";
	public static final String TABLE_NAME = "alarms";
	// �½���༭��־
	public static final int UPDATE_ALARM = 100;
	public static final int DELETE_ALARM = 200;

	public static class Columns implements BaseColumns {
		// AlarmProvider�ķ���Uri
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITIES + "/" + TABLE_NAME);
		// AlarmProvider���ص��������� MIME ��·����β---dir
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/alarms";
		// ��id ��β-----item
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/alarms";

		// ���е�����
		public static final String HOUR = "hour";
		public static final String MINUTES = "minutes";
		public static final String REPEAT = "repeat";
		public static final String BELL = "bell";
		public static final String VIBRATE = "vibrate";
		public static final String LABEL = "label";
		public static final String ENABLED = "enabled";
		// nextMillis��long���͵�
		public static final String NEXTMILLIS = "nextMillis";
		// ��ʵĬ������������+" ASC"д��дЧ����һ��
		// Ĭ������ asc ��ascend �������˼
		public static final String DEFAULT_SORT_ORDER = HOUR + ", " + MINUTES
				+ " ASC";
		// ��Ч��������
		public static final String ENABLED_SORT_ORDER = NEXTMILLIS + " ASC";

		// ��Ч
		public static final String ENABLED_WHER = "enabled = 1";
		// ���е��ֶ�
		public static final String[] ALARM_QUERY_COLUMNS = { _ID, HOUR,
				MINUTES, REPEAT, BELL, VIBRATE, LABEL, ENABLED, NEXTMILLIS };

		public static final int ID_INDEX = 0;
		public static final int HOUR_INDEX = 1;
		public static final int MINUTES_INDEX = 2;
		public static final int REPEAT_INDEX = 3;
		public static final int BELL_INDEX = 4;
		public static final int VIBRATE_INDEX = 5;
		public static final int LABEL_INDEX = 6;
		public static final int ENABLED_INDEX = 7;
		public static final int NEXTMILLIS_INDEX = 8;
	}

	// ���е�����
	public int id;
	public int hour;
	public int minutes;
	public String repeat;
	public String bell;
	public int vibrate;
	public String label;
	public int enabled;
	public long nextMillis;

	// Ĭ�Ϲ�����
	public Alarm() {
	}

	// �вι�����
	public Alarm(Cursor cursor) {
		id = cursor.getInt(Alarm.Columns.ID_INDEX);
		hour = cursor.getInt(Alarm.Columns.HOUR_INDEX);
		minutes = cursor.getInt(Alarm.Columns.MINUTES_INDEX);
		repeat = cursor.getString(Alarm.Columns.REPEAT_INDEX);
		bell = cursor.getString(Alarm.Columns.BELL_INDEX);
		vibrate = cursor.getInt(Alarm.Columns.VIBRATE_INDEX);
		label = cursor.getString(Alarm.Columns.LABEL_INDEX);
		enabled = cursor.getInt(Alarm.Columns.ENABLED_INDEX);
		nextMillis = cursor.getLong(Alarm.Columns.NEXTMILLIS_INDEX);
	}
}
