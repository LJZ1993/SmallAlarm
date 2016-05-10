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
	// 记录按返回键的时间
	private long downTime = 0;
	// 腾讯
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
		// 设置无标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 加载界面
		setContentView(R.layout.activity_main);
		context = this;// 省去了
		// 联系时跳转到QQ
		mTencent = Tencent.createInstance("1104776810", context);// 腾讯的密匙？
		// 获的下一个响铃闹钟
		AlarmClockManager.setNextAlarm(context);
		init();
	}

	// 初始化
	private void init() {
		// 城市名的TextView
		tv_county_name = (TextView) findViewById(R.id.tv_county_name);
		// 今日天气实况
		tv_today = (TextView) findViewById(R.id.tv_today);
		// 今日天气温度
		tv_today_temp = (TextView) findViewById(R.id.tv_today_temp);
		// 天气情况
		tv_weather_situation = (TextView) findViewById(R.id.tv_weather_situation);
		// 风力
		tv_windy = (TextView) findViewById(R.id.tv_windy);
		// 明天的温度
		tv_tomorrow_temp = (TextView) findViewById(R.id.tv_tomorrow_temp);
		tv_tomorrow = (TextView) findViewById(R.id.tv_tomorrow);
		tv_thirdDay = (TextView) findViewById(R.id.tv_thirdDay);
		// 后天的温度
		tv_thirdDay_temp = (TextView) findViewById(R.id.tv_thirdDay_temp);
		im_today = (ImageView) findViewById(R.id.im_today);
		im_tomorrow = (ImageView) findViewById(R.id.im_tomorrow);
		im_thirdDay = (ImageView) findViewById(R.id.im_thirdDay);
		// 初始化按钮并添加onClick点击事件
		findViewById(R.id.ib_add).setOnClickListener(this);
		// setting按钮的点击事件
		findViewById(R.id.ib_setting).setOnClickListener(this);
		// 加载闹钟listView
		lv_clocks = (ListView) findViewById(R.id.lv_clocks);
		// 设置闹钟之间的分割线
		lv_clocks.setDivider(new ColorDrawable(Color.BLACK));
		// 设置分割线之间的 高度
		lv_clocks.setDividerHeight(2);
		// 得到当前所有闹钟
		getAlarms(this);
		adapter = new AlarmAdapter();// 自定义适配器
		lv_clocks.setAdapter(adapter);
		// showWeatherInfo();
	}

	private void showWeatherInfo() {
		// 查询天气信息为好事操作，所以要异步处理
		QueryWeatherInfoAsyncTask asyncTask = new QueryWeatherInfoAsyncTask();
		asyncTask.execute();// 执行

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
						String c = b.substring(1, b.length() - 1);// 剔除[]
						// Log.w("c ",c );
						String[] resultArr = c.split("\\}");
						// Log.w("resultArr",resultArr[0]+"");
						if (resultArr.length > 0) {
							todayParse(resultArr[0]);
							Parsetommrow(resultArr[1]);
							Parsethirdday(resultArr[2]);
						}
					} else {
						Toast.makeText(context, "查无天气信息，请检查网络", 0).show();
					}
				} else {
					Toast.makeText(context, "查无天气信息，请检查网", 0).show();
				}
			} else {
				Toast.makeText(context, "查无天气信息，请检查网络", 0).show();
			}
			super.onPostExecute(result);
		}
	}

	// 获得当前所有闹钟
	private void getAlarms(Context context) {
		// Log.w(TAG, "获得闹钟列表"); 活动全部的闹钟
		alarms = AlarmHandle.getAlarms(context);// 得到的是List<Alarm> alarms
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

			tv_thirdDay.setText("后天 ：  " + weatherSituation);
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
					temper = tempArr[i].substring(3, tempArr[i].length()) + "℃";
				} else if (tempArr[i].indexOf("t2:") != -1) {
					temper = tempArr[i].substring(3, tempArr[i].length()) + "~"
							+ temper + "℃";
				} else if (tempArr[i].indexOf("d1:") != -1) {
					windyOri = tempArr[i].substring(3, tempArr[i].length());
				} else if (tempArr[i].indexOf("s1:") != -1) {
					weatherSituation = tempArr[i].substring(4,
							tempArr[i].length());
				}
			}
			tv_tomorrow.setText("明天：    " + weatherSituation);
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
				// indexOf() 方法可返回某个指定的字符串值在字符串中首次出现的位置
				if (tempArr[i].indexOf("t1:") != -1) {
					temper = tempArr[i].substring(3, tempArr[i].length());
				} else if (tempArr[i].indexOf("t2:") != -1) {
					temper = tempArr[i].substring(3, tempArr[i].length()) + "~"
							+ temper + "℃";
				} else if (tempArr[i].indexOf("d1:") != -1) {
					windy = tempArr[i].substring(3, tempArr[i].length());
				} else if (tempArr[i].indexOf("s1:") != -1) {
					weatherSituation = tempArr[i].substring(4,
							tempArr[i].length());
				}
			}

			tv_today_temp.setText("气温：" + temper);
			tv_weather_situation.setText("天气情况：" + weatherSituation);
			tv_windy.setText("风向：" + windy);
			im_today.setImageResource(imageResId(weatherSituation));

		}

	}

	private int imageResId(String weather) {
		int resID = R.drawable.s_2;
		if (weather.indexOf("多云") != -1 || weather.indexOf("晴") != -1) {// 多云转晴，以下类同
																		// indexOf:包含字串
			resID = R.drawable.s_1;
		} else if (weather.indexOf("多云") != -1 && weather.indexOf("阴") != -1) {
			resID = R.drawable.s_2;
		} else if (weather.indexOf("阴") != -1 && weather.indexOf("雨") != -1) {
			resID = R.drawable.s_3;
		} else if (weather.indexOf("晴") != -1 && weather.indexOf("雨") != -1) {
			resID = R.drawable.s_12;
		} else if (weather.indexOf("晴") != -1 && weather.indexOf("雾") != -1) {
			resID = R.drawable.s_12;
		} else if (weather.indexOf("晴") != -1) {
			resID = R.drawable.s_13;
		} else if (weather.indexOf("多云") != -1) {
			resID = R.drawable.s_2;
		} else if (weather.indexOf("阵雨") != -1) {
			resID = R.drawable.s_3;
		} else if (weather.indexOf("小雨") != -1) {
			resID = R.drawable.s_3;
		} else if (weather.indexOf("中雨") != -1) {
			resID = R.drawable.s_4;
		} else if (weather.indexOf("大雨") != -1) {
			resID = R.drawable.s_5;
		} else if (weather.indexOf("暴雨") != -1) {
			resID = R.drawable.s_5;
		} else if (weather.indexOf("冰雹") != -1) {
			resID = R.drawable.s_6;
		} else if (weather.indexOf("雷阵雨") != -1) {
			resID = R.drawable.s_7;
		} else if (weather.indexOf("小雪") != -1) {
			resID = R.drawable.s_8;
		} else if (weather.indexOf("中雪") != -1) {
			resID = R.drawable.s_9;
		} else if (weather.indexOf("大雪") != -1) {
			resID = R.drawable.s_10;
		} else if (weather.indexOf("暴雪") != -1) {
			resID = R.drawable.s_10;
		} else if (weather.indexOf("扬沙") != -1) {
			resID = R.drawable.s_11;
		} else if (weather.indexOf("沙尘") != -1) {
			resID = R.drawable.s_11;
		} else if (weather.indexOf("雾") != -1) {
			resID = R.drawable.s_12;
		}
		return resID;
	}

	// 自定义ListView的适配器
	class AlarmAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// alarms是闹钟的集合
			if (alarms != null) {// 当listView中存在alarm时，返回它的个数，没有就返回0
				return alarms.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			return alarms.get(position);// 自定义的适配器数据已经加入了，
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// 优化listView
			// 利用好 convertView 来重用 View，切忌每次 getView() 都新建。ListView 的核心原理就是重用
			// View。ListView 中有一个回收器，Item 滑出界面的时候 View 会回收到这里，需要显示新的 Item
			// 的时候，就尽量重用回收器里面的 View。
			// getView() 中要做尽量少的事情，不要有耗时的操作。特别是滑动的时候不要加载图片，停下来再加载
			Holder holder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.alarm_list_item, null);
				holder = new Holder();
				// 通过convertView的setTag将view与相应的holder对象绑定在一起。
				// 避免大量的findViewById()操作。
				convertView.setTag(holder);

				// 显示闹钟信息的LinearLayout
				holder.ll_info = (LinearLayout) convertView
						.findViewById(R.id.ll_info);
				// 时间
				holder.tv_time = (TextView) convertView
						.findViewById(R.id.tv_time);
				// 重复
				holder.tv_repeat = (TextView) convertView
						.findViewById(R.id.tv_repeat);
				// 开关
				holder.cb_switch = (CheckBox) convertView
						.findViewById(R.id.cb_switch);

			} else {
				holder = (Holder) convertView.getTag();
			}

			final Alarm alarm = alarms.get(position);
			// 把点击的闹钟的id set到holder.ll_info上面
			// View中的setTag(Onbect)表示给View添加一个格外的数据，以后可以用getTag()将这个数据取出来。
			holder.ll_info.setTag(alarm.id);
			// holder.tv_time.setTag(alarm.id);
			// 单击进入编辑界面
			holder.ll_info.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {// 设置holder.ll_info的点击事件，进入修改闹钟界面

					Intent intent = new Intent(context, NewClockActivity.class);
					// 携带点击的闹钟id进入NewClockActivity界面
					// 传递的是一个alarm对象
					intent.putExtra("alarm", alarms.get(position));
					// 返回值 requestCode为10
					startActivityForResult(intent, 10);
				}
			});
			String hourStr = (alarm.hour + "").length() == 1 ? "0" + alarm.hour
					: alarm.hour + "";// 比如hour=5 则true 若为10则为false 此方法较好
			String minutesStr = (alarm.minutes + "").length() == 1 ? "0"
					+ alarm.minutes : alarm.minutes + "";
			holder.tv_time.setText(hourStr + ":" + minutesStr);
			holder.tv_repeat.setText(alarm.repeat);

			holder.cb_switch.setChecked(alarm.enabled == 1 ? true : false);
			// 加载原设置
			// 开关控制
			holder.cb_switch.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ContentValues values = new ContentValues();
					boolean isChecked = true;// 默认设置是ture
					if (((CheckBox) v).isChecked()) {
						isChecked = true;
					} else {
						isChecked = false;
					}
					// 更新数据库里中的数据
					values.put(Alarm.Columns.ENABLED, isChecked ? 1 : 0);
					AlarmHandle.updateAlarm(context, values, alarm.id);
					alarms.get(position).enabled = isChecked ? 1 : 0;
					if (isChecked) {
						// 打开闹钟
						AlarmClockManager.setAlarm(context, alarm);
					} else {
						// 关闭闹钟
						AlarmClockManager.cancelAlarm(context, alarm.id);
					}

				}
			});
			return convertView;
		}

		class Holder {// Holder类中的成员变量是item中的控件
			LinearLayout ll_info;
			TextView tv_time;
			TextView tv_repeat;
			CheckBox cb_switch;
		}
	}

	/*
	 * 上层页面返回后做相应操作
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		Log.v(TAG, "更新adpater");
		switch (resultCode) {
		case Alarm.UPDATE_ALARM:
			// 创建和修改闹钟都用的是这个result
			if (adapter != null) {
				// 闹钟适配器
				// 获取所有的闹钟 由于新建和修改（删除）闹钟会改变闹钟的个数，所以需要重新获取闹钟
				getAlarms(context);
				adapter.notifyDataSetChanged();// 动态刷新列表
			}
			// 获取setResult传递过来的数据
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
	 * 主界面两个按钮的点击事件
	 */
	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.ib_add:
			// 进入到添加闹钟界面
			intent = new Intent(this, NewClockActivity.class);
			// 用于返回数据 intention+requestCode
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
	 * 界面再次获得焦点 更新数据
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (adapter != null) {
			Log.v(TAG, "onResume中更新闹钟列表");
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
			tv_county_name.setText("上海");
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
		// tv_county_name.setText("上海");
		// countyName = tv_county_name.getText().toString();
		// showWeatherInfo();
		// }
	}

	/*
	 * 创建对话框。
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case DELETE:
			dialog = new AlertDialog.Builder(context)
					.setTitle(R.string.action_del)
					.setMessage("确定要删除所有闹钟吗？")
					.setNegativeButton("取消", null)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									AlarmHandle.deleteAllAlarm(context);
									// 更新adpater
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
					.setNegativeButton("关闭", null).create();
			break;
		}
		return dialog;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (System.currentTimeMillis() - downTime > 2000) {
				Toast.makeText(context, "再按一次退出程序", Toast.LENGTH_SHORT).show();
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
