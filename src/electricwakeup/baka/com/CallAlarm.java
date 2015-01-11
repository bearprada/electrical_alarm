package electricwakeup.baka.com;

/* import����class */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/* �I�s�x��Alert��Receiver */
public class CallAlarm extends BroadcastReceiver
{
  @Override
  public void onReceive(Context context, Intent intent)
  {    
	  
	  /* create Intent�A�I�sAlarmAlert.class */
    Intent i = new Intent(context, AlarmAlert.class);
        
    Bundle bundleRet = new Bundle();
    bundleRet.putString("STR_CALLER", "");
    i.putExtras(bundleRet);
    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(i);      
  }
}