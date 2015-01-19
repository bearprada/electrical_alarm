package electricwakeup.baka.com;

import java.util.Calendar;
import java.util.Set;

import electricwakeup.baka.com.BluetoothChatService.LocalBinder;
import lhu.f713.stevenpon.com.R;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
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

	TextView setTime1;
	TextView setTime2;
	Button mButton1;
	Button mButton2;
	Button mButton3;
	Button mButton4;
	Calendar c = Calendar.getInstance();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (D) Log.e(TAG, "+++ ON CREATE +++");

		// Set up the window layout
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.custom_title);

		// Set up the custom title
		mTitle = (TextView) findViewById(R.id.title_left_text);
		mTitle.setText(R.string.app_name);
		mTitle = (TextView) findViewById(R.id.title_right_text);

		/* �H�U���u�T�@�����x�����]�w */
		setTime1 = (TextView) findViewById(R.id.setTime1);
		/* �u�T�@�����x�����]�wButton */
		mButton1 = (Button) findViewById(R.id.mButton1);
		mButton1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				/* ��o���U���s�ɪ��ɶ�����TimePickerDialog���w�]�� */
				c.setTimeInMillis(System.currentTimeMillis());
				int mHour = c.get(Calendar.HOUR_OF_DAY);
				int mMinute = c.get(Calendar.MINUTE);

				/* ���XTimePickerDialog�ӳ]�w�ɶ� */
				new TimePickerDialog(ElectricWake.this,
						new TimePickerDialog.OnTimeSetListener() {
							public void onTimeSet(TimePicker view,
									int hourOfDay, int minute) {
								/* ��o�]�w�᪺�ɶ��A���@��]��0 */
								c.setTimeInMillis(System.currentTimeMillis());
								c.set(Calendar.HOUR_OF_DAY, hourOfDay);
								c.set(Calendar.MINUTE, minute);
								c.set(Calendar.SECOND, 0);
								c.set(Calendar.MILLISECOND, 0);

								/* ��w�x���]�w�ɶ���ɭn����CallAlarm.class */
								Intent intent = new Intent(ElectricWake.this,
										CallAlarm.class);
								/* �إ�PendingIntent */
								PendingIntent sender = PendingIntent
										.getBroadcast(ElectricWake.this, 0,
												intent, 0);
								/*
								 * AlarmManager.RTC_WAKEUP�]�w�A�Ȧb�t�Υ�v�ɦP�˷|����
								 * �Hset()�]�w��PendingIntent�u�|����@��
								 */
								AlarmManager am;
								am = (AlarmManager) getSystemService(ALARM_SERVICE);
								am.set(AlarmManager.RTC_WAKEUP,
										c.getTimeInMillis(), sender);
								/* ��s��ܪ��]�w�x���ɶ� */
								String tmpS = format(hourOfDay) + "�G"
										+ format(minute);
								setTime1.setText(tmpS);
								/* �HToast���ܳ]�w�w���� */
								Toast.makeText(ElectricWake.this,
										"�]�w�x���ɶ���" + tmpS, Toast.LENGTH_SHORT)
										.show();
							}
						}, mHour, mMinute, true).show();
			}
		});

		/* �u�T�@�����x��������Button */
		mButton2 = (Button) findViewById(R.id.mButton2);
		mButton2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ElectricWake.this, CallAlarm.class);
				PendingIntent sender = PendingIntent.getBroadcast(
						ElectricWake.this, 0, intent, 0);
				/* ��AlarmManager������ */
				AlarmManager am;
				am = (AlarmManager) getSystemService(ALARM_SERVICE);
				am.cancel(sender);
				/* �HToast���ܤw�R���]�w�A�ç�s��ܪ��x���ɶ� */
				Toast.makeText(ElectricWake.this, "�x���ɶ��Ѱ�", Toast.LENGTH_SHORT)
						.show();
				setTime1.setText("�ثe�L�]�w");
			}
		});

		/* �H�U�������T�_���x�����]�w */
		setTime2 = (TextView) findViewById(R.id.setTime2);
		/* create�����T�_���x�����]�w�e�� */
		/* �ޥ�timeset.xml��Layout */
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
						/* ��o�]�w���}�l�ɶ��A��β@��]��0 */
						c.setTimeInMillis(System.currentTimeMillis());
						c.set(Calendar.HOUR_OF_DAY, tPicker.getCurrentHour());
						c.set(Calendar.MINUTE, tPicker.getCurrentMinute());
						c.set(Calendar.SECOND, 0);
						c.set(Calendar.MILLISECOND, 0);

						/* ��w�x���]�w�ɶ���ɭn����CallAlarm.class */
						Intent intent = new Intent(ElectricWake.this,
								CallAlarm.class);
						PendingIntent sender = PendingIntent.getBroadcast(
								ElectricWake.this, 1, intent, 0);
						/* setRepeating()�i��x�����а��� */
						AlarmManager am;
						am = (AlarmManager) getSystemService(ALARM_SERVICE);
						am.setRepeating(AlarmManager.RTC_WAKEUP,
								c.getTimeInMillis(), times, sender);
						/* ��s��ܪ��]�w�x���ɶ� */
						String tmpS = format(tPicker.getCurrentHour()) + "�G"
								+ format(tPicker.getCurrentMinute());
						setTime2.setText("�]�w�x���ɶ���" + tmpS + "�}�l�A���ж��j��" + times
								/ 1000 + "��");
						/* �HToast���ܳ]�w�w���� */
						Toast.makeText(
								ElectricWake.this,
								"�]�w�x���ɶ���" + tmpS + "�}�l�A���ж��j��" + times / 1000
										+ "��", Toast.LENGTH_SHORT).show();
					}
				})
				.setNegativeButton("���", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();

		/* �����T�_���x�����]�wButton */
		mButton3 = (Button) findViewById(R.id.mButton3);
		mButton3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				/* ��o���U���s�ɪ��ɶ�����tPicker���w�]�� */
				c.setTimeInMillis(System.currentTimeMillis());
				tPicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
				tPicker.setCurrentMinute(c.get(Calendar.MINUTE));
				/* ���X�]�w�e��di */
				di.show();
			}
		});

		/* �����T�_���x��������Button */
		mButton4 = (Button) findViewById(R.id.mButton4);
		mButton4.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ElectricWake.this, CallAlarm.class);
				PendingIntent sender = PendingIntent.getBroadcast(
						ElectricWake.this, 1, intent, 0);
				/* ��AlarmManager������ */
				AlarmManager am;
				am = (AlarmManager) getSystemService(ALARM_SERVICE);
				am.cancel(sender);
				/* �HToast���ܤw�R���]�w�A�ç�s��ܪ��x���ɶ� */
				Toast.makeText(ElectricWake.this, "�x���ɶ��Ѱ�", Toast.LENGTH_SHORT)
						.show();
				setTime2.setText("�ثe�L�]�w");
			}
		});

		myReceiver = new MyReceiver();
	}

	/* ����ɶ���ܨ��ƪ�method */
	private String format(int x) {
		String s = "" + x;
		if (s.length() == 1)
			s = "0" + s;
		return s;
	}

	private Object[] activities = { "BT1010", "BT1020", "BT1030", "BT1040",
			"BT1050", "BT1060", "BT1070", "BT1080" };

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

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(MY_ACTION);
		registerReceiver(myReceiver, intentFilter);
	}

	@Override
	public void onPause() {
		super.onPause();
		unbindService(mConnection);
		unregisterReceiver(myReceiver);
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
	}

	private void ensureDiscoverable() {
		if (D) Log.d(TAG, "ensure discoverable");
		Intent discoverableIntent = new Intent(
				BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(
				BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
		startActivity(discoverableIntent);
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
		// Check that there's actually something to send
		if (!TextUtils.isEmpty(message)) {
			// Get the message bytes and tell the BluetoothChatService to write
			mService.write(message.getBytes());
		}
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

	private class MyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context arg0, Intent arg1) {
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