package electricwakeup.baka.com;

/* import相關class */
import lhu.f713.stevenpon.com.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

/* 實際跳出鬧鈴Dialog的Activity */
public class AlarmAlert extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent sender = new Intent();   
		sender.putExtra("state", 1);
		sender.setAction(ElectricWake.MY_ACTION);   

		sendBroadcast(sender);
		/* 跳出的鬧鈴警示 */
		new AlertDialog.Builder(AlarmAlert.this)
				.setIcon(R.drawable.clock)
				.setTitle("鬧鐘響了!!")
				.setMessage("趕快起床吧!!!")
				.setPositiveButton("關掉他",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								Intent sender = new Intent();   
								sender.putExtra("state", 0);
								sender.setAction(ElectricWake.MY_ACTION);
								sendBroadcast(sender);
								/* 關閉Activity */
								AlarmAlert.this.finish();
							}
						}).show();

	}
}