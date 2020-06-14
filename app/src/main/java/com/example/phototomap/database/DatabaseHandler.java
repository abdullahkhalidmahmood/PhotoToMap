package com.example.phototomap.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.phototomap.model.LocationModel;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "LocationDB";
    private static final String TABLE_NAME = "Location_Table";

    //Table columns
    private static final String KEY_ID = "id";
    private static final String KEY_LAT = "lat";
    private static final String KEY_LNG = "lng";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_IMG = "image";
    Context context;


    public DatabaseHandler(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create_table = "CREATE TABLE " + TABLE_NAME + "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_LAT + " TEXT, " + KEY_LNG + " TEXT, " +
                KEY_ADDRESS + " TEXT, " + KEY_IMG + " BLOB)";

        db.execSQL(create_table);
        System.out.println("Table created");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
        System.out.println("Table Dropped");

    }



    public boolean addLocation(String newLatEntry, String newLngEntry, String newAddressEntry, byte[] newImageEntry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_LAT, newLatEntry);
        contentValues.put(KEY_LNG, newLngEntry);
        contentValues.put(KEY_ADDRESS, newAddressEntry);
        contentValues.put(KEY_IMG, newImageEntry);
        db.insert(TABLE_NAME, null, contentValues);
        return true;
    }

    public ArrayList<LocationModel> getAllLocations() {
        ArrayList<LocationModel> locationModelList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                LocationModel locationModel = new LocationModel();
                locationModel.setId(cursor.getString(0));
                locationModel.setLat(cursor.getString(1));
                locationModel.setLng(cursor.getString(2));
                locationModel.setAddress(cursor.getString(3));
                locationModel.setImg(cursor.getBlob(4));
                locationModelList.add(locationModel);

            } while (cursor.moveToNext());
        }
        return locationModelList;
    }

    /*public boolean editLocation(String id, String newNameEntry, String newNumberEntry, String newEmailEntry, String newCategoryEntry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_LAT, newNameEntry);
        contentValues.put(KEY_LNG, newNumberEntry);
        contentValues.put(KEY_ADDRESS, newEmailEntry);
        contentValues.put(KEY_IMG, newCategoryEntry);
        db.update(TABLE_NAME, contentValues, "id=?", new String[]{id});
        db.close();
        return true;
    }

    public boolean addContact(String newNameEntry, String newNumberEntry, String newEmailEntry, String newCategoryEntry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_LAT, newNameEntry);
        contentValues.put(KEY_LNG, newNumberEntry);
        contentValues.put(KEY_ADDRESS, newEmailEntry);
        contentValues.put(KEY_URI, newCategoryEntry);
        db.insert(TABLE_NAME, null, contentValues);
        return true;
    }


    public ArrayList<ContactsModel> getAllContacts() {
        ArrayList<ContactsModel> contactsModelList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ContactsModel contactsModel = new ContactsModel();
                contactsModel.setId(cursor.getString(0));
                contactsModel.setName(cursor.getString(1));
                contactsModel.setMobileNumber(cursor.getString(2));
                contactsModel.setEmailAddress(cursor.getString(3));
                contactsModel.setCategory(cursor.getString(4));
                contactsModelList.add(contactsModel);

            } while (cursor.moveToNext());
        }
        return contactsModelList;
    }

    public int deleteContact(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, KEY_ID + "=?", new String[]{id});

    }*/

}
