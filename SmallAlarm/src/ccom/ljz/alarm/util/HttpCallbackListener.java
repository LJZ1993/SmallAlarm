package ccom.ljz.alarm.util;

public interface HttpCallbackListener {
	/**
	 * �������
	 * 
	 * @param response
	 */
	void onFinish(String response);

	/**
	 * ����ʧ��
	 * 
	 * @param e
	 */
	void onError(Exception e);

}
