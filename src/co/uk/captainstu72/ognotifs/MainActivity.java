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
import android.database.Cursor;
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
	    	case R.id.btnRestoreNotifs:
	    		//restore all notifications stored in the database
	    		restoreOngoing();
	    		break;
    	}
    	
    }
    
    public void restoreOngoing() {
    	//This will restore the stored notifications from the database. Perhaps this could be run on phone startup
    	ArrayList<Integer> alNotifs = mDB.getAllNotifs();
    	for (int id : alNotifs) {
    		Cursor rs = mDB.getNotif(id);
    		rs.moveToFirst();
    		restoreNotification(
    				rs.getInt(rs.getColumnIndex(DatabaseHelper.colNotifID))
    			, rs.getString(rs.getColumnIndex(DatabaseHelper.colTitle))
    			, rs.getString(rs.getColumnIndex(DatabaseHelper.colText))
    			, rs.getString(rs.getColumnIndex(DatabaseHelper.colTicker))
    			, rs.getInt(rs.getColumnIndex(DatabaseHelper.colIcon))
    			, false
    			, true); //Could call ongoing, but we are only storing ongoing.
    		
    		if (!rs.isClosed()) 
            {
               rs.close();
            }
    		
    	}
    		
    }
    
    public void restoreNotification(int id,String title, String text, String ticker, int icon, boolean autocancel, boolean ongoing) {
        Notification.Builder ncomp = new Notification.Builder(this);
        
        Intent killIntent = new Intent(this, KillOffDialogActivity.class);
        killIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_NOTIFICATION_ID, id);
        killIntent.putExtras(bundle);
        ncomp.setExtras(bundle);
        PendingIntent killActivity = PendingIntent.getActivity(this, id, killIntent, 0);
        
//        Intent intentTestHangouts = new Intent(Intent.ACTION_SEND);
//        PendingIntent hangoutsActivity = PendingIntent.getActivity(this, id, intentTestHangouts, 0);
		
		ncomp.setContentTitle(title)
		.setContentText(text)
		.setTicker(ticker)
		.setSmallIcon(R.drawable.ic_done_24px)
		.setColor(this.getResources().getColor(R.color.md_primary_500))
		.setAutoCancel(autocancel)
		.setOngoing(ongoing)
		.addAction(R.drawable.ic_done_24px, "Done", killActivity)
		.addAction(R.drawable.ic_textsms_24px, "Open", killActivity);
		
        //ncomp.setContentIntent(activity);
        Log.d("createNotification","KEY_NOTIFICATION_ID: " + id);
        nManager.notify(id,ncomp.build());
    }

    class NotificationReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String temp = intent.getStringExtra("notification_event") + "\n" + txtView.getText();
            txtView.setText(temp);
        }
    }



}
