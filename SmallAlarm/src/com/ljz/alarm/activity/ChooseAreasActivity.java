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
	// ��ǰѡ�еļ���
	private int currentLevel = 0;
	private TextView tv_areas_tittle;
	private ListView listView;
	private Context context;
	private ArrayList<String> dataList = new ArrayList<String>();;
	private ArrayAdapter<String> adapter;
	private WeatherDBControl weatherDBControl;
	// ʡ�б�
	private List<Province> provincesList;
	// ���б�
	private List<City> cityList;
	// ���б�
	private List<County> countyList;

	// ѡ�е�ʡ��
	private Province selectedProvince;
	// ѡ�еĳ���
	private City selectedCity;

	// �Ƿ��WeatherActivity��ת������
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
		// ��ʼ���ؼ�
		listView = (ListView) findViewById(R.id.lv_areas);
		tv_areas_tittle = (TextView) findViewById(R.id.tv_areas_tittle);
		// ������

		adapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_expandable_list_item_1, dataList);
		listView.setAdapter(adapter);
		// listView�ĵ���¼�
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (currentLevel == LEVEL_PROVINCE) {
					selectedProvince = provincesList.get(position);
					queryCities();
				} else if (currentLevel == LEVEL_CITY) {
					selectedCity = cityList.get(position);
					// Toast.makeText(context, "���city", 0).show();
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
		queryProvinces();// ����ʡ������
	}

	protected void queryProvinces() {

		weatherDBControl = WeatherDBControl
				.getInstance(getApplicationContext());
		// ����ʡ���б�
		provincesList = weatherDBControl.loadProvinces();
		// ��ʡ���б���سɹ�ʱ����������datalist��
		if (provincesList != null && provincesList.size() > 0) {
			dataList.clear();// ���ԭ�е�
			for (Province province : provincesList) {
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();// �б�ˢ��
			listView.setSelection(0);// ѡ�е�һ��
			tv_areas_tittle.setText("�й�");// �����ı䣬����Ҫ����set
			currentLevel = LEVEL_PROVINCE;// ��ǰ����province
		} else {
			queryFromServer(null, "province");// �ӷ������в�ѯ
		}
	}

	/**
	 * ��ѯѡ��ʡ�����е��У����ȴ����ݿ��ѯ�����û�в�ѯ����ȥ�������ϲ�ѯ
	 */
	private void queryCities() {
		// ��ȡ������������ʵ��
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
	 * ��ѯѡ���������е��أ����ȴ����ݿ��ѯ�����û�в�ѯ����ȥ�������ϲ�ѯ
	 */
	private void queryCounties() {
		// ��ȡ������������ʵ��
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
	 * ���ݴ���Ĵ��ź����ʹӷ������ϲ�ѯʡ��������
	 * 
	 * @param object
	 * @param string
	 */
	private void queryFromServer(final String code, final String type) {
		String address;// url
		if (!TextUtils.isEmpty(code)) {// ��code��Ϊ��ʱ������
			address = "http://www.weather.com.cn/data/list3/city" + code
					+ ".xml";
		} else {// Ϊ��
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		// ����url
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
								Toast.makeText(getApplicationContext(), "����ʧ��",
										Toast.LENGTH_SHORT).show();
							}
						});
					}
				});

	}

	/**
	 * ����Back�������ݵ�ǰ�ļ������жϣ���ʱӦ�÷������б�ʡ�б�����ֱ���˳�
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
