package com.abclab.abcereports;

import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DBTestInfo extends SQLiteOpenHelper {
	private static abstract class TestVersionSchema implements BaseColumns{
		private static final String TABLE_NAME = "TstVer";
		public static final String BRANCH_ID = "TstVerBranchId";
		public static final String VALUE = "TstVerValue";
	}
	private static abstract class TestListSchema implements BaseColumns{
		public static final String TABLE_NAME = "TstList";
		public static final String BRANCH_ID = "TstLstBranchId";
		public static final String CODE = "TstLstCode";
		public static final String NAME = "TstLstName";
	}
	private static abstract class TestDetailsSchema implements BaseColumns{
		public static final String TABLE_NAME = "TestDetails";
		public static final String BRANCH_ID = "TstDetBranchId";
		public static final String CODE = "TstDetCode";
		public static final String NAME = "TstDetName";
		public static final String DESCRIPTION = "TstDetDescription";
		public static final String SPECIMEN = "TstDetSpecimen";
		public static final String PREPARATION = "TstDetPreparation";
		public static final String RUNNING = "TstDetRunning";
		public static final String TAT = "TstDetTAT";
	}
	public static class TestListData{
		public String Code;
		public String Name;
		public TestListData(){}
	}
	public static class TestDetailsData{
		public String Code;
		public String Name;
		public String Description;
		public String Specimen;
		public String Preparation;
		public String Running;
		public String TAT;
		public TestDetailsData(){}
	}
	private static final String SQL_CREATE_TEST_VERSION =
		    "CREATE TABLE " + TestVersionSchema.TABLE_NAME + " (" +
		    		TestVersionSchema.BRANCH_ID + " INTEGER," +
		    		TestVersionSchema.VALUE + " TEXT" +
		    " )";
	private static final String SQL_DELETE_TEST_VERSION =
		    "DROP TABLE IF EXISTS " + TestVersionSchema.TABLE_NAME;
	
	private static final String SQL_CREATE_TEST_LIST =
		    "CREATE TABLE " + TestListSchema.TABLE_NAME + " (" +
		    		TestListSchema.BRANCH_ID + " INTEGER," +
		    		TestListSchema.CODE + " TEXT," +
		    		TestListSchema.NAME + " TEXT" +
		    " )";
	private static final String SQL_DELETE_TEST_LIST =
		    "DROP TABLE IF EXISTS " + TestListSchema.TABLE_NAME;
	
	private static final String SQL_CREATE_TEST_DETAILS =
		    "CREATE TABLE " + TestDetailsSchema.TABLE_NAME + " (" +
		    		TestDetailsSchema.BRANCH_ID + " INTEGER," +
		    		TestDetailsSchema.CODE + " TEXT," +
		    		TestDetailsSchema.NAME + " TEXT," +
		    		TestDetailsSchema.DESCRIPTION + " TEXT," +
		    		TestDetailsSchema.SPECIMEN + " TEXT," +
		    		TestDetailsSchema.PREPARATION + " TEXT," +
		    		TestDetailsSchema.RUNNING + " TEXT," +
		    		TestDetailsSchema.TAT + " TEXT" +
		    " )";
	private static final String SQL_DELETE_TEST_DETAILS =
		    "DROP TABLE IF EXISTS " + TestDetailsSchema.TABLE_NAME;
	
	private void createDB(SQLiteDatabase db){
        db.execSQL(SQL_CREATE_TEST_VERSION);
        db.execSQL(SQL_CREATE_TEST_LIST);
        db.execSQL(SQL_CREATE_TEST_DETAILS);
	}
	private void deleteDB(SQLiteDatabase db) {
		db.execSQL(SQL_DELETE_TEST_VERSION);
		db.execSQL(SQL_DELETE_TEST_LIST);
		db.execSQL(SQL_DELETE_TEST_DETAILS);
	}
	private void recreateDB(SQLiteDatabase db) {
		deleteDB(db);
		createDB(db);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
        createDB(db);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		recreateDB(db);
	}

	public DBTestInfo(Context context) {
        super(context, DBInfo.DB_NAME, null, DBInfo.DB_VERSION);
    }
	
	public void setVersion(int branch,int ver){
		SQLiteDatabase db = getWritableDatabase();

		db.delete(TestVersionSchema.TABLE_NAME, null, null);
		
		ContentValues values = new ContentValues();
		values.put(TestVersionSchema.BRANCH_ID, branch);
		values.put(TestVersionSchema.VALUE, ver);
		db.insert(TestVersionSchema.TABLE_NAME,"",values);
	}
	public int getVersion(int branch){
		int retVal = 0;
		SQLiteDatabase db = getReadableDatabase();
		String[] cols = { 
			TestVersionSchema.VALUE
		};
		Cursor c = db.query(TestVersionSchema.TABLE_NAME, cols, null, null, null, null, null);

		if (c.getCount()>0) {
			c.moveToFirst();
			retVal = c.getInt(0);
		}
        c.close();
        return retVal;
	}
	public void clearList(int branch){
		SQLiteDatabase db = getWritableDatabase();
		db.delete(TestListSchema.TABLE_NAME, TestListSchema.BRANCH_ID + "=" + branch, null);
		db.delete(TestDetailsSchema.TABLE_NAME, TestDetailsSchema.BRANCH_ID + "=" + branch, null);
	}
	public void addTestList(int branch, String code, String name) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(TestListSchema.BRANCH_ID, branch);
		values.put(TestListSchema.CODE, code);
		values.put(TestListSchema.NAME, name);
		
		db.insert(TestListSchema.TABLE_NAME,"",values);
	}
	public ArrayList<TestListData> getList(int branch){
		ArrayList<TestListData> retVal = new ArrayList<TestListData>();
		SQLiteDatabase db = getReadableDatabase();
		String[] cols = { 
			TestListSchema.CODE,
			TestListSchema.NAME
		};
		String sel = TestListSchema.BRANCH_ID + "=?";
		Cursor c = db.query(TestListSchema.TABLE_NAME, cols, sel, new String[] { String.valueOf(branch) }, null, null, null);
		c.moveToFirst();
		while (!c.isAfterLast()) {
			TestListData d =  new TestListData();
			d.Code = c.getString(0);
			d.Name = c.getString(1);
			retVal.add(d);
			c.moveToNext();
		}
        c.close();
        return retVal;
	}
	public TestDetailsData getDetails(int branch, String code){
		TestDetailsData retVal = null;
		SQLiteDatabase db = getReadableDatabase();
		String[] cols = { 
			TestDetailsSchema.CODE,
			TestDetailsSchema.NAME,
			TestDetailsSchema.DESCRIPTION,
			TestDetailsSchema.SPECIMEN,
			TestDetailsSchema.PREPARATION,
			TestDetailsSchema.RUNNING,
			TestDetailsSchema.TAT
		};
		String sel = TestDetailsSchema.BRANCH_ID + "=? and " + TestDetailsSchema.CODE + "=?";
		Cursor c = db.query(TestDetailsSchema.TABLE_NAME, cols, sel, new String[]{String.valueOf(branch), code}, null, null, null);

		if (c.getCount()>0) {
			c.moveToFirst();
			retVal = new TestDetailsData();
			retVal.Code = c.getString(0);
			retVal.Name = c.getString(1);
			retVal.Description = c.getString(2);
			retVal.Specimen = c.getString(3);
			retVal.Preparation = c.getString(4);
			retVal.Running = c.getString(5);
			retVal.TAT = c.getString(6);
		}
        c.close();
        return retVal;
	}
	public void setDetails(int branch, TestDetailsData data) {
		SQLiteDatabase db = getWritableDatabase();
		
		String sel = TestDetailsSchema.BRANCH_ID + "=? and " + TestDetailsSchema.CODE + "=?";
		db.delete(TestDetailsSchema.TABLE_NAME, sel, new String[]{ String.valueOf(branch),data.Code});
		
		ContentValues values = new ContentValues();
		values.put(TestDetailsSchema.BRANCH_ID, branch);
		values.put(TestDetailsSchema.CODE, data.Code);
		values.put(TestDetailsSchema.NAME, data.Name);
		values.put(TestDetailsSchema.DESCRIPTION, data.Description);
		values.put(TestDetailsSchema.SPECIMEN, data.Specimen);
		values.put(TestDetailsSchema.PREPARATION, data.Preparation);
		values.put(TestDetailsSchema.RUNNING, data.Running);
		values.put(TestDetailsSchema.TAT, data.TAT);
		
		db.insert(TestDetailsSchema.TABLE_NAME,"",values);
	}
}
