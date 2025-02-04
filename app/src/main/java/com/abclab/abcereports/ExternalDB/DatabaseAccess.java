package com.abclab.abcereports.ExternalDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase db;
    private static DatabaseAccess instance;
    Cursor c = null;

    //private constructor so that object creation from outside the class is avoided
    private DatabaseAccess(Context context){
        this.openHelper = new DatabaseOpenHelper(context);
    }

    //to return the single instance of database

    public static DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    //to open the database


    public void open() {
        SQLiteDatabase db = openHelper.getWritableDatabase();
    }

    //closing the database connection

    public void close() {
        if (db != null) {
            this.db.close();
        }
    }


    //strings used in the database
    private static abstract class TestVersionSchema implements BaseColumns {
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


    //now lets create a method to query and return the result from database
    public void setVersion(int branch,int ver){
        SQLiteDatabase db = openHelper.getWritableDatabase();

        db.delete(TestVersionSchema.TABLE_NAME, null, null);

        ContentValues values = new ContentValues();
        values.put(TestVersionSchema.BRANCH_ID, branch);
        values.put(TestVersionSchema.VALUE, ver);
        db.insert(TestVersionSchema.TABLE_NAME,"",values);
    }
    public int getVersion(int branch){
        int retVal = 0;
        SQLiteDatabase db = openHelper.getWritableDatabase();
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
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.delete(TestListSchema.TABLE_NAME, TestListSchema.BRANCH_ID + "=" + branch, null);
        db.delete(TestDetailsSchema.TABLE_NAME, TestDetailsSchema.BRANCH_ID + "=" + branch, null);
    }
    public void addTestList(int branch, String code, String name) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TestListSchema.BRANCH_ID, branch);
        values.put(TestListSchema.CODE, code);
        values.put(TestListSchema.NAME, name);

        db.insert(TestListSchema.TABLE_NAME,"",values);
    }

    public ArrayList<TestListData> getList(int branch){
        ArrayList<TestListData> retVal = new ArrayList<TestListData>();
        SQLiteDatabase db = openHelper.getWritableDatabase();
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
        SQLiteDatabase db = openHelper.getWritableDatabase();
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
        SQLiteDatabase db = openHelper.getWritableDatabase();

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
