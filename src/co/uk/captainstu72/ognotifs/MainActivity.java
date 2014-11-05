package co.uk.captainstu72.ognotifs;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private TextView txtView;
    private NotificationReceiver nReceiver;
    private DatabaseHelper mDB ;
    String[] values;
    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    NotificationManager nManager;
    
    final static String KEY_NOTIFICATION_ID = "NOTIF_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fab);
        
        nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mDB = new DatabaseHelper(this);
        
        txtView = (TextView) findViewById(R.id.textView);
        
        nReceiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("co.uk.captainstu72.ognotifs.NOTIFICATION_LISTENER_EXAMPLE");
        registerReceiver(nReceiver,filter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(nReceiver);
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
			case R.id.action_settings:
				break;
			case R.id.action_notification_settings:
				startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
				break;
			default:
				break;
		}

		return super.onOptionsItemSelected(item);
	}

    public void buttonClicked(View v) {
    	Intent i;    	
    	switch (v.getId()) {
	    	case R.id.btnCreateNotify:
	    		createNotification("My Notification","My Text", "My Ticker", R.drawable.ic_launcher, true, false);
	    		break;
	    	case R.id.btnClearNotify:
	            i = new Intent("co.uk.captainstu72.ognotifs.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
	            i.putExtra("command","clearall");
	            sendBroadcast(i);
	    		break;
	    	case R.id.btnListNotify:
	            i = new Intent("co.uk.captainstu72.ognotifs.NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
	            i.putExtra("command","list");
	            sendBroadcast(i);
	    		break;
	    	case R.id.fabAddNotification:
	    		//createNotification("My Ongoing Notification","My Text", "My Ticker", R.drawable.ic_launcher, false, true);
	    		startActivity(new Intent(this, NewNotifActivity.class)
	    			, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
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
        
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_NOTIFICATION_ID, notifId);
        intent.putExtras(bundle);
        ncomp.setExtras(bundle);
        PendingIntent activity = PendingIntent.getActivity(this, notifId, intent, 0);
        ncomp.setContentIntent(activity);
        Log.d("createNotification","KEY_NOTIFICATION_ID: " + notifId);
        nManager.notify(notifId,ncomp.build());
    }

    class NotificationReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String temp = intent.getStringExtra("notification_event") + "\n" + txtView.getText();
            txtView.setText(temp);
        }
    }



}
