package electricwakeup.baka.com;

import java.util.Calendar;
import java.util.Set;

import lhu.f713.stevenpon.com.R;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class ElectricWake extends Activity {
	// Debugging
	private static final String TAG = "BluetoothIOControl";
	private static final boolean D = true;

	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;

	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE = 1;
	private static final int REQUEST_ENABLE_BT = 2;

	// Layout Views
	private TextView mTitle;
	// private ListView mConversationView;
	// private EditText mOutEditText;
	private EditText mBT11EditText, mBT21EditText, mBT31EditText,
			mBT41EditText, mBT51EditText, mBT61EditText, mBT71EditText,
			mBT81EditText;
	// private Button mSendButton;
	private Button mBT11On, mBT21On, mBT31On, mBT41On, mBT51On, mBT61On,
			mBT71On, mBT81On, mBT91On, mBTA1On;
	private Button mBT11Off, mBT21Off, mBT31Off, mBT41Off, mBT51Off, mBT61Off,
			mBT71Off, mBT81Off, mBT91Off, mBTA1Off;
	private Button btn_device, btn_disconnect, btn_connect;
	// Name of the connected device
	private String mConnectedDeviceName = null;
	private String address = null;

	// Array adapter for the conversation thread
	// private ArrayAdapter<String> mConversationArrayAdapter;
	// String buffer for outgoing messages
	// private StringBuffer mOutStringBuffer;
	// Local Bluetooth adapter
	private BluetoothAdapter mBluetoothAdapter = null;
	// Member object for the chat services
	private ElectricWakeService mChatService = null;

	/* 宣告物件變數 */
	TextView setTime1;
	TextView setTime2;
	Button mButton1;
	Button mButton2;
	Button mButton3;
	Button mButton4;
	Calendar c = Calendar.getInstance();

	final static String MY_ACTION = "testActivity.MY_ACTION";
	MyReceiver myReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (D)
			Log.e(TAG, "+++ ON CREATE +++");

		// Set up the window layout
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.custom_title);

		// Set up the custom title
		mTitle = (TextView) findViewById(R.id.title_left_text);
		mTitle.setText(R.string.app_name);
		mTitle = (TextView) findViewById(R.id.title_right_text);

		// Get local Bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available",
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		/* 以下為只響一次的鬧鐘的設定 */
		setTime1 = (TextView) findViewById(R.id.setTime1);
		/* 只響一次的鬧鐘的設定Button */
		mButton1 = (Button) findViewById(R.id.mButton1);
		mButton1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				/* 取得按下按鈕時的時間做為TimePickerDialog的預設值 */
				c.setTimeInMillis(System.currentTimeMillis());
				int mHour = c.get(Calendar.HOUR_OF_DAY);
				int mMinute = c.get(Calendar.MINUTE);

				/* 跳出TimePickerDialog來設定時間 */
				new TimePickerDialog(ElectricWake.this,
						new TimePickerDialog.OnTimeSetListener() {
							public void onTimeSet(TimePicker view,
									int hourOfDay, int minute) {
								/* 取得設定後的時間，秒跟毫秒設為0 */
								c.setTimeInMillis(System.currentTimeMillis());
								c.set(Calendar.HOUR_OF_DAY, hourOfDay);
								c.set(Calendar.MINUTE, minute);
								c.set(Calendar.SECOND, 0);
								c.set(Calendar.MILLISECOND, 0);

								/* 指定鬧鐘設定時間到時要執行CallAlarm.class */
								Intent intent = new Intent(ElectricWake.this,
										CallAlarm.class);
								/* 建立PendingIntent */
								PendingIntent sender = PendingIntent
										.getBroadcast(ElectricWake.this, 0,
												intent, 0);
								/*
								 * AlarmManager.RTC_WAKEUP設定服務在系統休眠時同樣會執行
								 * 以set()設定的PendingIntent只會執行一次
								 */
								AlarmManager am;
								am = (AlarmManager) getSystemService(ALARM_SERVICE);
								am.set(AlarmManager.RTC_WAKEUP,
										c.getTimeInMillis(), sender);
								/* 更新顯示的設定鬧鐘時間 */
								String tmpS = format(hourOfDay) + "："
										+ format(minute);
								setTime1.setText(tmpS);
								/* 以Toast提示設定已完成 */
								Toast.makeText(ElectricWake.this,
										"設定鬧鐘時間為" + tmpS, Toast.LENGTH_SHORT)
										.show();
							}
						}, mHour, mMinute, true).show();
			}
		});

		/* 只響一次的鬧鐘的移除Button */
		mButton2 = (Button) findViewById(R.id.mButton2);
		mButton2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ElectricWake.this, CallAlarm.class);
				PendingIntent sender = PendingIntent.getBroadcast(
						ElectricWake.this, 0, intent, 0);
				/* 由AlarmManager中移除 */
				AlarmManager am;
				am = (AlarmManager) getSystemService(ALARM_SERVICE);
				am.cancel(sender);
				/* 以Toast提示已刪除設定，並更新顯示的鬧鐘時間 */
				Toast.makeText(ElectricWake.this, "鬧鐘時間解除", Toast.LENGTH_SHORT)
						.show();
				setTime1.setText("目前無設定");
			}
		});

		/* 以下為重覆響起的鬧鐘的設定 */
		setTime2 = (TextView) findViewById(R.id.setTime2);
		/* create重覆響起的鬧鐘的設定畫面 */
		/* 引用timeset.xml為Layout */
		LayoutInflater factory = LayoutInflater.from(this);
		final View setView = factory.inflate(R.layout.timeset, null);
		final TimePicker tPicker = (TimePicker) setView
				.findViewById(R.id.tPicker);
		tPicker.setIs24HourView(true);

		/* create重覆響起鬧鐘的設定Dialog */
		final AlertDialog di = new AlertDialog.Builder(ElectricWake.this)
				.setIcon(R.drawable.clock)
				.setTitle("設定")
				.setView(setView)
				.setPositiveButton("確定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						/* 取得設定的間隔秒數 */
						EditText ed = (EditText) setView
								.findViewById(R.id.mEdit);
						int times = Integer.parseInt(ed.getText().toString()) * 1000;
						/* 取得設定的開始時間，秒及毫秒設為0 */
						c.setTimeInMillis(System.currentTimeMillis());
						c.set(Calendar.HOUR_OF_DAY, tPicker.getCurrentHour());
						c.set(Calendar.MINUTE, tPicker.getCurrentMinute());
						c.set(Calendar.SECOND, 0);
						c.set(Calendar.MILLISECOND, 0);

						/* 指定鬧鐘設定時間到時要執行CallAlarm.class */
						Intent intent = new Intent(ElectricWake.this,
								CallAlarm.class);
						PendingIntent sender = PendingIntent.getBroadcast(
								ElectricWake.this, 1, intent, 0);
						/* setRepeating()可讓鬧鐘重覆執行 */
						AlarmManager am;
						am = (AlarmManager) getSystemService(ALARM_SERVICE);
						am.setRepeating(AlarmManager.RTC_WAKEUP,
								c.getTimeInMillis(), times, sender);
						/* 更新顯示的設定鬧鐘時間 */
						String tmpS = format(tPicker.getCurrentHour()) + "："
								+ format(tPicker.getCurrentMinute());
						setTime2.setText("設定鬧鐘時間為" + tmpS + "開始，重覆間隔為" + times
								/ 1000 + "秒");
						/* 以Toast提示設定已完成 */
						Toast.makeText(
								ElectricWake.this,
								"設定鬧鐘時間為" + tmpS + "開始，重覆間隔為" + times / 1000
										+ "秒", Toast.LENGTH_SHORT).show();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();

		/* 重覆響起的鬧鐘的設定Button */
		mButton3 = (Button) findViewById(R.id.mButton3);
		mButton3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				/* 取得按下按鈕時的時間做為tPicker的預設值 */
				c.setTimeInMillis(System.currentTimeMillis());
				tPicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
				tPicker.setCurrentMinute(c.get(Calendar.MINUTE));
				/* 跳出設定畫面di */
				di.show();
			}
		});

		/* 重覆響起的鬧鐘的移除Button */
		mButton4 = (Button) findViewById(R.id.mButton4);
		mButton4.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ElectricWake.this, CallAlarm.class);
				PendingIntent sender = PendingIntent.getBroadcast(
						ElectricWake.this, 1, intent, 0);
				/* 由AlarmManager中移除 */
				AlarmManager am;
				am = (AlarmManager) getSystemService(ALARM_SERVICE);
				am.cancel(sender);
				/* 以Toast提示已刪除設定，並更新顯示的鬧鐘時間 */
				Toast.makeText(ElectricWake.this, "鬧鐘時間解除", Toast.LENGTH_SHORT)
						.show();
				setTime2.setText("目前無設定");
			}
		});
		myReceiver = new MyReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(MY_ACTION);
		registerReceiver(myReceiver, intentFilter);

	}

	/* 日期時間顯示兩位數的method */
	private String format(int x) {
		String s = "" + x;
		if (s.length() == 1)
			s = "0" + s;
		return s;
	}

	private Object[] activities = { "BT1010", "BT1020", "BT1030", "BT1040",
			"BT1050", "BT1060", "BT1070", "BT1080" };

	@Override
	public void onStart() {
		super.onStart();
		
		if (D)
			Log.e(TAG, "++ ON START ++");
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		} else {
			if (mChatService == null)
				setupChat();
		}

	}

	@Override
	public synchronized void onResume() {
		super.onResume();
		if (D)
			Log.e(TAG, "+ ON RESUME +");
		// Performing this check in onResume() covers the case in which BT was
		// not enabled during onStart(), so we were paused to enable it...
		// onResume() will be called when ACTION_REQUEST_ENABLE activity
		// returns.
		if (mChatService != null) {
			// Only if the state is STATE_NONE, do we know that we haven't
			// started already
			if (mChatService.getState() == ElectricWakeService.STATE_NONE) {
				// Start the Bluetooth chat services
				mChatService.start();
			}
		}

	}

	private void setupChat() {
		Log.d(TAG, "setupChat()");

		// Initialize the BluetoothChatService to perform bluetooth connections
		mChatService = new ElectricWakeService(this, mHandler);
	}

	private void createNXTConnection() {
		BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
		Set<BluetoothDevice> bondedDevices = btAdapter.getBondedDevices();
		BluetoothDevice nxtDevice = null;

		for (BluetoothDevice bluetoothDevice : bondedDevices) {
			if (bluetoothDevice.getName().equals(mConnectedDeviceName)) {
				nxtDevice = bluetoothDevice;
				break;
			}
		}

		if (nxtDevice == null) {
			Toast toast = Toast.makeText(this, "No paired BT device found",
					Toast.LENGTH_SHORT);
			toast.show();
			return;
		}
		mChatService.connect(nxtDevice);

	}

	@Override
	public synchronized void onPause() {
		super.onPause();
		if (D)
			Log.e(TAG, "- ON PAUSE -");
	}

	@Override
	public void onStop() {
		super.onStop();

		if (D)
			Log.e(TAG, "-- ON STOP --");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Stop the Bluetooth chat services
		unregisterReceiver(myReceiver);
		if (mChatService != null)
			mChatService.stop();
		if (D)
			Log.e(TAG, "--- ON DESTROY ---");
	}

	private void resetport() {
		if (mChatService.getState() == ElectricWakeService.STATE_CONNECTED) {
			CharSequence[] list = new CharSequence[activities.length];
			for (int i = 0; i < list.length; i++) {
				// list[i] = (String)activities[i * 2];
				String message = (String) activities[i];
				sendMessage(message + "\r\n");
			}
		}
	}

	private void scanbt() {
		Intent serverIntent = new Intent(this, DeviceListActivity.class);
		startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
	}

	private void ensureDiscoverable() {
		if (D)
			Log.d(TAG, "ensure discoverable");
		if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(
					BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(discoverableIntent);
		}
	}

	// private void hotlifeonly(){
	// String hotlifecode = "001AFF";
	// String[] checkcode = address.split(":");
	// String data1 = checkcode[0]+checkcode[1]+checkcode[2];
	// if(data1 != hotlifecode){
	// // Attempt to connect to the device
	// mChatService.stop();
	// mChatService.start();
	//
	// }
	// }

	/**
	 * Sends a message.
	 * 
	 * @param message
	 *            A string of text to send.
	 */
	private void sendMessage(String message) {
		// Check that we're actually connected before trying anything
		if (mChatService.getState() != ElectricWakeService.STATE_CONNECTED) {
			Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT)
					.show();
			return;
		}

		// Check that there's actually something to send
		if (message.length() > 0) {
			// Get the message bytes and tell the BluetoothChatService to write
			byte[] send = message.getBytes();
			mChatService.write(send);
		}
	}

	// The Handler that gets information back from the BluetoothChatService
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				if (D)
					Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
				switch (msg.arg1) {
				case ElectricWakeService.STATE_CONNECTED:
					mTitle.setText(R.string.title_connected_to);
					mTitle.append(mConnectedDeviceName);
					resetport();
					// mConversationArrayAdapter.clear();
					break;
				case ElectricWakeService.STATE_CONNECTING:
					// Get the BLuetoothDevice object
					mTitle.setText(R.string.title_connecting);
					break;
				case ElectricWakeService.STATE_LISTEN:
				case ElectricWakeService.STATE_NONE:
					mTitle.setText(R.string.title_not_connected);
					break;
				}
				break;
			case MESSAGE_DEVICE_NAME:
				// save the connected device's name
				mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				Toast.makeText(getApplicationContext(),
						"Connected to " + mConnectedDeviceName,
						Toast.LENGTH_SHORT).show();
				break;
			case MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(),
						msg.getData().getString(TOAST), Toast.LENGTH_SHORT)
						.show();
				break;
			}
		}
	};

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (D)
			Log.d(TAG, "onActivityResult " + resultCode);
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				// Get the device MAC address
				String address = data.getExtras().getString(
						DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				// Get the BLuetoothDevice object
				/*
				 * String[] hotlifecode = {"00","1A","FF"}; String[] checkcode =
				 * address.split(":"); String[] changcode = new String[3];
				 * changcode[0] = checkcode[0]; changcode[1] = checkcode[1];
				 * changcode[2] = checkcode[2];
				 * 
				 * // Attempt to connect to the device if(changcode[0] ==
				 * hotlifecode[0] && changcode[1] == hotlifecode[1] &&
				 * changcode[2] == hotlifecode[2]){ // Attempt to connect to the
				 * device mChatService.stop(); mChatService.start(); } else{
				 * 
				 * }
				 */
				BluetoothDevice device = mBluetoothAdapter
						.getRemoteDevice(address);
				mChatService.connect(device);

			}
			break;
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				// Bluetooth is now enabled, so set up a chat session
				setupChat();
			} else {
				// User did not enable Bluetooth or an error occured
				Log.d(TAG, "BT not enabled");
				Toast.makeText(this, R.string.bt_not_enabled_leaving,
						Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option_menu, menu);
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

	private class MyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			int state = arg1.getIntExtra("state", -1);
			if (state == 1) {
				String message = "BT1051";
				sendMessage(message + "\r\n");
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {

				}
				message = "BT1050";
				sendMessage(message + "\r\n");
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {

				}
				message = "BT1051";
				sendMessage(message + "\r\n");
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {

				}
				message = "BT1050";
				sendMessage(message + "\r\n");
			} else if (state == 0) {
				String message = "BT1071";
				sendMessage(message + "\r\n");
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {

				}
				message = "BT1070";
				sendMessage(message + "\r\n");
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {

				}
				message = "BT1071";
				sendMessage(message + "\r\n");
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {

				}
				message = "BT1070";
				sendMessage(message + "\r\n");
				

			}

		}
	}

}