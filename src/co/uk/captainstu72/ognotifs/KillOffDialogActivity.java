/**
 * 
 */
package co.uk.captainstu72.ognotifs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * @author stuarta
 *
 */
public class KillOffDialogActivity extends Activity {
	
	private NotificationManager nManager;
	private DatabaseHelper mDB;
	final static String KEY_NOTIFICATION_ID = "NOTIF_ID";

	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mDB = new DatabaseHelper(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();        
		processIntent(getIntent());    
	}    

	@Override
	protected void onNewIntent(Intent intent) {     
		//processIntent(intent);
	};

	private void processIntent(Intent intent){
		if (intent.getExtras() != null) {
			int notifId = getIntent().getExtras().getInt(KEY_NOTIFICATION_ID);
			Log.d("onResume","KEY_NOTIFICATION_ID:" + notifId);
			if (notifId > 0 ) {
				clearNotification(notifId, this);
			}
		}
	}
	
	private void clearNotification(final int notifId, final Context c) {
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which){
					case DialogInterface.BUTTON_POSITIVE:
						//Yes button clicked
						nManager.cancel(notifId);
						Log.d("clearNotification","Notification ID: " + notifId);
						Toast.makeText(c, "notifId:" + notifId, Toast.LENGTH_SHORT).show();
						Toast.makeText(c, "Yes", Toast.LENGTH_SHORT).show();
						
						mDB.deleteNotif(notifId);
						finish();
						
					break;
					
					case DialogInterface.BUTTON_NEGATIVE:
						//No button clicked
						finish();
					break;
					}
				}
			};
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Kill this notification? psss This is from KillOff").setPositiveButton("Yes", dialogClickListener)
		.setNegativeButton("No", dialogClickListener).show();
	}

}
