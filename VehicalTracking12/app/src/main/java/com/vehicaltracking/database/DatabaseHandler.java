package com.vehicaltracking.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;
	// Database Name
	private static final String DATABASE_NAME = "vehicaltracking";

	// Tracking table name
	private static final String TABLE_Location = "location_table";
	// Tracking Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_Latitude = "Latitude";
	private static final String KEY_Longitude = "Longitude";
	public static DatabaseHandler mDatabaseHandler;


	public static DatabaseHandler getDatabaseInstance(Context mcontext)
	{
		if(mDatabaseHandler==null){
			mDatabaseHandler=new DatabaseHandler(mcontext);
		}
		return mDatabaseHandler;


	}


	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {

		String CREATE_TrackingTable = "CREATE TABLE " + TABLE_Location + "(" + KEY_ID
				+ " INTEGER PRIMARY KEY," + KEY_Latitude + " TEXT," + KEY_Longitude + " TEXT" + ")";
		db.execSQL(CREATE_TrackingTable);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_Location);
		// Create tables again
		onCreate(db);
	}

	public void addContactLocation(String Latitude,String Longitude) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_Latitude, Latitude);
		values.put(KEY_Longitude, Longitude);

		// Inserting Row
		db.insert(TABLE_Location, null, values);
		// Closing database connection
	}




	public void CloseDb() {

		if(mDatabaseHandler!=null){
			SQLiteDatabase db = this.getWritableDatabase();
			if(db.isOpen())
				db.close();
		}
	}




}