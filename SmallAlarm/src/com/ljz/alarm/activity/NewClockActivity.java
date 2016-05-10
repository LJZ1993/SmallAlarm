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

	// 打开对话框的标志
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

	// 是否打开震动
	private int vibration = 1;
	// 记录重复方式 0只响一次，1周一到周五，2每天
	private int repeat = 0;
	private int repeatOld = 0;
	private int hour;// 设定的小时
	private int minute;// 设定的分钟
	private String bellPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置无标题栏
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.new_clock);
		init();
	}

	/**
	 * 初始化
	 */
	private void init() {
		context = this;
		// timepicker
		timePicker = (TimePicker) findViewById(R.id.clock);
		// 设置24小时制
		timePicker.setIs24HourView(true);
		// 设置禁止键盘输入 会让所有子View无法获取焦点
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
		// 判断是新建还是编辑 ？Serializable是什么意思？获取的不是数据是个对象
		// 共有两个intent进入这个界面。一个是新建 一个是修改，新建的forresult，没有传值
		alarm = (Alarm) getIntent().getSerializableExtra("alarm");
		if (alarm == null) {
			// 当alarm为空时表示新建，同时isNew为true
			isNew = true;
			// 设置标题栏信息
			((TextView) findViewById(R.id.tv_title)).setText("新建闹钟");
			// new出calendar实例用于获取timepicker的时间
			// Calendar calendar = Calendar.getInstance();
			// hour = calendar.get(Calendar.HOUR_OF_DAY);
			// minute = calendar.get(Calendar.MINUTE);
			// timePicker.setCurrentHour(hour);
			// timePicker.setCurrentMinute(minute);
			// 可以行timepicker中获取
			hour = timePicker.getCurrentHour();
			minute = timePicker.getCurrentMinute();
			Log.w("hour", hour + "");
			// 设置默认repeat文字
			tv_repeat.setText("只响一次");
			// 获得默认的铃声路径
			bellName = getDefaultbell();
			Log.w("bellName", bellName);
			// /system/media/audio/alarms/Alarm_Beep_01.ogg
			// String temp[] = bellPath.split("/");
			// Log.w("temp[]", temp + "");
			// Alarm_Beep_01.ogg “.”和“|”都是转义字符，必须得加"\\";
			// tv_bell.setText(temp[temp.length - 1].split("\\.")[0]);
			tv_bell.setText(bellName);
			cb_vibration.setChecked(true);// 默认是选中状态
			tv_label.setText("闹钟");
			// 隐藏删除按钮
			bt_del.setVisibility(View.GONE);
		} else {
			// 修改编辑
			isNew = false;
			((TextView) findViewById(R.id.tv_title)).setText("修改闹钟");
			// 把alarm的原来设定的时间原本的还原回去
			timePicker.setCurrentHour(alarm.hour);
			timePicker.setCurrentMinute(alarm.minutes);
			// 获取到alarm修改前的时间
			hour = alarm.hour;
			minute = alarm.minutes;
			tv_repeat.setText(alarm.repeat);
			// 四目运算符 获取原来设定的响铃周期并转化成int类型表示
			repeatOld = alarm.repeat.equals("只响一次") ? 0 : alarm.repeat
					.equals("周一到周五") ? 1 : 2;
			// 赋给新的对象
			repeat = repeatOld;
			//
			bellPath = alarm.bell;
			Log.w("bellPath", bellPath);
			// /storage/emulated/0/kgmusic/download/老狼 - 同桌的你 - 95年红星版.mp3
			// split 出个bell名字
			// 在java中 \代表转义字符 \n \t 等，而 \\ 代表一个反斜杠 而.代表一个元字符
			String temp[] = bellPath.split("/");
			tv_bell.setText(temp[temp.length - 1].split("\\.")[0]);
			// 获取到bell name
			bellName = tv_bell.getText().toString();
			// 根据原有保存到的数据来还原
			cb_vibration.setChecked(alarm.vibrate == 1 ? true : false);
			tv_label.setText(alarm.label);
			// 显示删除按钮
			bt_del.setVisibility(View.VISIBLE);
			bt_del.setOnClickListener(this);
		}

		cb_vibration.setOnClickListener(this);
		// 给timepicker设置监听器
		timePicker.setOnTimeChangedListener(new OnTimeChangedListener() {
			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int mit) {
				// 把改变了的时间设置到对象hour 和minute中
				hour = hourOfDay;
				minute = mit;
			}
		});

	}

	// 获取默认的铃声名，只需要一个，所以不用遍历
	private String getDefaultbell() {
		String ret = "";
		// 通过getContentResover查询到铃声路径
		Cursor cursor = getContentResolver().query(
				MediaStore.Audio.Media.INTERNAL_CONTENT_URI, null, null, null,
				null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {// 一般通过判断cursor.moveToFirst()的值为true或false来确定查询结果是否为空
				ret = cursor.getString(cursor
						.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
			}
			cursor.close();// 记得关闭cursor
		}
		Log.w("ret", ret);
		return ret;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_ok:
			Intent intent = new Intent();// 无意图，用的是forResult
			if (isNew) {// 创建闹钟
				alarm = new Alarm();
				// 把timepicker中的hour，minute传入
				alarm.hour = hour;
				alarm.minutes = minute;
				alarm.repeat = repeat == 0 ? "只响一次" : repeat == 1 ? "周一到周五"
						: "每天";// 三目表达式来获取选择的响铃次数
				alarm.bell = bellName;// 获取铃声的绝对路径
				alarm.vibrate = vibration;// 获取震动信息
				alarm.label = TextUtils.isEmpty(tv_label.getText()) ? ""
						: tv_label.getText().toString();// 获取备注信息
				alarm.enabled = 1;// ?
				alarm.nextMillis = 0;// ?
				// 插入，为什么这个不用new出实例来 content的好处显示出来了
				AlarmHandle.addAlarm(context, alarm);
				intent.putExtra("alarm", alarm);
			} else {
				// 修改闹钟就不需要再创建闹钟对象了，在isNew=true是已经创建好了，只需要把修改数据保存到表中
				// ContentValues 和HashTable类似都是一种存储的机制 但是两者最大的区别就在于，contenvalues
				// Key只能是String类型，values只能存储基本类型的数据，像string，int之类的，不能存储对象这种东西。ContentValues
				// 常用在数据库中的操作。
				ContentValues values = new ContentValues();
				if (alarm.hour != hour) {
					// 当hour和原来的hour不同时
					values.put(Alarm.Columns.HOUR, hour);
					// 把新的hour赋给alarm,hour 下同
					alarm.hour = hour;
				}
				if (alarm.minutes != minute) {
					values.put(Alarm.Columns.MINUTES, minute);
					alarm.minutes = minute;
				}
				if (repeatOld != repeat) {
					// 灵活运用了四目运算符
					values.put(Alarm.Columns.REPEAT, repeat == 0 ? "只响一次"
							: repeat == 1 ? "周一到周五" : "每天");
					alarm.repeat = repeat == 0 ? "只响一次" : repeat == 1 ? "周一到周五"
							: "每天";
				}
				// equals比较两个对象的内容是否相同
				// == 比较两个对象是否是同一对象。
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
			// 返回更新
			setResult(Alarm.UPDATE_ALARM, intent);
			finish();
			break;
		case R.id.bt_cancel:
			finish();
			break;
		case R.id.ll_repeat:
			showDialog(SHOW_REPEAT);// 如果有-比较多的dialog采用这种方式较好，不乱
			break;
		case R.id.ll_bell:// 选择铃声
			// TODO Auto-generated method stub
			Intent selectBell = new Intent(NewClockActivity.this,
					SelectBellActivity.class);
			selectBell.putExtra("bellPath", bellName);// 带这两个过去有什么用
			selectBell.putExtra("bellName", tv_bell.getText());
			startActivityForResult(selectBell, 1);
			break;
		case R.id.cb_offon:
			if (cb_vibration.isChecked()) {
				// 默认为1 vibration = 1来代表选中
				vibration = 1;
			} else {
				vibration = 0;
			}
			break;
		case R.id.ll_label:
			showDialog(SHOW_LABEL);
			break;
		case R.id.bt_del:
			// 弹出删除闹钟对话框
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
									repeat = which;// 选中的item的ID
									dialog.dismiss();
								}
							}).setNegativeButton("取消", null).create();
			break;
		case SHOW_LABEL:
			final View view = inflater.inflate(R.layout.label_dialog, null);
			final EditText et = (EditText) view.findViewById(R.id.et_label);
			et.setText(tv_label.getText());
			dialog = new AlertDialog.Builder(context)
					.setTitle(getResources().getText(R.string.label_text))
					.setView(view).setNegativeButton("取消", null)
					.setPositiveButton("确定", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							tv_label.setText(et.getText().toString());
						}
					}).create();
			break;
		case DEL_ALARM:
			dialog = new AlertDialog.Builder(context)
					.setTitle(getResources().getText(R.string.del_clock))
					.setMessage("是否删除此闹钟？").setNegativeButton("取消", null)
					.setPositiveButton("确定", new OnClickListener() {
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

	// 获得选择铃声的名称和路径
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
