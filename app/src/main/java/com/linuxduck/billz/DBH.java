package com.linuxduck.billz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DBH extends SQLiteOpenHelper {
    String lock = "dblock";
    private static final Integer DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "billzdb";

    // Table Group which holds income total left
    private static final String TABLE_GROUP = "groupt"; //TEXT
    private static final String COL_GID = "gid"; //IntegerEGER
    private static final String COL_NAME = "name"; //TEXT
    private static final String COL_AMOUNT = "amount"; //REAL

    // Table Bill holds a bill that takes away from income
    private static final String TABLE_BILL = "bill";
    private static final String COL_BID = "bid"; //IntegerEGER
    // COL_NAME //TEXT
    // COL_AMOUNT //REAL
    private static final String COL_DELETED = "deleted";
    private static final String COL_DUE = "duedate";

    // Cross Reference helps tie the bills to the income
    // It also holds whether you have paid the bill or not
    private static final String TABLE_XREF = "xref";
    private static final String COL_XID = "xid"; //IntegerEGER
    private static final String COL_FGID = "fgid"; //IntegerEGER
    private static final String COL_FBID = "bgid"; //IntegerEGER
    private static final String COL_PAID = "paid"; //IntegerEGER

    public DBH(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createGroup =  "CREATE TABLE "  + TABLE_GROUP + "(" +
                COL_GID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_NAME +
                " TEXT, " + COL_AMOUNT + " REAL)";
        String createBill = "CREATE TABLE " + TABLE_BILL + "(" +
                COL_BID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_NAME +
                " TEXT, " + COL_AMOUNT + " REAL, " + COL_DELETED + " INTEGER, " + COL_DUE + " TEXT)";
        String createXREF = "CREATE TABLE " + TABLE_XREF + "(" +
                COL_XID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_FGID +
                " IntegerEGER, " + COL_FBID + " INTEGER, " +
                COL_PAID + " INTEGER)";
        db.execSQL(createGroup);
        db.execSQL(createBill);
        db.execSQL(createXREF);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_XREF);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BILL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUP);
        onCreate(db);
    }

    public GroupList getGroupList(Group group) {
        GroupList groupList = new GroupList();
        ArrayList<Bill> bills = getXREFBills(group.getGid());
        groupList = new GroupList(group, bills);
        return groupList;
    }

    public List<Group> getGroups() {
        List<Group> groups = new ArrayList<>();
        String getGroupsQ = "SELECT * FROM " + TABLE_GROUP;
        synchronized (lock){
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(getGroupsQ, null);

            if(cursor.moveToFirst()){
                do {
                    Group group = new Group(cursor.getInt(0),
                            cursor.getString(1),
                            cursor.getDouble(2));
                    groups.add(group);
                } while (cursor.moveToNext());
            }
            db.close();
        }
        return groups;
    }

    public ArrayList<GroupList> getGroupLists() {
        ArrayList<GroupList> groupLists = new ArrayList<>();
        List<Group> groups = new ArrayList<>();
        groups = getGroups();
        for(int i = 0; i < groups.size(); i++){
            groupLists.add(getGroupList(groups.get(i)));
        }
        return groupLists;
    }

    public List<Bill> getNonDeletedBills(){
        List<Bill>bills = new ArrayList<>();
        String getBillsQ = "SELECT * FROM " + TABLE_BILL + " WHERE " +
                COL_DELETED + "=0";
        synchronized (lock){
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(getBillsQ, null);

            if(cursor.moveToFirst()){
                do {
                    Bill bill = new Bill(cursor.getInt(0),
                            cursor.getString(1),
                            cursor.getDouble(2),
                            false,
                            cursor.getString(4));
                    bills.add(bill);
                } while (cursor.moveToNext());
            }
            db.close();
        }
        return bills;
    }

    public void addGroup(Group group){
        synchronized (lock){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_NAME, group.getName());
            values.put(COL_AMOUNT, group.getAmount());
            db.insert(TABLE_GROUP, null, values);
            db.close();
        }
    }

    public Long addBill(Bill bill){
        Long newrow = -1l;
        synchronized (lock){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_NAME, bill.getName());
            values.put(COL_AMOUNT, bill.getAmount());
            values.put(COL_DELETED, false);
            values.put(COL_DUE, bill.getDuedate());
            newrow = db.insert(TABLE_BILL, null, values);
            db.close();
        }
        return newrow;
    }

    public void addXREF(Integer group_id, Integer bill_id) {
        synchronized (lock){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_FGID, group_id);
            values.put(COL_FBID, bill_id);
            db.insert(TABLE_XREF, null, values);
            db.close();
        }
    }

    public void deleteGroup(Integer id){
        synchronized (lock){
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_GROUP, COL_GID + "=?",
                    new String[]{String.valueOf(id)});
            List<Integer> toDelete = getXREFBillIds(id);
            for (int i = 0; i < toDelete.size(); i++){
                deleteXREF(toDelete.get(i));
            }
            db.close();
        }
    }

    public void deleteBill(Integer id){
        synchronized (lock){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_DELETED, 1);
            db.update(TABLE_BILL, values, COL_BID + " = " + id,
                    null);
            db.close();
        }
    }

    public ArrayList<Bill> getXREFBills(Integer group_id) {
        SimpleDateFormat sdp = new SimpleDateFormat("yyyy.MM.dd");
        ArrayList<Bill> bills = new ArrayList<>();
        synchronized (lock){
            SQLiteDatabase db = this.getWritableDatabase();

            String getBillsQ = "SELECT " + COL_BID + ", " + COL_NAME + ", " +
                    COL_AMOUNT + ", " + COL_DUE + " FROM " + TABLE_BILL + " JOIN " + TABLE_XREF + " ON " +
                    COL_BID + "=" + COL_FBID + " WHERE " + group_id + "=" + COL_FGID;

            Cursor cursor = db.rawQuery(getBillsQ, null);
            if(cursor.moveToFirst()){
                do {
                    Bill bill = new Bill(
                            cursor.getInt(0),
                            cursor.getString(1),
                            cursor.getDouble(2),
                            false,
                            cursor.getString(3)
                    );
                    bills.add(bill);
                } while (cursor.moveToNext());
            }
            db.close();
        }
        return bills;
    }

    public void deleteXREF(Integer id){
        synchronized (lock){
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_XREF, COL_XID + "=?",
                    new String[]{String.valueOf(id)});
            db.close();
        }
    }

    public void deleteXREF(Integer group_id, Integer bill_id){
        synchronized (lock){
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_XREF,
                    COL_FGID + "=" + group_id + " AND " + COL_FBID + "=" + bill_id,
                    null);
            db.close();
        }
    }

    public List<Integer> getXREFBillIds(Integer id){
        List<Integer> ids = new ArrayList<>();
        synchronized (lock){
            SQLiteDatabase db = this.getWritableDatabase();
            String getFBIDsQ = "SELECT " + COL_FBID + " FROM "
                    + TABLE_XREF + " WHERE " + COL_FGID + " = " + String.valueOf(id);

            Cursor cursor = db.rawQuery(getFBIDsQ, null);

            if(cursor.moveToFirst()){
                do {
                    Integer temp = cursor.getInt(0);
                    ids.add(temp);
                } while (cursor.moveToNext());
            }
            db.close();
        }
        return ids;
    }
}