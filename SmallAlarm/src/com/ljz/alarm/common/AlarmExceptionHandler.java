package com.ljz.alarm.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class AlarmExceptionHandler implements UncaughtExceptionHandler{
	public static final String TAG = "MCISExceptionHandler";
    
    //系统默认的UncaughtException处理
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    //CrashHandler实例
    private static AlarmExceptionHandler INSTANCE ;
    //程序的Context对象
    private Context mContext;
    //用来存储设备信息和异常信�?
    private Map<String, String> infos = new LinkedHashMap<String, String>();
 
    //用于格式化日期格式作为日志文件名�?
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
 
    private AlarmExceptionHandler() {
    }
 
    /** 获取AlarmExceptionHandler实例 ,单例模式 */
    public static AlarmExceptionHandler getInstance() {
    	if(INSTANCE == null){
    		INSTANCE = new AlarmExceptionHandler();
    	}
        return INSTANCE;
    }
 
    /**
     * 初始�??
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        //获取系统默认的UncaughtException处理方式
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该AlarmExceptionHandler为程序的默认处理方式
        Thread.setDefaultUncaughtExceptionHandler(this);
    }
 
    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处�?
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Log.e(TAG, "error : ", e);
            }
            //�?出程�?
             System.exit(0);
        }
    }
 
    /**
     * 自定义错误处理，收集错误信息 发�?�错误报告等操作均在此完�?.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        //使用Toast来显示异常信�?
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "很抱歉程序出现异�?,即将�?出�??", Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
        //收集信息
        collectInfo();
        //保存日志文件
        saveCrashInfo2File(ex);
        return true;
    }
     
    /**
     * 收集信息
     */
    public void collectInfo() {
        infos.put("标题", "设备信息");
        infos.put("设备厂商", android.os.Build.MANUFACTURER);
        infos.put("设备品牌", android.os.Build.BRAND);
        infos.put("设备型号", android.os.Build.MODEL);
        infos.put("系统版本", "Android " + android.os.Build.VERSION.RELEASE);
    }
 
    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return  返回文件名称,便于将文件传送到服务�?
     */
    private String saveCrashInfo2File(Throwable ex) {
         
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if("标题".equals(key)){
            	sb.append(value+ "：\n");
            	Log.d(TAG, value);
            	continue;
            }
            sb.append(key + "�?" + value + "\n");
            Log.d(TAG, key + "�?" + value);
        }
        sb.append("\n错误详细信息：\n");
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        Log.e(TAG, result);
        sb.append(result);
        try {
            String time = formatter.format(new Date());
            String fileName = "error_" + time + ".log";
            File dir;
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String path = Environment.getExternalStorageDirectory().getPath()+"/Alarm_Err_Log/";
                dir = new File(path);
            }else{
            	dir = mContext.getDir("ErrLog", Context.MODE_PRIVATE | 
						Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE); 
            }
            if (!dir.exists()) {
            	dir.mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(dir.getPath() +"/"+ fileName);
            fos.write(sb.toString().getBytes());
            fos.close();
            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
        }
        return null;
    }
}
