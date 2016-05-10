package com.ljz.alarm.activity;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ljz.alarm.R;
import com.ljz.alarm.domain.AlarmPreference;

public class SettingActivity extends Activity implements View.OnClickListener {

	private final static int SHOW_CANCEL_MODE = 1;
	private final static int SHOW_NUM_TIMES = 2;
	private final static int SHOW_SHAKE_ITEM = 3;
	private final static int CANCEL = 4;
	private static final int WEATHER = 0;
	private final static String TAG = "SettingActivity";
	private Context context;

	private LinearLayout ll_num_times;
	private LinearLayout ll_weather;
	private LinearLayout ll_shake;
	private CheckBox cb_check;
	private TextView tv_county;
	private TextView tv_cancel_mode;
	private TextView tv_num_time;
	private TextView tv_shake;

	private boolean isBell = true;
	private int cancelAlaemMode = 0;
	private String times = "3";

	private int[] shakeValues = { 4000, 5000, 6000 };
	private int shakeValue = 0;
	private int shakeItemIndex = 0;
	private Map<String, Object> settings;
	private String county_name;
	private String county_code;
	private String countyName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// �����ޱ���
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		init();
	}

	/*
	 * ��ʼ�����
	 */
	private void init() {
		context = this;
		findViewById(R.id.bt_setting_ok).setOnClickListener(this);
		findViewById(R.id.bt_setting_cancel).setOnClickListener(this);
		findViewById(R.id.ll_setting_bell_mode).setOnClickListener(this);
		findViewById(R.id.ll_setting_cancel_mode).setOnClickListener(this);
		ll_weather = (LinearLayout) findViewById(R.id.ll_setting_weather);
		ll_weather.setOnClickListener(this);
		ll_num_times = (LinearLayout) findViewById(R.id.ll_setting_num_times);
		ll_num_times.setOnClickListener(this);
		ll_shake = (LinearLayout) findViewById(R.id.ll_setting_shake);
		ll_shake.setOnClickListener(this);
		cb_check = (CheckBox) findViewById(R.id.cb_setting_check);
		tv_county = (TextView) findViewById(R.id.tv_setting_county);
		tv_cancel_mode = (TextView) findViewById(R.id.tv_setting_cancel_mode);
		tv_num_time = (TextView) findViewById(R.id.tv_setting_num_time);
		tv_shake = (TextView) findViewById(R.id.tv_setting_shake);
		settings = new HashMap<String, Object>();
		countyName = (String) AlarmPreference.getSettingValue(context,
				AlarmPreference.COUNTRY_NAME_KEY);
		isBell = (boolean) AlarmPreference.getSettingValue(context,
				AlarmPreference.BELL_MODE_KEY);
		cancelAlaemMode = (int) AlarmPreference.getSettingValue(context,
				AlarmPreference.CANCEL_MODE_KEY);
		times = ""
				+ (int) AlarmPreference.getSettingValue(context,
						AlarmPreference.NUM_TIMES_KEY);
		shakeValue = (int) AlarmPreference.getSettingValue(context,
				AlarmPreference.SHAKE_ITEM_KEY);
		cb_check.setChecked(isBell);// ��ԭ�趨��isbell����
		// ��ȡԭ���õ�ֵ
		if (countyName != null&&countyName!="") {
			tv_county.setText(countyName);
		} else {
			tv_county.setText("�Ϻ�");
		}
		tv_cancel_mode.setText(getResources().getStringArray(
				R.array.cancel_bell_mode)[cancelAlaemMode]);
		if (cancelAlaemMode == 0) {// ��һ��ѡ��ʱ,���������ʧ��ҡ��ѡ�����
			ll_num_times.setVisibility(View.VISIBLE);
			ll_shake.setVisibility(View.GONE);
		} else if (cancelAlaemMode == 1) {
			ll_num_times.setVisibility(View.GONE);
			ll_shake.setVisibility(View.VISIBLE);
		} else {
			ll_num_times.setVisibility(View.GONE);
			ll_shake.setVisibility(View.GONE);
		}
		if ("3".equals(times)) {// ��tv_num_time������Ϊ3ʱ Ĭ��Ϊ3
		} else {// �����ȡ��������
			tv_num_time.setText(times);
		}
		for (int i = 0; i < shakeValues.length; i++) {
			if (shakeValues[i] == shakeValue) {
				tv_shake.setText(""
						+ getResources().getStringArray(R.array.shake_item)[i]);
				shakeItemIndex = i;// ��
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bt_setting_cancel:
			showDialog(CANCEL);
			break;
		case R.id.bt_setting_ok:// ���� ���� key����value��ʽ
			settings.put(AlarmPreference.BELL_MODE_KEY, isBell);
			settings.put(AlarmPreference.CANCEL_MODE_KEY, cancelAlaemMode);
			settings.put(AlarmPreference.NUM_TIMES_KEY, Integer.parseInt(times));
			settings.put(AlarmPreference.SHAKE_ITEM_KEY, shakeValue);
			settings.put(AlarmPreference.COUNTRY_NAME_KEY, tv_county.getText()
					.toString());
			settings.put(AlarmPreference.COUNTRY_CODE_KEY, county_code);
			AlarmPreference.saveSetting(context, settings);
			Intent MainIntent = new Intent(SettingActivity.this,
					MainActivity.class);
			startActivity(MainIntent);
			// Log.w(TAG+"startActivity(MainIntent);",county_name);
			finish();
			break;
		case R.id.ll_setting_weather:
			// �������ѡ��listView
			Intent intent = new Intent(this, ChooseAreasActivity.class);
			// ����Ҫ����ѡ���city��tv�ϣ�����ҪforResult
			startActivityForResult(intent, 1);
			finish();
			break;
		case R.id.ll_setting_bell_mode:
			if (isBell) {
				isBell = false;
			} else {
				isBell = true;
			}
			cb_check.setChecked(isBell);
			break;
		case R.id.ll_setting_cancel_mode:
			showDialog(SHOW_CANCEL_MODE);
			break;
		case R.id.ll_setting_num_times:
			showDialog(SHOW_NUM_TIMES);
			break;
		case R.id.ll_setting_shake:
			showDialog(SHOW_SHAKE_ITEM);
			break;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case CANCEL:
			dialog = new AlertDialog.Builder(context)
					.setTitle(R.string.action_settings).setMessage("�Ƿ�����������ã�")
					.setNegativeButton("��", null)
					.setPositiveButton("��", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();// dialog��ʧ ����activity
							finish();
						}
					}).create();
			break;
		case SHOW_CANCEL_MODE:
			dialog = new AlertDialog.Builder(context)
					.setTitle(R.string.cancel_alarm_mode)
					.setSingleChoiceItems(R.array.cancel_bell_mode,
							cancelAlaemMode, new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									tv_cancel_mode
											.setText(getResources()
													.getStringArray(
															R.array.cancel_bell_mode)[which]);
									cancelAlaemMode = which;
									if (which == 0) {
										ll_num_times
												.setVisibility(View.VISIBLE);
										ll_shake.setVisibility(View.GONE);
									} else if (which == 1) {
										ll_num_times.setVisibility(View.GONE);
										ll_shake.setVisibility(View.VISIBLE);
									} else {
										ll_num_times.setVisibility(View.GONE);
										ll_shake.setVisibility(View.GONE);
									}
									dialog.dismiss();
								}
							}).setNegativeButton("ȡ��", null).create();
			break;
		case SHOW_NUM_TIMES:
			LayoutInflater inflater2 = LayoutInflater.from(context);
			final View time_view = inflater2.inflate(R.layout.num_label, null);
			final EditText et_times = (EditText) time_view
					.findViewById(R.id.et_times);
			et_times.setText(times);
			dialog = new AlertDialog.Builder(context)
					.setTitle(R.string.num_time).setView(time_view)
					// ��Ҫ
					.setNegativeButton("ȡ��", null)
					.setPositiveButton("ȷ��", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (!TextUtils.isEmpty(et_times.getText())
									&& !"0".equals(et_times.getText()
											.toString().trim())) {
								times = et_times.getText().toString().trim();
							} else {
								times = "3";
							}
							if ("3".equals(times)) {
								tv_num_time.setText(times + "(Ĭ��)");
							} else {
								tv_num_time.setText(times);
							}
						}
					}).create();
			break;
		case SHOW_SHAKE_ITEM:
			dialog = new AlertDialog.Builder(context)
					.setTitle(R.string.shake_title)
					.setSingleChoiceItems(R.array.shake_item, shakeItemIndex,
							new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									tv_shake.setText(getResources()
											.getStringArray(R.array.shake_item)[which]);
									shakeValue = shakeValues[which];
									System.out.println(shakeValue);
									dialog.dismiss();
								}
							}).setNegativeButton("ȡ��", null).create();
			break;
		}
		dialog.setCanceledOnTouchOutside(false);
		return dialog;
	}

	// @Override
	// protected void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	// super.onActivityResult(requestCode, resultCode, data);
	// if (resultCode == RESULT_OK) {// ���سɹ�
	// if (requestCode == 1) {// ����ѡ���county_code county_name
	// county_code = data.getStringExtra("county_code");
	// county_name = data.getStringExtra("county_name");
	// Log.w(TAG+"county_name", county_name);
	// // ��ѡ���country���õ�tv�У�֮�������ll���浽�洢�豸��
	// tv_county.setText(county_name);
	// }
	//
	// } else {// ���ز��ɹ�
	// Toast.makeText(context, "û��ѡ��ɹ���������ѡ��", 0).show();
	//
	// }
	// }

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}
}
