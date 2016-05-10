package com.ljz.alarm.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import ccom.ljz.alarm.util.HttpCallbackListener;
import ccom.ljz.alarm.util.WeatherAreasHttpUtil;
import ccom.ljz.alarm.util.WeatherAreasUtility;

import com.ljz.alarm.R;
import com.ljz.alarm.common.AlarmHandle;
import com.ljz.alarm.dao.City;
import com.ljz.alarm.dao.County;
import com.ljz.alarm.dao.Province;
import com.ljz.alarm.domain.AlarmPreference;
import com.ljz.alarm.domain.WeatherDBControl;

public class ChooseAreasActivity extends Activity {
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;
	// 当前选中的级别
	private int currentLevel = 0;
	private TextView tv_areas_tittle;
	private ListView listView;
	private Context context;
	private ArrayList<String> dataList = new ArrayList<String>();;
	private ArrayAdapter<String> adapter;
	private WeatherDBControl weatherDBControl;
	// 省列表
	private List<Province> provincesList;
	// 市列表
	private List<City> cityList;
	// 县列表
	private List<County> countyList;

	// 选中的省份
	private Province selectedProvince;
	// 选中的城市
	private City selectedCity;

	// 是否从WeatherActivity跳转过来的
	private boolean isFromWeatherActivity;
	private HashMap<String, Object> settings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_choose_areas);
		settings = new HashMap<String, Object>();
		initView();
	}

	private void initView() {
		// 初始化控件
		listView = (ListView) findViewById(R.id.lv_areas);
		tv_areas_tittle = (TextView) findViewById(R.id.tv_areas_tittle);
		// 适配器

		adapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_expandable_list_item_1, dataList);
		listView.setAdapter(adapter);
		// listView的点击事件
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (currentLevel == LEVEL_PROVINCE) {
					selectedProvince = provincesList.get(position);
					queryCities();
				} else if (currentLevel == LEVEL_CITY) {
					selectedCity = cityList.get(position);
					// Toast.makeText(context, "点击city", 0).show();
					queryCounties();
				} else if (currentLevel == LEVEL_COUNTY) {
					String countyCode = countyList.get(position)
							.getCountyCode();
					String countyName = countyList.get(position)
							.getCountyName();
					settings.put(AlarmPreference.COUNTRY_CODE_KEY, countyCode);
					settings.put(AlarmPreference.COUNTRY_NAME_KEY, countyName);
					AlarmPreference.saveSetting(context, settings);
					Intent intent = new Intent(ChooseAreasActivity.this,
							SettingActivity.class);
					startActivity(intent);
					// intent.putExtra("county_code", countyCode);
					// intent.putExtra("county_name", countyName);
					// // startActivity(intent);
					// setResult(RESULT_OK, intent);
					finish();
				}
			}
		});
		queryProvinces();// 加载省级数据
	}

	protected void queryProvinces() {

		weatherDBControl = WeatherDBControl
				.getInstance(getApplicationContext());
		// 加载省份列表
		provincesList = weatherDBControl.loadProvinces();
		// 当省份列表加载成功时，把其放入的datalist中
		if (provincesList != null && provincesList.size() > 0) {
			dataList.clear();// 清除原有的
			for (Province province : provincesList) {
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();// 列表刷新
			listView.setSelection(0);// 选中第一个
			tv_areas_tittle.setText("中国");// 后面会改变，所以要重新set
			currentLevel = LEVEL_PROVINCE;// 当前处于province
		} else {
			queryFromServer(null, "province");// 从服务器中查询
		}
	}

	/**
	 * 查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询
	 */
	private void queryCities() {
		// 获取天气控制器的实例
		weatherDBControl = WeatherDBControl
				.getInstance(getApplicationContext());
		cityList = weatherDBControl.loadCities(selectedProvince.getId());
		// Log.w("queryCities()", "queryCities()");
		if (cityList != null && cityList.size() > 0) {
			dataList.clear();
			for (City city : cityList) {
				dataList.add(city.getCityName());
			}

			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			tv_areas_tittle.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		} else {
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}

	}

	/**
	 * 查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询
	 */
	private void queryCounties() {
		// 获取天气控制器的实例
		weatherDBControl = WeatherDBControl
				.getInstance(getApplicationContext());
		countyList = weatherDBControl.loadCounties(selectedCity.getId());
		if (countyList != null && countyList.size() > 0) {
			dataList.clear();
			for (County county : countyList) {
				dataList.add(county.getCountyName());
			}

			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			tv_areas_tittle.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
		} else {
			queryFromServer(selectedCity.getCityCode(), "county");
		}

	}

	/**
	 * 根据传入的代号和类型从服务器上查询省市县数据
	 * 
	 * @param object
	 * @param string
	 */
	private void queryFromServer(final String code, final String type) {
		String address;// url
		if (!TextUtils.isEmpty(code)) {// 当code不为空时，传入
			address = "http://www.weather.com.cn/data/list3/city" + code
					+ ".xml";
		} else {// 为空
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		// 解析url
		WeatherAreasHttpUtil.sendHttpRequest(address,
				new HttpCallbackListener() {

					public void onFinish(String response) {
						boolean result = false;
						if ("province".equals(type)) {
							result = WeatherAreasUtility
									.handleProvincesResponse(weatherDBControl,
											response);
						} else if ("city".equals(type)) {
							result = WeatherAreasUtility.handleCitiesResponse(
									weatherDBControl, response,
									selectedProvince.getId());
						} else if ("county".equals(type)) {
							result = WeatherAreasUtility
									.handleCountiesResponse(weatherDBControl,
											response, selectedCity.getId());
						}
						if (result) {
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									if ("province".equals(type)) {
										queryProvinces();
									} else if ("city".equals(type)) {
										queryCities();
									} else if ("county".equals(type)) {
										queryCounties();
									}
								}
							});
						}

					}

					public void onError(Exception e) {
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								Toast.makeText(getApplicationContext(), "加载失败",
										Toast.LENGTH_SHORT).show();
							}
						});
					}
				});

	}

	/**
	 * 捕获Back键，根据当前的级别来判断，此时应该返回市列表、省列表，还是直接退出
	 */
	@Override
	public void onBackPressed() {
		if (currentLevel == LEVEL_COUNTY) {
			queryCities();
		} else if (currentLevel == LEVEL_CITY) {
			queryProvinces();
		} else {
			Intent intent = new Intent(this, SettingActivity.class);
			startActivity(intent);
			finish();
		}

	}
}
