package com.ljz.alarm.activity;

import java.util.Calendar;

import com.ljz.alarm.R;
import com.ljz.alarm.common.Alarm;
import com.ljz.alarm.common.AlarmClockManager;
import com.ljz.alarm.common.AlarmHandle;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

public class NewClockActivity extends Activity implements View.OnClickListener {

	// �򿪶Ի���ı�־
	private final static int SHOW_REPEAT = 1;
	private final static int SHOW_LABEL = 2;
	private final static int DEL_ALARM = 3;

	private TimePicker timePicker;
	private TextView tv_repeat;
	private TextView tv_bell;
	private TextView tv_label;
	private CheckBox cb_vibration;
	private Button bt_del;

	private Alarm alarm;

	private boolean isNew = false;

	private Context context;

	private String bellName;

	// �Ƿ����
	private int vibration = 1;
	// ��¼�ظ���ʽ 0ֻ��һ�Σ�1��һ�����壬2ÿ��
	private int repeat = 0;
	private int repeatOld = 0;
	private int hour;// �趨��Сʱ
	private int minute;// �趨�ķ���
	private String bellPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// �����ޱ�����
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.new_clock);
		init();
	}

	/**
	 * ��ʼ��
	 */
	private void init() {
		context = this;
		// timepicker
		timePicker = (TimePicker) findViewById(R.id.clock);
		// ����24Сʱ��
		timePicker.setIs24HourView(true);
		// ���ý�ֹ�������� ����������View�޷���ȡ����
		// This view will block any of its descendants from getting focus, even
		// if they are focusable.

		timePicker
				.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
		tv_repeat = (TextView) findViewById(R.id.tv_repeat);
		tv_bell = (TextView) findViewById(R.id.tv_bell);
		tv_label = (TextView) findViewById(R.id.tv_label);
		cb_vibration = (CheckBox) findViewById(R.id.cb_offon);
		bt_del = (Button) findViewById(R.id.bt_del);
		findViewById(R.id.bt_cancel).setOnClickListener(this);
		findViewById(R.id.bt_ok).setOnClickListener(this);
		findViewById(R.id.ll_repeat).setOnClickListener(this);
		findViewById(R.id.ll_bell).setOnClickListener(this);
		findViewById(R.id.ll_label).setOnClickListener(this);
		// �ж����½����Ǳ༭ ��Serializable��ʲô��˼����ȡ�Ĳ��������Ǹ�����
		// ��������intent����������档һ�����½� һ�����޸ģ��½���forresult��û�д�ֵ
		alarm = (Alarm) getIntent().getSerializableExtra("alarm");
		if (alarm == null) {
			// ��alarmΪ��ʱ��ʾ�½���ͬʱisNewΪtrue
			isNew = true;
			// ���ñ�������Ϣ
			((TextView) findViewById(R.id.tv_title)).setText("�½�����");
			// new��calendarʵ�����ڻ�ȡtimepicker��ʱ��
			// Calendar calendar = Calendar.getInstance();
			// hour = calendar.get(Calendar.HOUR_OF_DAY);
			// minute = calendar.get(Calendar.MINUTE);
			// timePicker.setCurrentHour(hour);
			// timePicker.setCurrentMinute(minute);
			// ������timepicker�л�ȡ
			hour = timePicker.getCurrentHour();
			minute = timePicker.getCurrentMinute();
			Log.w("hour", hour + "");
			// ����Ĭ��repeat����
			tv_repeat.setText("ֻ��һ��");
			// ���Ĭ�ϵ�����·��
			bellName = getDefaultbell();
			Log.w("bellName", bellName);
			// /system/media/audio/alarms/Alarm_Beep_01.ogg
			// String temp[] = bellPath.split("/");
			// Log.w("temp[]", temp + "");
			// Alarm_Beep_01.ogg ��.���͡�|������ת���ַ�������ü�"\\";
			// tv_bell.setText(temp[temp.length - 1].split("\\.")[0]);
			tv_bell.setText(bellName);
			cb_vibration.setChecked(true);// Ĭ����ѡ��״̬
			tv_label.setText("����");
			// ����ɾ����ť
			bt_del.setVisibility(View.GONE);
		} else {
			// �޸ı༭
			isNew = false;
			((TextView) findViewById(R.id.tv_title)).setText("�޸�����");
			// ��alarm��ԭ���趨��ʱ��ԭ���Ļ�ԭ��ȥ
			timePicker.setCurrentHour(alarm.hour);
			timePicker.setCurrentMinute(alarm.minutes);
			// ��ȡ��alarm�޸�ǰ��ʱ��
			hour = alarm.hour;
			minute = alarm.minutes;
			tv_repeat.setText(alarm.repeat);
			// ��Ŀ����� ��ȡԭ���趨���������ڲ�ת����int���ͱ�ʾ
			repeatOld = alarm.repeat.equals("ֻ��һ��") ? 0 : alarm.repeat
					.equals("��һ������") ? 1 : 2;
			// �����µĶ���
			repeat = repeatOld;
			//
			bellPath = alarm.bell;
			Log.w("bellPath", bellPath);
			// /storage/emulated/0/kgmusic/download/���� - ͬ������ - 95����ǰ�.mp3
			// split ����bell����
			// ��java�� \����ת���ַ� \n \t �ȣ��� \\ ����һ����б�� ��.����һ��Ԫ�ַ�
			String temp[] = bellPath.split("/");
			tv_bell.setText(temp[temp.length - 1].split("\\.")[0]);
			// ��ȡ��bell name
			bellName = tv_bell.getText().toString();
			// ����ԭ�б��浽����������ԭ
			cb_vibration.setChecked(alarm.vibrate == 1 ? true : false);
			tv_label.setText(alarm.label);
			// ��ʾɾ����ť
			bt_del.setVisibility(View.VISIBLE);
			bt_del.setOnClickListener(this);
		}

		cb_vibration.setOnClickListener(this);
		// ��timepicker���ü�����
		timePicker.setOnTimeChangedListener(new OnTimeChangedListener() {
			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int mit) {
				// �Ѹı��˵�ʱ�����õ�����hour ��minute��
				hour = hourOfDay;
				minute = mit;
			}
		});

	}

	// ��ȡĬ�ϵ���������ֻ��Ҫһ�������Բ��ñ���
	private String getDefaultbell() {
		String ret = "";
		// ͨ��getContentResover��ѯ������·��
		Cursor cursor = getContentResolver().query(
				MediaStore.Audio.Media.INTERNAL_CONTENT_URI, null, null, null,
				null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {// һ��ͨ���ж�cursor.moveToFirst()��ֵΪtrue��false��ȷ����ѯ����Ƿ�Ϊ��
				ret = cursor.getString(cursor
						.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
			}
			cursor.close();// �ǵùر�cursor
		}
		Log.w("ret", ret);
		return ret;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_ok:
			Intent intent = new Intent();// ����ͼ���õ���forResult
			if (isNew) {// ��������
				alarm = new Alarm();
				// ��timepicker�е�hour��minute����
				alarm.hour = hour;
				alarm.minutes = minute;
				alarm.repeat = repeat == 0 ? "ֻ��һ��" : repeat == 1 ? "��һ������"
						: "ÿ��";// ��Ŀ���ʽ����ȡѡ����������
				alarm.bell = bellName;// ��ȡ�����ľ���·��
				alarm.vibrate = vibration;// ��ȡ����Ϣ
				alarm.label = TextUtils.isEmpty(tv_label.getText()) ? ""
						: tv_label.getText().toString();// ��ȡ��ע��Ϣ
				alarm.enabled = 1;// ?
				alarm.nextMillis = 0;// ?
				// ���룬Ϊʲô�������new��ʵ���� content�ĺô���ʾ������
				AlarmHandle.addAlarm(context, alarm);
				intent.putExtra("alarm", alarm);
			} else {
				// �޸����ӾͲ���Ҫ�ٴ������Ӷ����ˣ���isNew=true���Ѿ��������ˣ�ֻ��Ҫ���޸����ݱ��浽����
				// ContentValues ��HashTable���ƶ���һ�ִ洢�Ļ��� ��������������������ڣ�contenvalues
				// Keyֻ����String���ͣ�valuesֻ�ܴ洢�������͵����ݣ���string��int֮��ģ����ܴ洢�������ֶ�����ContentValues
				// ���������ݿ��еĲ�����
				ContentValues values = new ContentValues();
				if (alarm.hour != hour) {
					// ��hour��ԭ����hour��ͬʱ
					values.put(Alarm.Columns.HOUR, hour);
					// ���µ�hour����alarm,hour ��ͬ
					alarm.hour = hour;
				}
				if (alarm.minutes != minute) {
					values.put(Alarm.Columns.MINUTES, minute);
					alarm.minutes = minute;
				}
				if (repeatOld != repeat) {
					// �����������Ŀ�����
					values.put(Alarm.Columns.REPEAT, repeat == 0 ? "ֻ��һ��"
							: repeat == 1 ? "��һ������" : "ÿ��");
					alarm.repeat = repeat == 0 ? "ֻ��һ��" : repeat == 1 ? "��һ������"
							: "ÿ��";
				}
				// equals�Ƚ���������������Ƿ���ͬ
				// == �Ƚ����������Ƿ���ͬһ����
				if (!TextUtils.isEmpty(bellName)
						&& !alarm.bell.equals(bellName)) {
					values.put(Alarm.Columns.BELL, bellName);
				}
				if (vibration != alarm.vibrate) {
					values.put(Alarm.Columns.VIBRATE, vibration);
				}
				if (!TextUtils.isEmpty(tv_label.getText())
						&& !alarm.label.equals(tv_label.getText())) {
					values.put(Alarm.Columns.LABEL, tv_label.getText()
							.toString());
				}
				//????
				if (alarm.enabled != 1) {
					values.put(Alarm.Columns.ENABLED, 1);
					alarm.enabled = 1;
				}
				if (values.size() > 0) {
					AlarmHandle.updateAlarm(context, values, alarm.id);
					intent.putExtra("alarm", alarm);
				}
			}
			// ���ظ���
			setResult(Alarm.UPDATE_ALARM, intent);
			finish();
			break;
		case R.id.bt_cancel:
			finish();
			break;
		case R.id.ll_repeat:
			showDialog(SHOW_REPEAT);// �����-�Ƚ϶��dialog�������ַ�ʽ�Ϻã�����
			break;
		case R.id.ll_bell:// ѡ������
			// TODO Auto-generated method stub
			Intent selectBell = new Intent(NewClockActivity.this,
					SelectBellActivity.class);
			selectBell.putExtra("bellPath", bellName);// ����������ȥ��ʲô��
			selectBell.putExtra("bellName", tv_bell.getText());
			startActivityForResult(selectBell, 1);
			break;
		case R.id.cb_offon:
			if (cb_vibration.isChecked()) {
				// Ĭ��Ϊ1 vibration = 1������ѡ��
				vibration = 1;
			} else {
				vibration = 0;
			}
			break;
		case R.id.ll_label:
			showDialog(SHOW_LABEL);
			break;
		case R.id.bt_del:
			// ����ɾ�����ӶԻ���
			showDialog(DEL_ALARM);
			break;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		LayoutInflater inflater = LayoutInflater.from(context);
		switch (id) {
		case SHOW_REPEAT:
			dialog = new AlertDialog.Builder(context)
					.setTitle(getResources().getText(R.string.repeat_text))
					.setSingleChoiceItems(R.array.repeat_item, repeatOld,
							new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									tv_repeat
											.setText(getResources()
													.getStringArray(
															R.array.repeat_item)[which]);
									repeat = which;// ѡ�е�item��ID
									dialog.dismiss();
								}
							}).setNegativeButton("ȡ��", null).create();
			break;
		case SHOW_LABEL:
			final View view = inflater.inflate(R.layout.label_dialog, null);
			final EditText et = (EditText) view.findViewById(R.id.et_label);
			et.setText(tv_label.getText());
			dialog = new AlertDialog.Builder(context)
					.setTitle(getResources().getText(R.string.label_text))
					.setView(view).setNegativeButton("ȡ��", null)
					.setPositiveButton("ȷ��", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							tv_label.setText(et.getText().toString());
						}
					}).create();
			break;
		case DEL_ALARM:
			dialog = new AlertDialog.Builder(context)
					.setTitle(getResources().getText(R.string.del_clock))
					.setMessage("�Ƿ�ɾ�������ӣ�").setNegativeButton("ȡ��", null)
					.setPositiveButton("ȷ��", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (alarm.enabled == 1) {
								AlarmClockManager
										.cancelAlarm(context, alarm.id);
							}
							AlarmHandle.deleteAlarm(context, alarm.id);
							setResult(Alarm.DELETE_ALARM);
							finish();
						}
					}).create();
		}
		dialog.setCanceledOnTouchOutside(false);
		return dialog;
	}

	// ���ѡ�����������ƺ�·��
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 100) {
			tv_bell.setText(data.getStringExtra("name"));
			bellName = data.getStringExtra("path");
		}
	}

}
