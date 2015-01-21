package electricwakeup.baka.com;

import electricwakeup.baka.com.BluetoothChatService.LocalBinder;
import lhu.f713.stevenpon.com.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;

public class AlarmAlert extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm);
		findViewById(R.id.btn_confirm).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mBound) {
					mService.turnOffElectricalAlarm(null);
					finish();
				}
			}
		});
	}

	private BluetoothChatService mService;
	private boolean mBound;

	private ServiceConnection mConnection = new ServiceConnection() {
		// Called when the connection with the service is established
	    public void onServiceConnected(ComponentName className, IBinder service) {
	        // Because we have bound to an explicit
	        // service that is running in our own process, we can
	        // cast its IBinder to a concrete class and directly access it.
	        LocalBinder binder = (LocalBinder) service;
	        mService = binder.getService();
	        mBound = true;
    		mService.turnOnElectricalAlarm(null);
	    }

	    // Called when the connection with the service disconnects unexpectedly
	    public void onServiceDisconnected(ComponentName className) {
	        mBound = false;
	        // mService.turnOffElectricalAlarm(null);
	    }
	};

	@Override
	public void onStart() {
		super.onStart();
		Intent intent = new Intent(this, BluetoothChatService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	public void onStop() {
		super.onStop();
		unbindService(mConnection);
	}

}