package ccom.ljz.alarm.util;

public interface HttpCallbackListener {
	/**
	 * 请求完成
	 * 
	 * @param response
	 */
	void onFinish(String response);

	/**
	 * 请求失败
	 * 
	 * @param e
	 */
	void onError(Exception e);

}
