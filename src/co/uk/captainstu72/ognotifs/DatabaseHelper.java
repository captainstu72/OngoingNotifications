/**
 * 
 */
package co.uk.captainstu72.ognotifs;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author stuarta
 *
 */
public class DatabaseHelper extends SQLiteOpenHelper {
	
	static final String dbName="ognotifsDB.db";
	static final String notifTable="Notifications";
	static final String colID ="_id";
	static final String colNotifID ="notifID";
	static final String colTitle ="notifTitle";
	static final String colText ="notifText";
	static final String colTicker ="notifTicker";
	static final String colIcon ="notifIcon";
	static final String colOngoing = "notifOngoing";
	
	static final int DB_VERS = 1;

	public DatabaseHelper(Context context) {
		super(context, dbName, null, DB_VERS); 
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
		//create the data store table
		db.execSQL	("CREATE TABLE " + notifTable + " ("
						+ colID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
						+ colNotifID+ " INTEGER, "
						+ colTitle+ " TEXT, "
						+ colText+ " TEXT, "
						+ colTicker+ " TEXT, "
						+ colIcon+ " INTEGER ,"
						+ colOngoing + " INTEGER "				
						+ ")"
					);		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
		// Write this if we ever change the structure
		// Appending new fields by version code most likely. 
		// guides tend to say delete the table and recreate the DB. Just no.
		
		// use the versions to work out what changes to make
		
	}
	

	public int numberOfRows(){
		SQLiteDatabase db = this.getReadableDatabase();
		int numRows = (int) DatabaseUtils.queryNumEntries(db, notifTable);
		db.close();
		return numRows;
	}
	

	public Cursor getNotif(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor res =  db.rawQuery( "SELECT * FROM " + notifTable + " WHERE " + colNotifID + " = " + id, null );
//		db.close();
		return res;
	}
	
	public ArrayList<Integer> getAllNotifs() {
		ArrayList<Integer> array_list = new ArrayList<Integer>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor res =  db.rawQuery( "SELECT " + colNotifID + " FROM " + notifTable, null );
		res.moveToFirst();
		while(res.isAfterLast() == false) {
			array_list.add(res.getInt(res.getColumnIndex(colNotifID)));
			res.moveToNext();
		}
//		db.close();
		return array_list;
	}
	
	//
	public boolean insertNotif(int id, String title, String text, String ticker, int icon, boolean ongoing) {
		//connect to the database writeable as we are inserting
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		
		cv.put(colNotifID, id);
		cv.put(colTitle, title);
		cv.put(colText, text);
		cv.put(colTicker, ticker);
		cv.put(colIcon, icon);
		cv.put(colOngoing, ongoing);
		
		db.insert(notifTable, null, cv);
		
		//always close when done
		db.close();
		return true;
	}
	
	public Integer deleteNotif(int id) {
		//connect to the database writeable as we are deleting
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(notifTable, colNotifID + " = ?", new String[] { Integer.toString(id) });
		db.close();
		return id;		
	}

}
