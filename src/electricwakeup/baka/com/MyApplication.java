package electricwakeup.baka.com;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

public class MyApplication extends Application {

	@Override
	public void onCreate () {
		super.onCreate();
		Log.d("eAlarm", "Application - onCreate");
	    startService(new Intent(this, BluetoothChatService.class));
	}
	
	@Override
	public void onTerminate() { 
		super.onTerminate();
		Log.d("eAlarm", "Application - onTerminate");
	    stopService(new Intent(this, BluetoothChatService.class));
	}
}
