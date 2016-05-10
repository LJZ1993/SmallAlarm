package ccom.ljz.alarm.util;

import android.text.TextUtils;

import com.ljz.alarm.dao.City;
import com.ljz.alarm.dao.County;
import com.ljz.alarm.dao.Province;
import com.ljz.alarm.domain.WeatherDBControl;

public class WeatherAreasUtility {
	/**
	 * �����ʹ�����������ص�ʡ������
	 * 
	 * @param mDbController
	 * @param response
	 * @return
	 */
	public synchronized static boolean handleProvincesResponse(
			WeatherDBControl mWDbController, String response) {
		if (!TextUtils.isEmpty(response)) {
			String[] allProvinces = response.split(",");
			if (allProvinces != null && allProvinces.length > 0) {
				for (String p : allProvinces) {
					String[] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					// ���������������ݴ洢��Province����
					mWDbController.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * �����ʹ�����������ص��м�����
	 * 
	 * @param mDbController
	 * @param response
	 * @param provinceId
	 * @return
	 */
	public static boolean handleCitiesResponse(WeatherDBControl mWDbController,
			String response, int provinceId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCities = response.split(",");
			if (allCities != null && allCities.length > 0) {
				for (String c : allCities) {
					String[] array = c.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					// ���������������ݴ洢��City����
					mWDbController.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * �����ʹ�����������ص��ؼ�����
	 * 
	 * @param mDbController
	 * @param response
	 * @param cityId
	 * @return
	 */
	public static boolean handleCountiesResponse(
			WeatherDBControl mWDbController, String response, int cityId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCounties = response.split(",");
			if (allCounties != null && allCounties.length > 0) {
				for (String c : allCounties) {
					String[] array = c.split("\\|");
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					// ���������������ݴ洢��County����
					mWDbController.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}

}
