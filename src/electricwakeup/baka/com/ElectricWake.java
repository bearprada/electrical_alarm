package electricwakeup.baka.com;

import java.util.Calendar;
import electricwakeup.baka.com.BluetoothChatService.LocalBinder;
import lhu.f713.stevenpon.com.R;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class ElectricWake extends Activity {
	// Debugging
	private static final String TAG = "BluetoothIOControl";
	private static final boolean D = true;

	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE = 1;

	// Layout Views
	private TextView mTitle;

	private AlarmManager am;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (D) Log.e(TAG, "+++ ON CREATE +++");

		// Set up the window layout
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);

		am = (AlarmManager) getSystemService(ALARM_SERVICE);

		// Set up the custom title
		mTitle = (TextView) findViewById(R.id.title_left_text);
		mTitle.setText(R.string.app_name);
		mTitle = (TextView) findViewById(R.id.title_right_text);
		
		findViewById(R.id.mButton1).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Calendar c = Calendar.getInstance();
				int mHour = c.get(Calendar.HOUR_OF_DAY);
				int mMinute = c.get(Calendar.MINUTE);

				new TimePickerDialog(ElectricWake.this,
						new TimePickerDialog.OnTimeSetListener() {
							public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
								Calendar c = Calendar.getInstance();
								c.set(Calendar.HOUR_OF_DAY, hourOfDay);
								c.set(Calendar.MINUTE, minute);
								c.set(Calendar.SECOND, 0);
								c.set(Calendar.MILLISECOND, 0);

								am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), prepareAlarmIntent());
								/* ��s��ܪ��]�w�x���ɶ� */
								String tmpS = format(hourOfDay) + "�G"
										+ format(minute);
								((TextView) findViewById(R.id.setTime1)).setText(tmpS);
								/* �HToast���ܳ]�w�w���� */
								Toast.makeText(ElectricWake.this,
										"�]�w�x���ɶ���" + tmpS, Toast.LENGTH_SHORT)
										.show();
							}
						}, mHour, mMinute, true).show();
			}
		});

		findViewById(R.id.mButton2).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				am.cancel(prepareAlarmIntent());
				/* �HToast���ܤw�R���]�w�A�ç�s��ܪ��x���ɶ� */
				Toast.makeText(ElectricWake.this, "�x���ɶ��Ѱ�", Toast.LENGTH_SHORT)
						.show();
				((TextView) findViewById(R.id.setTime1)).setText("�ثe�L�]�w");
			}
		});

		LayoutInflater factory = LayoutInflater.from(this);
		final View setView = factory.inflate(R.layout.timeset, null);
		final TimePicker tPicker = (TimePicker) setView
				.findViewById(R.id.tPicker);
		tPicker.setIs24HourView(true);

		/* create�����T�_�x�����]�wDialog */
		final AlertDialog di = new AlertDialog.Builder(ElectricWake.this)
				.setIcon(R.drawable.clock)
				.setTitle("�]�w")
				.setView(setView)
				.setPositiveButton("�T�w", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						/* ��o�]�w�����j��� */
						EditText ed = (EditText) setView
								.findViewById(R.id.mEdit);
						int times = Integer.parseInt(ed.getText().toString()) * 1000;

						Calendar c = Calendar.getInstance();
						c.set(Calendar.HOUR_OF_DAY, tPicker.getCurrentHour());
						c.set(Calendar.MINUTE, tPicker.getCurrentMinute());
						c.set(Calendar.SECOND, 0);
						c.set(Calendar.MILLISECOND, 0);

						am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), times, prepareAlarmIntent());
						
						/* ��s��ܪ��]�w�x���ɶ� */
						String tmpS = format(tPicker.getCurrentHour()) + "�G"
								+ format(tPicker.getCurrentMinute());
						((TextView) findViewById(R.id.setTime2)).setText("�]�w�x���ɶ���" + tmpS + "�}�l�A���ж��j��" + times
								/ 1000 + "��");
						/* �HToast���ܳ]�w�w���� */
						Toast.makeText(
								ElectricWake.this,
								"�]�w�x���ɶ���" + tmpS + "�}�l�A���ж��j��" + times / 1000
										+ "��", Toast.LENGTH_SHORT).show();
					}
				})
				.setNegativeButton("���", null).create();

		findViewById(R.id.mButton3).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Calendar c = Calendar.getInstance();
				tPicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
				tPicker.setCurrentMinute(c.get(Calendar.MINUTE));
				di.show();
			}
		});

		findViewById(R.id.mButton4).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				am.cancel(prepareAlarmIntent());
				Toast.makeText(ElectricWake.this, "�x���ɶ��Ѱ�", Toast.LENGTH_SHORT).show();
				((TextView) findViewById(R.id.setTime2)).setText("�ثe�L�]�w");
			}
		});
	}

	private PendingIntent prepareAlarmIntent() {
		Intent intent = new Intent(this, AlarmAlert.class);
		return PendingIntent.getActivity(this, 1, intent, 0);
	}

	private String format(int x) {
		String s = "" + x;
		if (s.length() == 1)
			s = "0" + s;
		return s;
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
	    }

	    // Called when the connection with the service disconnects unexpectedly
	    public void onServiceDisconnected(ComponentName className) {
	        Log.e(TAG, "onServiceDisconnected");
	        mBound = false;
	    }
	};

	@Override
	public void onResume() {
		super.onResume();
		Intent intent = new Intent(this, BluetoothChatService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	public void onPause() {
		super.onPause();
		unbindService(mConnection);
	}

	private void ensureDiscoverable() {
		if (D) Log.d(TAG, "ensure discoverable");
		Intent discoverableIntent = new Intent(
				BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(
				BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
		startActivity(discoverableIntent);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (D) Log.d(TAG, "onActivityResult " + resultCode);
		switch (requestCode) {
			case REQUEST_CONNECT_DEVICE:
				// When DeviceListActivity returns with a device to connect
	            if (resultCode == Activity.RESULT_OK && mService != null) {
	                // Get the device MAC address
	                String address = data.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
	                // Attempt to connect to the device
                    mService.connect(address);
	            }
				break;
			default:
				super.onActivityResult(requestCode, resultCode, data);
				break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.option_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.scan:
			// Launch the DeviceListActivity to see devices and do scan
			Intent serverIntent = new Intent(this, DeviceListActivity.class);
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
			return true;
		case R.id.discoverable:
			// Ensure this device is discoverable by others
			ensureDiscoverable();
			return true;
		}
		return false;
	}
}