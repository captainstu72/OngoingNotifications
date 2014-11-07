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
import android.widget.EditText;

public class NewNotifActivity extends Activity {
	
    private NotificationManager nManager;
    private DatabaseHelper mDB;
    private EditText etTitle;
    private EditText etText;
    
    final static String KEY_NOTIFICATION_ID = "NOTIF_ID";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_notif);
        nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mDB = new DatabaseHelper(this);
        etTitle = (EditText) findViewById(R.id.etTitle);
        etText = (EditText) findViewById(R.id.etText);
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
	    		createNotification(
		    				  etTitle.getText().toString()
		    				, etText.getText().toString()
		    				, "My Ticker"
		    				, R.drawable.ic_done_24px
		    				, false, ongoing
	    				);
	    		finish();
	    		break;
    	}
    }
	
	public void createNotification(String title, String text, String ticker, int icon, boolean autocancel, boolean ongoing) {
		int notifId = (int) System.currentTimeMillis();
		Notification.Builder ncomp = new Notification.Builder(this);
		
		Intent killInent = new Intent(this, KillOffDialogActivity.class);
		killInent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Bundle bundle = new Bundle();
		bundle.putInt(KEY_NOTIFICATION_ID, notifId);
		killInent.putExtras(bundle);
		ncomp.setExtras(bundle);
		PendingIntent killActivity = PendingIntent.getActivity(this, notifId, killInent, 0);
		 
		Log.d("createNotification","KEY_NOTIFICATION_ID: " + notifId);
		
		ncomp.setContentTitle(title)
		.setContentText(text)
		.setTicker(ticker)
		.setSmallIcon(icon)
		.setColor(this.getResources().getColor(R.color.md_primary_500))
		.setAutoCancel(autocancel)
		.setOngoing(ongoing)
		.addAction(R.drawable.ic_done_24px, "Done", killActivity); //If we wanted to add a done button,
		// we could also add an action to open an app...

		//ncomp.setContentIntent(activity);
		nManager.notify(notifId,ncomp.build());
		
		//insert into SQLite
		if (ongoing) {
			mDB.insertNotif(notifId, title, text, ticker, icon, ongoing);
			Log.d("createNotification", "table rowcount: " + String.valueOf(mDB.numberOfRows()));
		}
	}
}
