package electricwakeup.baka.com;

/* import����class */
import lhu.f713.stevenpon.com.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

/* ��ڸ��X�x�aDialog��Activity */
public class AlarmAlert extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent sender = new Intent();   
		sender.putExtra("state", 1);
		sender.setAction(ElectricWake.MY_ACTION);   

		sendBroadcast(sender);
		/* ���X���x�aĵ�� */
		new AlertDialog.Builder(AlarmAlert.this)
				.setIcon(R.drawable.clock)
				.setTitle("�x���T�F!!")
				.setMessage("���ְ_�ɧa!!!")
				.setPositiveButton("�����L",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								Intent sender = new Intent();   
								sender.putExtra("state", 0);
								sender.setAction(ElectricWake.MY_ACTION);
								sendBroadcast(sender);
								/* ����Activity */
								AlarmAlert.this.finish();
							}
						}).show();

	}
}