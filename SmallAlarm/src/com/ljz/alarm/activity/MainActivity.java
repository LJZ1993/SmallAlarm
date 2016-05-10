package com.ljz.alarm.activity;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ljz.alarm.R;
import com.ljz.alarm.common.Alarm;
import com.ljz.alarm.common.AlarmClockManager;
import com.ljz.alarm.common.AlarmHandle;
import com.ljz.alarm.domain.AlarmPreference;
import com.tencent.tauth.Tencent;

public class MainActivity extends Activity implements OnClickListener {

	private final static int DELETE = 1;
	private final static int ABOUT = 2;

	private Context context;
	private final static String TAG = "MainActivity";
	private ProgressDialog progressDialog;
	private boolean flag = true;
	private ListView lv_clocks;
	private List<Alarm> alarms;
	private AlarmAdapter adapter;
	// ��¼�����ؼ���ʱ��
	private long downTime = 0;
	// ��Ѷ
	private Tencent mTencent;
	private TextView tv_county_name;
	private TextView tv_today;
	private TextView tv_today_temp;
	private TextView tv_weather_situation;
	private TextView tv_windy;
	private TextView tv_tomorrow_temp;
	private TextView tv_tomorrow;
	private TextView tv_thirdDay;
	private TextView tv_thirdDay_temp;
	private ImageView im_today;
	private ImageView im_tomorrow;
	private ImageView im_thirdDay;
	private String countyName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// �����ޱ�����
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// ���ؽ���
		setContentView(R.layout.activity_main);
		context = this;// ʡȥ��
		// ��ϵʱ��ת��QQ
		mTencent = Tencent.createInstance("1104776810", context);// ��Ѷ���ܳף�
		// �����һ����������
		AlarmClockManager.setNextAlarm(context);
		init();
	}

	// ��ʼ��
	private void init() {
		// ��������TextView
		tv_county_name = (TextView) findViewById(R.id.tv_county_name);
		// ��������ʵ��
		tv_today = (TextView) findViewById(R.id.tv_today);
		// ���������¶�
		tv_today_temp = (TextView) findViewById(R.id.tv_today_temp);
		// �������
		tv_weather_situation = (TextView) findViewById(R.id.tv_weather_situation);
		// ����
		tv_windy = (TextView) findViewById(R.id.tv_windy);
		// ������¶�
		tv_tomorrow_temp = (TextView) findViewById(R.id.tv_tomorrow_temp);
		tv_tomorrow = (TextView) findViewById(R.id.tv_tomorrow);
		tv_thirdDay = (TextView) findViewById(R.id.tv_thirdDay);
		// ������¶�
		tv_thirdDay_temp = (TextView) findViewById(R.id.tv_thirdDay_temp);
		im_today = (ImageView) findViewById(R.id.im_today);
		im_tomorrow = (ImageView) findViewById(R.id.im_tomorrow);
		im_thirdDay = (ImageView) findViewById(R.id.im_thirdDay);
		// ��ʼ����ť�����onClick����¼�
		findViewById(R.id.ib_add).setOnClickListener(this);
		// setting��ť�ĵ���¼�
		findViewById(R.id.ib_setting).setOnClickListener(this);
		// ��������listView
		lv_clocks = (ListView) findViewById(R.id.lv_clocks);
		// ��������֮��ķָ���
		lv_clocks.setDivider(new ColorDrawable(Color.BLACK));
		// ���÷ָ���֮��� �߶�
		lv_clocks.setDividerHeight(2);
		// �õ���ǰ��������
		getAlarms(this);
		adapter = new AlarmAdapter();// �Զ���������
		lv_clocks.setAdapter(adapter);
		// showWeatherInfo();
	}

	private void showWeatherInfo() {
		// ��ѯ������ϢΪ���²���������Ҫ�첽����
		QueryWeatherInfoAsyncTask asyncTask = new QueryWeatherInfoAsyncTask();
		asyncTask.execute();// ִ��

	}

	/**
	 * @author LJZ AsyncTask
	 * 
	 */
	class QueryWeatherInfoAsyncTask extends AsyncTask<Object, Void, Object> {

		@Override
		protected Object doInBackground(Object... params) {
			try {
				return getWeather(countyName);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			}
			return params;
		}

		private Object getWeather(String countyName)
				throws ClientProtocolException {
			String result = null;
			String url = "http://php.weather.sina.com.cn/iframe/index/w_cl.php?code=js&day=2&city="
					+ countyName + "&dfc=3";
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet method = new HttpGet(url);
			try {
				HttpResponse httpResponse = httpClient.execute(method);
				if (httpResponse.getStatusLine().getStatusCode() == 200) {
					result = EntityUtils.toString(httpResponse.getEntity(),
							"gb2312");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(Object result) {
			if (result != null) {
				String weatherResult = (String) result;
				Log.w("weatherResult", weatherResult);
				if (weatherResult.split(";").length > 1) {
					String a = weatherResult.split(";")[1];
					// Log.w("a",a);
					if (a.split("=").length > 1) {
						String b = a.split("=")[1];
						// Log.w("b ",b );
						String c = b.substring(1, b.length() - 1);// �޳�[]
						// Log.w("c ",c );
						String[] resultArr = c.split("\\}");
						// Log.w("resultArr",resultArr[0]+"");
						if (resultArr.length > 0) {
							todayParse(resultArr[0]);
							Parsetommrow(resultArr[1]);
							Parsethirdday(resultArr[2]);
						}
					} else {
						Toast.makeText(context, "����������Ϣ����������", 0).show();
					}
				} else {
					Toast.makeText(context, "����������Ϣ��������", 0).show();
				}
			} else {
				Toast.makeText(context, "����������Ϣ����������", 0).show();
			}
			super.onPostExecute(result);
		}
	}

	// ��õ�ǰ��������
	private void getAlarms(Context context) {
		// Log.w(TAG, "��������б�"); �ȫ��������
		alarms = AlarmHandle.getAlarms(context);// �õ�����List<Alarm> alarms
	}

	public void Parsethirdday(String weather) {
		String temp = weather.replace("'", "");
		String[] tempArr = temp.split(",");
		String temper = "";
		String weatherSituation = "";
		String windyOri = "";
		if (tempArr.length > 0) {
			for (int i = 0; i < tempArr.length; i++) {
				if (tempArr[i].indexOf("t1:") != -1) {
					temper = tempArr[i].substring(3, tempArr[i].length());
				} else if (tempArr[i].indexOf("t2:") != -1) {
					temper = tempArr[i].substring(3, tempArr[i].length()) + "~"
							+ temper;
				} else if (tempArr[i].indexOf("d1:") != -1) {
					windyOri = tempArr[i].substring(3, tempArr[i].length());
				} else if (tempArr[i].indexOf("s1:") != -1) {
					weatherSituation = tempArr[i].substring(4,
							tempArr[i].length());
				}
			}

			tv_thirdDay.setText("���� ��  " + weatherSituation);
			tv_thirdDay_temp.setText(temper);
			im_thirdDay.setImageResource(imageResId(weatherSituation));

		}
	}

	public void Parsetommrow(String weather) {
		String temp = weather.replace("'", "");
		String[] tempArr = temp.split(",");
		String temper = "";
		String weatherSituation = "";
		String windyOri = "";
		if (tempArr.length > 0) {
			for (int i = 0; i < tempArr.length; i++) {
				if (tempArr[i].indexOf("t1:") != -1) {
					temper = tempArr[i].substring(3, tempArr[i].length()) + "��";
				} else if (tempArr[i].indexOf("t2:") != -1) {
					temper = tempArr[i].substring(3, tempArr[i].length()) + "~"
							+ temper + "��";
				} else if (tempArr[i].indexOf("d1:") != -1) {
					windyOri = tempArr[i].substring(3, tempArr[i].length());
				} else if (tempArr[i].indexOf("s1:") != -1) {
					weatherSituation = tempArr[i].substring(4,
							tempArr[i].length());
				}
			}
			tv_tomorrow.setText("���죺    " + weatherSituation);
			tv_tomorrow_temp.setText(temper);
			im_tomorrow.setImageResource(imageResId(weatherSituation));

		}
	}

	public void todayParse(String weather) {
		String temp = weather.replace("'", "");
		Log.w("temp", temp);
		String[] tempArr = temp.split(",");
		Log.w("tempArr", tempArr[0]);
		String temper = "";
		String weatherSituation = "";
		String windy = "";
		if (tempArr.length > 0) {
			for (int i = 0; i < tempArr.length; i++) {
				// indexOf() �����ɷ���ĳ��ָ�����ַ���ֵ���ַ������״γ��ֵ�λ��
				if (tempArr[i].indexOf("t1:") != -1) {
					temper = tempArr[i].substring(3, tempArr[i].length());
				} else if (tempArr[i].indexOf("t2:") != -1) {
					temper = tempArr[i].substring(3, tempArr[i].length()) + "~"
							+ temper + "��";
				} else if (tempArr[i].indexOf("d1:") != -1) {
					windy = tempArr[i].substring(3, tempArr[i].length());
				} else if (tempArr[i].indexOf("s1:") != -1) {
					weatherSituation = tempArr[i].substring(4,
							tempArr[i].length());
				}
			}

			tv_today_temp.setText("���£�" + temper);
			tv_weather_situation.setText("���������" + weatherSituation);
			tv_windy.setText("����" + windy);
			im_today.setImageResource(imageResId(weatherSituation));

		}

	}

	private int imageResId(String weather) {
		int resID = R.drawable.s_2;
		if (weather.indexOf("����") != -1 || weather.indexOf("��") != -1) {// ����ת�磬������ͬ
																		// indexOf:�����ִ�
			resID = R.drawable.s_1;
		} else if (weather.indexOf("����") != -1 && weather.indexOf("��") != -1) {
			resID = R.drawable.s_2;
		} else if (weather.indexOf("��") != -1 && weather.indexOf("��") != -1) {
			resID = R.drawable.s_3;
		} else if (weather.indexOf("��") != -1 && weather.indexOf("��") != -1) {
			resID = R.drawable.s_12;
		} else if (weather.indexOf("��") != -1 && weather.indexOf("��") != -1) {
			resID = R.drawable.s_12;
		} else if (weather.indexOf("��") != -1) {
			resID = R.drawable.s_13;
		} else if (weather.indexOf("����") != -1) {
			resID = R.drawable.s_2;
		} else if (weather.indexOf("����") != -1) {
			resID = R.drawable.s_3;
		} else if (weather.indexOf("С��") != -1) {
			resID = R.drawable.s_3;
		} else if (weather.indexOf("����") != -1) {
			resID = R.drawable.s_4;
		} else if (weather.indexOf("����") != -1) {
			resID = R.drawable.s_5;
		} else if (weather.indexOf("����") != -1) {
			resID = R.drawable.s_5;
		} else if (weather.indexOf("����") != -1) {
			resID = R.drawable.s_6;
		} else if (weather.indexOf("������") != -1) {
			resID = R.drawable.s_7;
		} else if (weather.indexOf("Сѩ") != -1) {
			resID = R.drawable.s_8;
		} else if (weather.indexOf("��ѩ") != -1) {
			resID = R.drawable.s_9;
		} else if (weather.indexOf("��ѩ") != -1) {
			resID = R.drawable.s_10;
		} else if (weather.indexOf("��ѩ") != -1) {
			resID = R.drawable.s_10;
		} else if (weather.indexOf("��ɳ") != -1) {
			resID = R.drawable.s_11;
		} else if (weather.indexOf("ɳ��") != -1) {
			resID = R.drawable.s_11;
		} else if (weather.indexOf("��") != -1) {
			resID = R.drawable.s_12;
		}
		return resID;
	}

	// �Զ���ListView��������
	class AlarmAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// alarms�����ӵļ���
			if (alarms != null) {// ��listView�д���alarmʱ���������ĸ�����û�оͷ���0
				return alarms.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return alarms.get(position);// �Զ���������������Ѿ������ˣ�
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// �Ż�listView
			// ���ú� convertView ������ View���м�ÿ�� getView() ���½���ListView �ĺ���ԭ���������
			// View��ListView ����һ����������Item ���������ʱ�� View ����յ������Ҫ��ʾ�µ� Item
			// ��ʱ�򣬾;������û���������� View��
			// getView() ��Ҫ�������ٵ����飬��Ҫ�к�ʱ�Ĳ������ر��ǻ�����ʱ��Ҫ����ͼƬ��ͣ�����ټ���
			Holder holder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.alarm_list_item, null);
				holder = new Holder();
				// ͨ��convertView��setTag��view����Ӧ��holder�������һ��
				// ���������findViewById()������
				convertView.setTag(holder);

				// ��ʾ������Ϣ��LinearLayout
				holder.ll_info = (LinearLayout) convertView
						.findViewById(R.id.ll_info);
				// ʱ��
				holder.tv_time = (TextView) convertView
						.findViewById(R.id.tv_time);
				// �ظ�
				holder.tv_repeat = (TextView) convertView
						.findViewById(R.id.tv_repeat);
				// ����
				holder.cb_switch = (CheckBox) convertView
						.findViewById(R.id.cb_switch);

			} else {
				holder = (Holder) convertView.getTag();
			}

			final Alarm alarm = alarms.get(position);
			// �ѵ�������ӵ�id set��holder.ll_info����
			// View�е�setTag(Onbect)��ʾ��View���һ����������ݣ��Ժ������getTag()���������ȡ������
			holder.ll_info.setTag(alarm.id);
			// holder.tv_time.setTag(alarm.id);
			// ��������༭����
			holder.ll_info.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {// ����holder.ll_info�ĵ���¼��������޸����ӽ���

					Intent intent = new Intent(context, NewClockActivity.class);
					// Я�����������id����NewClockActivity����
					// ���ݵ���һ��alarm����
					intent.putExtra("alarm", alarms.get(position));
					// ����ֵ requestCodeΪ10
					startActivityForResult(intent, 10);
				}
			});
			String hourStr = (alarm.hour + "").length() == 1 ? "0" + alarm.hour
					: alarm.hour + "";// ����hour=5 ��true ��Ϊ10��Ϊfalse �˷����Ϻ�
			String minutesStr = (alarm.minutes + "").length() == 1 ? "0"
					+ alarm.minutes : alarm.minutes + "";
			holder.tv_time.setText(hourStr + ":" + minutesStr);
			holder.tv_repeat.setText(alarm.repeat);

			holder.cb_switch.setChecked(alarm.enabled == 1 ? true : false);
			// ����ԭ����
			// ���ؿ���
			holder.cb_switch.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ContentValues values = new ContentValues();
					boolean isChecked = true;// Ĭ��������ture
					if (((CheckBox) v).isChecked()) {
						isChecked = true;
					} else {
						isChecked = false;
					}
					// �������ݿ����е�����
					values.put(Alarm.Columns.ENABLED, isChecked ? 1 : 0);
					AlarmHandle.updateAlarm(context, values, alarm.id);
					alarms.get(position).enabled = isChecked ? 1 : 0;
					if (isChecked) {
						// ������
						AlarmClockManager.setAlarm(context, alarm);
					} else {
						// �ر�����
						AlarmClockManager.cancelAlarm(context, alarm.id);
					}

				}
			});
			return convertView;
		}

		class Holder {// Holder���еĳ�Ա������item�еĿؼ�
			LinearLayout ll_info;
			TextView tv_time;
			TextView tv_repeat;
			CheckBox cb_switch;
		}
	}

	/*
	 * �ϲ�ҳ�淵�غ�����Ӧ����
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		Log.v(TAG, "����adpater");
		switch (resultCode) {
		case Alarm.UPDATE_ALARM:
			// �������޸����Ӷ��õ������result
			if (adapter != null) {
				// ����������
				// ��ȡ���е����� �����½����޸ģ�ɾ�������ӻ�ı����ӵĸ�����������Ҫ���»�ȡ����
				getAlarms(context);
				adapter.notifyDataSetChanged();// ��̬ˢ���б�
			}
			// ��ȡsetResult���ݹ���������
			Alarm alarm = (Alarm) data.getSerializableExtra("alarm");
			if (alarm != null) {
				//
				AlarmClockManager.setAlarm(context, alarm);
			}
			break;
		case Alarm.DELETE_ALARM:
			if (adapter != null) {
				getAlarms(context);
				adapter.notifyDataSetChanged();
			}
			break;
		}
	}

	/*
	 * ������������ť�ĵ���¼�
	 */
	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.ib_add:
			// ���뵽������ӽ���
			intent = new Intent(this, NewClockActivity.class);
			// ���ڷ������� intention+requestCode
			startActivityForResult(intent, 10);
			break;
		case R.id.ib_setting:
			intent = new Intent(this, SettingActivity.class);
			startActivity(intent);
			finish();
			break;
		}

	}

	/*
	 * �����ٴλ�ý��� ��������
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (adapter != null) {
			Log.v(TAG, "onResume�и��������б�");
			getAlarms(context);
			adapter.notifyDataSetChanged();
		}
		String county_name = (String) AlarmPreference.getSettingValue(context,
				AlarmPreference.COUNTRY_NAME_KEY);
		if (county_name != null) {
			tv_county_name.setText(county_name);
			countyName = tv_county_name.getText().toString();
			showWeatherInfo();
		} else {
			tv_county_name.setText("�Ϻ�");
			countyName = tv_county_name.getText().toString();
			showWeatherInfo();
		}
		// Intent settingIntent = getIntent();
		// String county_name = settingIntent.getStringExtra("county_name");
		// if (county_name != null) {
		// tv_county_name.setText(county_name);
		// countyName = tv_county_name.getText().toString();
		// showWeatherInfo();
		// } else {
		// tv_county_name.setText("�Ϻ�");
		// countyName = tv_county_name.getText().toString();
		// showWeatherInfo();
		// }
	}

	/*
	 * �����Ի���
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case DELETE:
			dialog = new AlertDialog.Builder(context)
					.setTitle(R.string.action_del)
					.setMessage("ȷ��Ҫɾ������������")
					.setNegativeButton("ȡ��", null)
					.setPositiveButton("ȷ��",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									AlarmHandle.deleteAllAlarm(context);
									// ����adpater
									if (adapter != null) {
										getAlarms(context);
										adapter.notifyDataSetChanged();
									}
									dialog.dismiss();
								}
							}).create();
			break;
		case ABOUT:
			dialog = new AlertDialog.Builder(context)
					.setTitle(R.string.action_about)
					.setMessage(R.string.about_content)
					.setNegativeButton("�ر�", null).create();
			break;
		}
		return dialog;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (System.currentTimeMillis() - downTime > 2000) {
				Toast.makeText(context, "�ٰ�һ���˳�����", Toast.LENGTH_SHORT).show();
				downTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
