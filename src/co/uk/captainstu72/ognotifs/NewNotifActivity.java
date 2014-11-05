package co.uk.captainstu72.ognotifs;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

public class NewNotifActivity extends Activity {
	
    private NotificationManager nManager;
    private DatabaseHelper mDB ;
    
    final static String KEY_NOTIFICATION_ID = "NOTIF_ID";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_notif);
        nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mDB = new DatabaseHelper(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_notif, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
    public void buttonClicked(View v) {
    	switch (v.getId()) {
	    	case R.id.fabAddNotification:
	    		boolean ongoing = ((CheckBox) findViewById(R.id.cbPersistent)).isChecked();
	    		createNotification("My Notification","My Text", "My Ticker", R.drawable.ic_launcher, false, ongoing);
	    		break;
    	}
    }
	
	public void createNotification(String title, String text, String ticker, int icon, boolean autocancel, boolean ongoing) {
		int notifId = (int) System.currentTimeMillis();
		Notification.Builder ncomp = new Notification.Builder(this);
		ncomp.setContentTitle(title);
		ncomp.setContentText(text);
		ncomp.setTicker(ticker);
		ncomp.setSmallIcon(icon);
		ncomp.setAutoCancel(autocancel);
		ncomp.setOngoing(ongoing);
		
		Intent intent = new Intent(this, KillOffDialogActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Bundle bundle = new Bundle();
		bundle.putInt(KEY_NOTIFICATION_ID, notifId);
		intent.putExtras(bundle);
		ncomp.setExtras(bundle);
		PendingIntent activity = PendingIntent.getActivity(this, notifId, intent, 0);
		ncomp.setContentIntent(activity);
		Log.d("createNotification","KEY_NOTIFICATION_ID: " + notifId);
		nManager.notify(notifId,ncomp.build());
		
		//insert into SQLite
		if (ongoing) {
			mDB.insertNotif(notifId, title, text, ticker, icon, ongoing);
			Log.d("createNotification", "table rowcount: " + String.valueOf(mDB.numberOfRows()));
		}
	}
}
