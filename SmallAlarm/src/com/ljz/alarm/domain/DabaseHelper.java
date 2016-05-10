package com.ljz.alarm.domain;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author sjz
 * �̳�SQLiteOpenHelper�����࣬ͨ��getReadableDatabase()�� getWritableDatabase()�����õ�SqliteDabase����
 * 
 */
public class DabaseHelper extends SQLiteOpenHelper {

	private final static int VERSION = 5;
	
	//SQLiteOpenHelper����������иù��캯��
	public DabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	
	public DabaseHelper(Context context, String name,int version) {
		this(context, name, null,version);
		// TODO Auto-generated constructor stub
	}
	
	public DabaseHelper(Context context, String name) {
		this(context, name, VERSION);
		// TODO Auto-generated constructor stub
	}

	/*
	 * �ú������ڵ�һ�δ������ݿ��ʱ����ã�ʵ�������ڵõ�SqliteDabase�����ʱ��ŵ��á�
	 * ������getReadableDatabase()�� getWritableDatabase()ʱ�ŵ��ô˷�����
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("create table alarms("
				  + "_id INTEGER PRIMARY KEY, hour INTEGER,"
				  + "minutes INTEGER,repeat varchar(20),"
				  + "bell varchar(50),vibrate INTEGER,"
				  + "label varchar(50),enabled INTEGER,nextMillis INTEGER)");
		System.out.println("create");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
